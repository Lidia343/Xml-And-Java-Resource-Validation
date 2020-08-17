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
import dcmdon.resources.validation.model.file.java.Interface;
import dcmdon.resources.validation.model.file.java.InterfaceConstantRecognizer;
import dcmdon.resources.validation.model.file.xml.IdParameterRecognizer;

public class ResourceValidator
{
	public static final int OK_RESULT_CODE = 0;
	public static final int ERROR_RESULT_CODE = 1;
	
	private final String m_info = "[INFO]\t";
	private final String m_error = "[ERR!]\t";
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
		Interface[] interfaces = getInterfacesForValidation();
		for (Interface i : interfaces)
		{
			if (!isFileExists(i.getPath())) continue;
			validateInterfaceConstants (interfaceConstRecognizer, i);
		}
	}
	
	private void validateInterfaceConstants (InterfaceConstantRecognizer a_interfaceConstRecognizer,
											 Interface a_interface) throws NumberFormatException,
																           FileNotFoundException, 
																           NoSuchElementException
	{
		Map<Short, List<String>> allowedEqualConsts = a_interface.getAllowedEqualConstNamesByValue();
		
		Map<String, Short> interfaceConstValueByName = new HashMap<>();
		
		String interfaceType = a_interface.getType();
		List<Constant> interfaceConstants = a_interfaceConstRecognizer.getConstants(interfaceType, a_interface.getPath());
		
		boolean validConstant;
		boolean errorsExist = false;
		
		for (Constant c : interfaceConstants)
		{
			String name = c.getName();
			short value = c.getValue();
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
							writeErrorConstIntoReport(name, value, entryName);
						}
					}
					else
					{
						validConstant = false;
						errorsExist = true;
						writeErrorConstIntoReport(name, value, entryName);
					}
				}
			}
			if (validConstant)
			{
				interfaceConstValueByName.put(name, value);
				
				if (interfaceType.equals(Constant.RESOURCE_TYPE))
				{
					m_allResourceInterfaceConstantValues.add(Short.valueOf(value));
				}
				if (interfaceType.equals(Constant.PROPERTY_TYPE))
				{
					m_allPropertyInterfaceConstantValues.add(Short.valueOf(value));
				}
			}
		}
		if (!errorsExist) writeMessageIntoReport(m_info, m_noErrMessage);
	}

	private Interface[] getInterfacesForValidation () throws IOException
	{
		writeMessageIntoReport(m_info, "Проверка интерфейсов...");
		
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
		writeMessageIntoReport(m_info, a_filePath + ":");
		File javaFile = new File(a_filePath);
		if (!javaFile.exists())
		{
			writeMessageIntoReport(m_error, "Файл не найден.");
			return false;
		}
		return true;
	}
	
	private void writeMessageIntoReport (String a_prefix, String a_message)
	{
		if (a_prefix.equals(m_error)) m_resultCode = ERROR_RESULT_CODE;
		m_reportBuilder.append(a_prefix + a_message + System.lineSeparator());
	}
	
	private void writeErrorConstIntoReport (String a_errorConstName,
											short a_errorConstValue,
											String a_equalConstName)
	{
		writeMessageIntoReport(m_error, "Значение " + a_errorConstValue +
							   " константы " + a_errorConstName +
		        			   " равно значению константы " + a_equalConstName);
	}
	
	private void validateXmlFiles () throws ParserConfigurationException, SAXException, IOException
	{
		writeMessageIntoReport(m_info, "Проверка xml-файлов...");
		
		IdParameterRecognizer tagRecognizer = new IdParameterRecognizer();
		
		String[] xmlFilePaths = m_configuration.getXmlFilePaths();
		for (String path : xmlFilePaths)
		{
			if (!isFileExists(path)) continue;
			
			List<Constant> resourcePars = tagRecognizer.getConstants(Constant.RESOURCE_TYPE, path);
			List<Constant> propertyPars = tagRecognizer.getConstants(Constant.PROPERTY_TYPE, path);
			
			boolean errorsExist = checkXmlParameters(resourcePars, m_allResourceInterfaceConstantValues);
			errorsExist = checkXmlParameters(propertyPars, m_allPropertyInterfaceConstantValues);
			
			if (!errorsExist) writeMessageIntoReport(m_info, m_noErrMessage);
		}
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
				writeMessageIntoReport(m_error, "Параметр " + value + " атрибута "
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
