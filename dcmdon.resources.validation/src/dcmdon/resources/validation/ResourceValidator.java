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
	public static final String ERROR = "[ERR!]\t";
	
	private final String m_noErrMessage = "Ошибки не обнаружены.";
	
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
		Map<Short, List<String>> allowedEqualConsts = a_interfaces.getAllowedEqualConstNamesByValue();
		
		Map<String, Short> interfaceConstValueByName = new HashMap<>();
		
		String interfaceType = a_interfaces.getType();
		
		for (String interfacePath : a_interfaces.getPaths())
		{
			if (!isFileExists(interfacePath)) continue;
			List<Constant> interfaceConstants = a_interfaceConstRecognizer.getConstants(interfaceType, interfacePath);
		
			boolean validConstant;
			boolean errorsExist = false;
			
			for (Constant c : interfaceConstants)
			{
				String name = c.getName();
				Short value = Short.valueOf(c.getValue());
				validConstant = true;
				for (Entry<String, Short> entry : interfaceConstValueByName.entrySet())
				{
					String entryName = entry.getKey();
					if (entry.getValue().equals(value))
					{
						if (allowedEqualConsts.containsKey(value))
						{
							List<String> allowedEqualNames = allowedEqualConsts.get(value);
							if (!allowedEqualNames.contains(name) || !allowedEqualNames.contains(entryName))
							{
								validConstant = false;
								errorsExist = true;
								writeErrorConstIntoReport(name, value.shortValue(),
														  entryName);
							}
						}
						else
						{
							validConstant = false;
							errorsExist = true;
							writeErrorConstIntoReport(name, value.shortValue(),
													  entryName);
						}
					}
				}
				if (validConstant)
				{
					interfaceConstValueByName.put(name, value);
						
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
	
	private boolean isFileExists (String a_filePath)
	{
		writeMessageIntoReport(INFO, a_filePath + ":");
		File javaFile = new File(a_filePath);
		if (!javaFile.exists())
		{
			writeMessageIntoReport(ERROR, "Файл не найден.");
			return false;
		}
		return true;
	}
	
	private void writeMessageIntoReport (String a_prefix, String a_message)
	{
		if (a_prefix.equals(ERROR)) m_resultCode = ERROR_RESULT_CODE;
		m_reportBuilder.append(a_prefix + a_message + System.lineSeparator());
	}
	
	private void writeErrorConstIntoReport (String a_errorConstName,
											short a_errorConstValue,
											String a_equalConstName)
	{
		writeMessageIntoReport(ERROR, "Значение " + a_errorConstValue +
							   " константы " + a_errorConstName +
		        			   " равно значению константы " + a_equalConstName);
	}
	
	private void validateXmlFiles () throws ParserConfigurationException, SAXException, IOException
	{
		IdParameterRecognizer tagRecognizer = new IdParameterRecognizer();
		
		String[] xmlFilePaths = getXmlFilesForValidation();
		for (String path : xmlFilePaths)
		{
			if (!isFileExists(path)) continue;
			
			List<Constant> resourcePars = tagRecognizer.getConstants(Interfaces.RESOURCE_TYPE, path);
			List<Constant> propertyPars = tagRecognizer.getConstants(Interfaces.PROPERTY_TYPE, path);
			
			if (!checkXmlParameters(resourcePars, m_allResourceInterfaceConstantValues) ||
				!checkXmlParameters(propertyPars, m_allPropertyInterfaceConstantValues))
			{
				writeMessageIntoReport(INFO, m_noErrMessage);
			}
		}
	}
	
	private String[] getXmlFilesForValidation () throws IOException
	{
		writeMessageIntoReport(INFO, "Проверка xml-файлов...");
		return m_configuration.getXmlFilePaths();
	}
	
	private boolean checkXmlParameters (List<Constant> a_xmlParameters,
									    List<Short> a_interfaceConstantValues)
	{
		boolean errorsExist = false;
		for (Constant par : a_xmlParameters)
		{
			short value = par.getValue();
			if (!a_interfaceConstantValues.contains(value))
			{
				errorsExist = true;
				writeMessageIntoReport(ERROR, "Параметр " + value + " атрибута "
									   + par.getName() +" тега " + par.getType() +
									   " не найден в константах соответствующих " +
									   "интерфейсов");
			}
		}
		return errorsExist;
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
