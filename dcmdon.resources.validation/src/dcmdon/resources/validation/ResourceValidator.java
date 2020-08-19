package dcmdon.resources.validation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dcmdon.resources.validation.model.Configuration;
import dcmdon.resources.validation.model.file.Constant;
import dcmdon.resources.validation.model.file.java.Interfaces;
import dcmdon.resources.validation.model.file.java.InterfaceConstantRecognizer;
import dcmdon.resources.validation.model.file.xml.IdParameterRecognizer;

public class ResourceValidator
{
	public static final int OK_RESULT_CODE = 0;
	public static final int ERROR_RESULT_CODE = 1;
	
	public static final String INFO = "[INFO]\t";
	public static final String WARNING = "[WARN]\t";
	public static final String ERROR = "[ERR!]\t";
	
	public static final String ERROR_MESSAGE_END = " в конфигурационном файле.";
	
	private final String m_noErrMessage = "Ошибки не обнаружены.";
	
	private final String m_fileNotFoundMessage = "Файл не найден.";
	
	private String m_configFilePath;
	
	private Configuration m_configuration;
	
	private StringBuilder m_reportBuilder = new StringBuilder();
	
	private int m_resultCode = OK_RESULT_CODE;
	
	private List<Short> m_allResourceInterfaceConstantValues = new ArrayList<>();
	private List<Short>  m_allPropertyInterfaceConstantValues = new ArrayList<>();
	
	public ResourceValidator (String a_configFilePath)
	{
		 m_configFilePath = a_configFilePath;
	}
	
	private void validateAllResources () throws UnsupportedEncodingException, FileNotFoundException, IOException, ParserConfigurationException, SAXException
	{
		m_configuration = readConfiguration();
		validateInterfaces();
		validateXmlFiles();
	}
	
	private void validateInterfaces () throws NumberFormatException, NoSuchElementException, IOException
	{
		InterfaceConstantRecognizer interfaceConstRecognizer = new InterfaceConstantRecognizer();
		Interfaces[] interfaces = getInterfacesForValidation();
		
		for (Interfaces i : interfaces)
		{
			validateInterfaceConstants (interfaceConstRecognizer, i);
		}
	}
	
	private void validateInterfaceConstants (InterfaceConstantRecognizer a_interfaceConstRecognizer,
											 Interfaces a_interfaces) throws NumberFormatException,
																             FileNotFoundException, 
																             NoSuchElementException
	{
		String interfaceType = a_interfaces.getType();
		
		Map<Short, List<String>> allowedEqualConsts = a_interfaces.getAllowedEqualConstNamesByValue();
		
		Map<String, Constant> interfaceConstByName = new HashMap<>();
		
		for (String interfacePath : a_interfaces.getPaths())
		{
			if (!writeFileExistingIntoReport(interfacePath)) continue;
			List<Constant> interfaceConstants = a_interfaceConstRecognizer.getConstants(interfaceType, interfacePath);
		
			boolean validConstant;
			boolean errorsExist = false;
			
			for (Constant c : interfaceConstants)
			{
				String name = c.getName();
				Short value = Short.valueOf(c.getValue());
				validConstant = true;
				for (Entry<String, Constant> entry : interfaceConstByName.entrySet())
				{
					String entryConstName = entry.getKey();
					Constant entryConst = entry.getValue();
					Short entryConstValue = Short.valueOf(entryConst.getValue());
					
					if (value.equals(entryConstValue))
					{
						if (allowedEqualConsts.containsKey(value))
						{
							List<String> allowedEqualNames = allowedEqualConsts.get(entryConstValue);
							if (!allowedEqualNames.contains(name) || !allowedEqualNames.contains(entryConstName))
							{
								validConstant = false;
								errorsExist = true;
								writeErrorConstIntoReport(c, entryConst);
							}
						}
						else
						{
							validConstant = false;
							errorsExist = true;
							writeErrorConstIntoReport(c, entryConst);
						}
					}
				}
				if (validConstant)
				{
					interfaceConstByName.put(name, c);
						
					if (interfaceType.equals(Interfaces.RESOURCE_TYPE))
					{
						m_allResourceInterfaceConstantValues.add(value);
					}
					if (interfaceType.equals(Interfaces.PROPERTY_TYPE))
					{
						m_allPropertyInterfaceConstantValues.add(value);
					}
				}
			}
			if (!errorsExist) writeMessageIntoReport(INFO, m_noErrMessage);
		}
	}

	private Interfaces[] getInterfacesForValidation () throws IOException
	{
		writeMessageIntoReport(INFO, "Проверка интерфейсов...");
		return m_configuration.getInterfaces();
	}
	
	private Configuration readConfiguration () throws IOException
	{
		try (BufferedReader reader = new BufferedReader (new InputStreamReader(new FileInputStream(m_configFilePath), "UTF-8")))
		{
			StringBuilder stringBuilder = new StringBuilder();
			String line = reader.readLine();
			while (line != null)
			{
				stringBuilder.append(line);
				line = reader.readLine();
			}
			String result = stringBuilder.toString();
			Gson gson = new GsonBuilder().create();
			return gson.fromJson(result, Configuration.class);
		}
	}
	
	private boolean writeFileExistingIntoReport (String a_filePath)
	{
		writeMessageIntoReport(INFO, a_filePath + ":");
		File javaFile = new File(a_filePath);
		if (!javaFile.exists())
		{
			writeMessageIntoReport(ERROR, m_fileNotFoundMessage);
			return false;
		}
		return true;
	}
	
	private void writeNotExistingFileIntoReport (File a_file)
	{
		if (!a_file.exists())
		{
			writeMessageIntoReport(INFO, a_file.getAbsolutePath() + ":");
			writeMessageIntoReport(ERROR, m_fileNotFoundMessage);
		}
	}
	
