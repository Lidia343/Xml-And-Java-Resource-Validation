package dcmdon.resources.validation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dcmdon.resources.validation.model.Configuration;
import dcmdon.resources.validation.model.file.Constant;
import dcmdon.resources.validation.model.file.IConstantRecognizer;
import dcmdon.resources.validation.model.file.java.Interfaces;
import dcmdon.resources.validation.model.file.java.InterfaceConstantRecognizer;
import dcmdon.resources.validation.model.file.xml.IdParameterRecognizer;

/**
 * Класс для проверки констант интерфейсов
 * двух типов (Resource и Property) на
 * наличие одинаковых значений в интерфейсах
 * одного типа, а также для проверки xml-файлов
 * на присутствие каждого параметра атрибута
 * id одного из тегов (Resource или Property)
 * в значениях констант соответствующих интерфейсов.
 */
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
	
	private StringBuilder m_reportBuilder;
	
	private int m_resultCode = OK_RESULT_CODE;
	
	private List<Short> m_allResourceInterfaceConstantValues = new ArrayList<>();
	private List<Short>  m_allPropertyInterfaceConstantValues = new ArrayList<>();
	
	/**
	 * @param a_configFilePath
	 * 		  Путь к конфигурационному файлу
	 * 		  (содержимое файла должно
	 * 		  соответствовать формату JSON)
	 */
	public ResourceValidator (String a_configFilePath)
	{
		 m_configFilePath = a_configFilePath;
	}
	
	/**
	 * Считывает файл конфигурации и проверяет
	 * интерфейсы и xml-файлы.
	 * @throws Exception 
	 */
	private void validateAllResources () throws Exception
	{
		m_reportBuilder = new StringBuilder();
		m_configuration = readConfiguration();
		validateInterfaces();
		validateXmlFiles();
	}
	
	/**
	 * Проверяет интерфейсы.
	 * @throws Exception 
	 */
	private void validateInterfaces () throws Exception
	{
		InterfaceConstantRecognizer interfaceConstRecognizer =
								    new InterfaceConstantRecognizer();
		Interfaces[] interfaces = getInterfacesForValidation();
		
		for (Interfaces i : interfaces)
		{
			validateInterfaceConstants (interfaceConstRecognizer, i);
		}
	}
	
	/**
	 * Проверяет константы указанных интерфейсов.
	 * @param a_interfaceConstRecognizer
	 *		  Распознаватель констант интерфейса
	 * @param a_interfaces
	 *		  Интерфейсы для проверки
	 * @throws NoSuchElementException 
	 * @throws FileNotFoundException 
	 * @throws NumberFormatException 
	 * @throws Exception 
	 */
	private void validateInterfaceConstants (InterfaceConstantRecognizer
											 a_interfaceConstRecognizer,
											 Interfaces a_interfaces) throws
											 NumberFormatException,
											 FileNotFoundException,
											 NoSuchElementException
	{
		String interfaceType = a_interfaces.getType();
		
		/*
		 * Константы, значения которых могут повторяться в рамках
		 * одного типа интерфейсов:
		 */
		Map<Short, List<String>> allowedEqualConsts =
		a_interfaces.getAllowedEqualConstNamesByValue();
		
		/*
		 * Отображение для записи в него правильных констант из числа всех
		 * и сравнения проверяемых констант с ними:
		 */
		Map<String, Constant> interfaceConstByName = new HashMap<>();
		
		Map<String, String> idByPath = a_interfaces.getIdByPath();
		for (String interfacePath : idByPath.keySet())
		{
			m_reportBuilder.append(System.lineSeparator());
			if (!writeFileExistingIntoReport(interfacePath)) continue;
			
			String interfaceId = idByPath.get(interfacePath);
			List<Constant> interfaceConstants = a_interfaceConstRecognizer.
					                            getConstants(interfaceId,
															interfaceType,
															interfacePath);
		
			/*
			 * Переменная для записи в неё информации о правильности
			 * текущей проверяемой константы:
			 */
			boolean validConstant;
			
			/*
			 * Переменная для записи в неё информации о наличии
			 * среди проверяемых констант хотя бы одной неправильной:
			 */
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
					
					/*
					 * Если найдена константа с тем же значением,
					 * проверяется, входит ли она в отображение
					 * с константами-исключениями:
					 */
					if (value.equals(entryConstValue))
					{
						if (allowedEqualConsts.containsKey(value))
						{
							List<String> allowedEqualNames = allowedEqualConsts.
															 get(entryConstValue);
							
							if (!allowedEqualNames.contains(name) ||
							    !allowedEqualNames.contains(entryConstName))
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
				
				/*
				 * Если константа успешно прошла проверку, она
				 * добавляется в отображение interfaceConstByName,
				 * а её значение - в общий для констант её типа список:
				 */
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
			
			/*
			 * Если при проверке интерфейса не было найдено ошибок,
			 * в отчёт записывается соответствующее сообщение:
			 */
			if (!errorsExist) writeMessageIntoReport(INFO, m_noErrMessage);
		}
	}

	/**
	 * Возвращает интерфейсы для проверки и печатает в отчёт
	 * сообщение о проверке интерфейсов.
	 * @return интерфейсы для проверки
	 */
	private Interfaces[] getInterfacesForValidation ()
	{
		writeMessageIntoReport(INFO, "Проверка интерфейсов...");
		return m_configuration.getInterfaces();
	}
	
	/**
	 * Считывает файл конфигурации.
	 * @return объект класса Configuration, который
	 * содержит пути к xml-файлам и информацию об
	 * интерфейсах для проверки
	 * @throws IOException
	 */
	private Configuration readConfiguration () throws IOException
	{
		try (BufferedReader reader = new BufferedReader (new InputStreamReader(
														 new FileInputStream(
														 m_configFilePath),
														 "UTF-8")))
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
	
	/**
	 * Печатает в отчёт путь к файлу и, если файл
	 * не существует, информацию о том, что он не
	 * был найден.
	 * @param a_filePath
	 * 		  Путь к файлу
	 * @return true - если файл существует, false -
	 * иначе
	 */
	private boolean writeFileExistingIntoReport (String a_filePath)
	{
		writeMessageIntoReport(INFO, a_filePath + ":");
		File javaFile = new File(a_filePath);
		if (!javaFile.exists())
		{
			writeMessageIntoReport(ERROR, m_fileNotFoundMessage);
			return false;
		}
		if (javaFile.isDirectory())
		{
			writeMessageIntoReport(ERROR, "Укажите путь к файлу интерфейса, " +
										  "а не к директориии");
			return false;
		}
		return true;
	}
	
	/**
	 * Если файл не существует, печатает соответствующее
	 * сообщение в отчёт.
	 * @param a_file
	 * 		  Файл для проверки на существование
	 */
	private void writeIntoReportIfFileNotFound (File a_file)
	{
		if (!a_file.exists())
		{
			writeMessageIntoReport(INFO, a_file.getAbsolutePath() + ":");
			writeMessageIntoReport(ERROR, m_fileNotFoundMessage);
		}
	}
	
	/**
	 * Записывает сообщение в отчёт. Если a_prefix равен
	 * ResourceValidator.ERROR, коду результата общей
	 * проверки присваивается значение, соответствующее
	 * ошибке (ResourceValidator.ERROR_RESULT_CODE).
	 * @param a_prefix
	 * 		  Префикс сообщения
	 * @param a_message
	 * 		  Сообщение
	 */
	private void writeMessageIntoReport (String a_prefix, String a_message)
	{
		if (a_prefix.equals(ERROR)) m_resultCode = ERROR_RESULT_CODE;
		m_reportBuilder.append(a_prefix + a_message + System.lineSeparator());
	}
	
	/**
	 * Записывает в отчёт, что значение константы
	 * a_errorConst равно значению константы
	 * a_equalConst.
	 * @param a_errorConst
	 * 		  Неправильная константа, значение
	 * 		  которой совпадает со значением
	 * 		  константы a_equalConst
	 * @param a_equalConst
	 * 		  Правильная константа
	 */
	private void writeErrorConstIntoReport (Constant a_errorConst,
											Constant a_equalConst)
	{
		String errorMessage = "Константа " + a_errorConst.getName() +
				   			  " = " + a_errorConst.getValue() +
				   			  " равна константе " + a_equalConst.getName();
		
		if (!a_errorConst.getInterfacePath().equals(a_equalConst.
													getInterfacePath()))
		{
			errorMessage += " (" + a_equalConst.getInterfaceId() + ")";
		}
		writeMessageIntoReport(ERROR, errorMessage);
	}
	
	/**
	 * Проверяет xml-файлы.
	 * @throws Exception 
	 */
	private void validateXmlFiles () throws Exception
	{
		IConstantRecognizer tagRecognizer = new IdParameterRecognizer();
		
		List<File> xmlFilesForValidation = getXmlFilesForValidation();
		if (xmlFilesForValidation.size() == 0)
		{
			writeMessageIntoReport(WARNING, "Не указаны файлы для проверки" +
											 ERROR_MESSAGE_END);
		}
		for (File xmlFile : xmlFilesForValidation)
		{
			String path = xmlFile.getAbsolutePath();
			
			m_reportBuilder.append(System.lineSeparator());
			writeMessageIntoReport(INFO, path + ":");
			
			List<Constant> resourcePars = tagRecognizer.
										  getConstants(Interfaces.RESOURCE_TYPE,
												  	   path);
			List<Constant> propertyPars = tagRecognizer.
										  getConstants(Interfaces.PROPERTY_TYPE,
												       path);
			
			/*
			 * Если все параметры правильные, записывает в отчёт
			 * сообщение об отсутствии ошибок:
			 */
			if (!(!checkXmlParameters(resourcePars,
									  m_allResourceInterfaceConstantValues) ||
				!checkXmlParameters(propertyPars,
									m_allPropertyInterfaceConstantValues)))
			{
				writeMessageIntoReport(INFO, m_noErrMessage);
			}
		}
	}
	
	/**
	 * @return список xml-файлов для проверки
	 * @throws IOException
	 */
	private List<File> getXmlFilesForValidation () throws IOException
	{
		m_reportBuilder.append(System.lineSeparator());
		writeMessageIntoReport(INFO, "Проверка xml-файлов...");
		
		List<File> result = new ArrayList<>();
		
		List<File> uniqueXmlFiles =
		getUniqueXmlFilesForValidation(m_configuration.getXmlFilePaths());
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
	
	/**
	 * @param a_xmlFilePaths
	 * 		  Массив путей к файлам и
	 * 		  директориям
	 * @return список xml-файлов и директорий,
	 * найденных по путям a_xmlFilePaths. В
	 * списке не присутствуют избыточные файлы и
	 * директории
	 */
	private List<File> getUniqueXmlFilesForValidation (String[] a_xmlFilePaths)
	{
		List<File> result = new ArrayList<>();
		addXmlFilesToList(result, a_xmlFilePaths);
		removeFilesWithRedundantPaths(result);
		return result;
	}
	
	/**
	 * Удаляет файлы и директории, присутствие которых
	 * в данном списке избыточно.
	 * Например, если в списке есть директория и файл,
	 * находящийся в данной директории, файл будет удалён,
	 * так как впоследствии он может быть найден в результате
	 * обхода данной директории.
	 * @param a_xmlFiles
	 * 		  Список файлов для проверки
	 */
	private void removeFilesWithRedundantPaths (List<File> a_xmlFiles)
	{
		List<File> filesWithRedundantPaths =
		getFilesWithRedundantPaths(a_xmlFiles);
		for (File file : filesWithRedundantPaths)
		{
			a_xmlFiles.remove(file);
		}
	}
	
	/**
	 * Добавляет файлы и директории, имеющие пути
	 * a_xmlFilePaths, в список a_result.
	 * @param a_result
	 * 		  Список для добавления
	 * @param a_xmlFilePaths
	 * 		  Пути к файлам
	 */
	private void addXmlFilesToList (List<File> a_result,
									String[] a_xmlFilePaths)
	{
		for (String path : a_xmlFilePaths)
		{
			File file = new File(path);
			
			writeIntoReportIfFileNotFound(file);
			
			if (file.isFile())
			{
				addXmlFileToList(file, a_result);
			}
			if (file.isDirectory())
			{
				a_result.add(file);
			}
		}
	}
	
	/**
	 * Выявляет в списке a_xmlFiles файлы и директории,
	 * присутствие которых в данном списке избыточно.
	 * Например, если в списке есть директория и файл,
	 * находящийся в данной директории, файл будет
	 * добавлен в результирующий список, так как
	 * впоследствии файл может быть найден в результате
	 * обхода данной директории.
	 * @param a_xmlFiles
	 * 		  Список файлов для проверки
	 * @return список файлов, которые необходимо
	 * удалить
	 */
	private List<File> getFilesWithRedundantPaths (List<File> a_xmlFiles)
	{
		List<File> result = new ArrayList<>();
		
		int size = a_xmlFiles.size();
		for (int i = 0; i < size - 1; i++)
		{
			for (int j = i + 1; j < size; j++)
			{
				File file1 = a_xmlFiles.get(i);
				File file2 = a_xmlFiles.get(j);
				
				String path1 = file1.getAbsolutePath();
				String path2 = file2.getAbsolutePath();
				
				/*
				 * Если первый путь содержит второй,
				 * необходимо добавить файл, имеющий
				 * первый путь, в результирующий список:
				 */
				if (path1.contains(path2))
				{
					if (!result.contains(file1))
					{
						result.add(file1);
					}
				}
				/*
				 * Если второй путь содержит первый,
				 * необходимо провести аналогичные
				 * действия:
				 */
				else
				if (path2.contains(path1))
				{
					if (!result.contains(file2))
					{
						result.add(file2);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * @param a_parent
	 * 		  Директория для рекурсивного
	 * 		  обхода
	 * @return все xml-файлы из директории
	 * a_parent
	 */
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
	
	/**
	 * Добавляет файл a_file в список a_list,
	 * если a_file имеет расширение .xml.
	 * @param a_file
	 * 		  Файл для добавления в список
	 * 		  a_list
	 * @param a_list - список, в который
	 * 		  необходимо добавить a_file
	 */
	private void addXmlFileToList (File a_file, List<File> a_list)
	{
		if (a_file.getName().endsWith(".xml"))
		{
			a_list.add(a_file);
		}
	}
	
	/**
	 * Проверяет список констант a_xmlParameters,
	 * являющихся атрибутами тега Resource или
	 * Property xml-файла, на наличие их значений
	 * в списке a_interfaceConstantValues. Если
	 * значение хотя бы одной константы отсутствует
	 * в списке, в отчёт записывается соответствующее
	 * сообщение.
	 * @param a_xmlAttributes
	 * 		  Константы для проверки
	 * @param a_interfaceConstantValues
	 * 		  Список значений констант интерфейса
	 * 	      того типа, который соответствует типу
	 * 	      констант в списке a_xmlAttributes
	 * @return true - если все значения констант из
	 * списка a_xmlAttributes содержатся в списке
	 * a_interfaceConstantValues, false - иначе
	 */
	private boolean checkXmlParameters (List<Constant> a_xmlAttributes,
									    List<Short> a_interfaceConstantValues)
	{
		boolean result = true;
		for (Constant par : a_xmlAttributes)
		{
			short value = par.getValue();
			if (!a_interfaceConstantValues.contains(value))
			{
				result = false;
				writeMessageIntoReport(ERROR, "Строка: " + par.getLineNumber() +
									   ". Тег: " + par.getType() + ". " +
									   par.getName() + " = " + value);
			}
		}
		return result;
	}
	
	/**
	 * Проверяет константы интерфейсов типа
	 * Resource и Property и параметры
	 * атрибутов id тегов Resource и Property
	 * xml-файлов. Результат проверки возвращается
	 * в виде строки-отчёта.
	 * Файлы для проверки, а также списки констант,
	 * значения которых могут повторяться в рамках
	 * одного типа интерфейсов, указываются в файле
	 * конфигурации.
	 * @return отчёт о результате проверки
	 * @throws Exception
	 */
	public String validateAndGetReport () throws Exception
	{
		validateAllResources();
		return m_reportBuilder.toString();
	}
	
	/**
	 * @return код результата проверки. Если
	 * в процессе проверки не было обнаружено
	 * ошибок, возвращает
	 * ResourceValidator.OK_RESULT_CODE, иначе -
	 * ResourceValidator.ERROR_RESULT_CODE
	 */
	public int getValidationResultCode ()
	{
		return m_resultCode;
	}
}