	private void writeMessageIntoReport (String a_prefix, String a_message)
	{
		if (a_prefix.equals(ERROR)) m_resultCode = ERROR_RESULT_CODE;
		m_reportBuilder.append(a_prefix + a_message + System.lineSeparator());
	}
	
	private void writeErrorConstIntoReport (Constant a_errorConst,
											Constant a_equalConst)
	{
		writeMessageIntoReport(ERROR, "Константа " + a_errorConst.getName() +
							   " = " + a_errorConst.getValue() +
							   " равна константе " + a_equalConst.getName() +
							   " (" + a_equalConst.getInterfacePath() + ")");
	}
	
	private void validateXmlFiles () throws ParserConfigurationException, SAXException, IOException
	{
		IdParameterRecognizer tagRecognizer = new IdParameterRecognizer();
		
		List<File> xmlFilesForValidation = getXmlFilesForValidation();
		if (xmlFilesForValidation.size() == 0)
		{
			writeMessageIntoReport(WARNING, "Не указаны файлы для проверки" + ERROR_MESSAGE_END);
		}
		for (File xmlFile : xmlFilesForValidation)
		{
			String path = xmlFile.getAbsolutePath();
			
			writeMessageIntoReport(INFO, path + ":");
			
			List<Constant> resourcePars = tagRecognizer.getConstants(Interfaces.RESOURCE_TYPE, path);
			List<Constant> propertyPars = tagRecognizer.getConstants(Interfaces.PROPERTY_TYPE, path);
			
			if (!(!checkXmlParameters(resourcePars, m_allResourceInterfaceConstantValues) ||
				!checkXmlParameters(propertyPars,m_allPropertyInterfaceConstantValues)))
			{
				writeMessageIntoReport(INFO, m_noErrMessage);
			}
		}
	}
	
	private List<File> getXmlFilesForValidation () throws IOException
	{
		writeMessageIntoReport(INFO, "Проверка xml-файлов...");
		
		List<File> result = new ArrayList<>();
		
		List<File> uniqueXmlFiles = getUniqueXmlFilesForValidation(m_configuration.
																   getXmlFilePaths());
		for (File xmlFile : uniqueXmlFiles)
		{
			if (xmlFile.isDirectory())
			{
				result.addAll(getXmlFiles(xmlFile));
			}
			else
			{
				result.add(xmlFile);
			}
		}
		return result;
	}
	
	private List<File> getUniqueXmlFilesForValidation (String[] a_xmlFilePaths)
	{
		List<File> result = new ArrayList<>();
		addXmlFilesToList(result, a_xmlFilePaths);
		
		List<File> filesForRemoving = getXmlFilesForRemoving(result);
		for (File file : filesForRemoving)
		{
			result.remove(file);
		}
		
		return result;
	}
	
	private void addXmlFilesToList (List<File> result, String[] a_xmlFilePaths)
	{
		for (String path : a_xmlFilePaths)
		{
			File file = new File(path);
			
			writeNotExistingFileIntoReport(file);
			
			if (file.isFile())
			{
				addXmlFileToList(file, result);
			}
			if (file.isDirectory())
			{
				result.add(file);
			}
		}
	}
	
	private List<File> getXmlFilesForRemoving (List<File> a_xmlFiles)
	{
		List<File> filesForRemoving = new ArrayList<>();
		
		int size = a_xmlFiles.size();
		for (int i = 0; i < size - 1; i++)
		{
			for (int j = i + 1; j < size; j++)
			{
				File file1 = a_xmlFiles.get(i);
				File file2 = a_xmlFiles.get(j);
				
				String path1 = file1.getAbsolutePath();
				String path2 = file2.getAbsolutePath();
				
				if (path1.contains(path2))
				{
					if (!filesForRemoving.contains(file1))
					{
						filesForRemoving.add(file1);
					}
				}
				else
				if (path2.contains(path1))
				{
					if (!filesForRemoving.contains(file2))
					{
						filesForRemoving.add(file2);
					}
				}
			}
		}
		return filesForRemoving;
	}
	
	private List<File> getXmlFiles (File a_parent)
	{
		List<File> files = new ArrayList<>();

		if (!a_parent.isDirectory())
		{
			addXmlFileToList(a_parent, files);
			return files;
		}

		for (File f : a_parent.listFiles())
		{
			if (f.isDirectory())
			{
				files.addAll(getXmlFiles(f));
			}
			else
			{
				addXmlFileToList(f, files);
			}
		}
		return files;
	}
	
	private void addXmlFileToList (File a_file, List<File> a_list)
	{
		if (a_file.getName().endsWith(".xml"))
		{
			a_list.add(a_file);
		}
	}
	
	private boolean checkXmlParameters (List<Constant> a_xmlParameters,
									    List<Short> a_interfaceConstantValues)
	{
		boolean result = true;
		for (Constant par : a_xmlParameters)
		{
			short value = par.getValue();
			if (!a_interfaceConstantValues.contains(value))
			{
				result = false;
				writeMessageIntoReport(ERROR, "Строка: " + par.getLineNumber() +
									   ". Столбец: " + par.getColumnNumber() +
									   ". Параметр " + value + " атрибута "
									   + par.getName() +" тега " + par.getType() +
									   " не найден в константах соответствующих " +
									   "интерфейсов");
			}
		}
		return result;
	}
	
	public String validateAndGetReport () throws UnsupportedEncodingException, FileNotFoundException, IOException, ParserConfigurationException, SAXException
	{
		validateAllResources();
		return m_reportBuilder.toString();
	}
	
	public int getValidationResultCode ()
	{
		return m_resultCode;
	}
}
