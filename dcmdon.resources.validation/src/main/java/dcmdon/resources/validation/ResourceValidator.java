package dcmdon.resources.validation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;

import dcmdon.resources.validation.model.Configuration;
import dcmdon.resources.validation.model.TxtReport;
import dcmdon.resources.validation.model.ValidationReport;
import dcmdon.resources.validation.model.ValidationResult;
import dcmdon.resources.validation.model.ValidationResult.Code;
import dcmdon.resources.validation.model.ValidationResult.Key;
import dcmdon.resources.validation.model.file.Constant;
import dcmdon.resources.validation.model.file.Constant.Type;
import dcmdon.resources.validation.model.file.SourceFile;
import dcmdon.resources.validation.model.file.java.Interfaces;
import dcmdon.resources.validation.recognition.IConstantRecognizer;
import dcmdon.resources.validation.recognition.IdParameterRecognizer;
import dcmdon.resources.validation.recognition.InterfaceConstantRecognizer;

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
	public static final String CONFIG_MESSAGE_END = " в конфигурационном файле.";
	
	private final String m_successMessage = "Ошибки не обнаружены.";
	
	private final String m_fileNotFoundMessage = "Файл не найден.";
	
	private Configuration m_configuration;
	
	private ValidationResult m_valResultRoot;
	
	private ValidationResult m_currentValResult;
	
	private ValidationReport m_report;
	
	private List<Short> m_allResourceInterfaceConstantValues = new ArrayList<>();
	private List<Short>  m_allPropertyInterfaceConstantValues = new ArrayList<>();
	
	/**
	 * Конструктор класса ResourceValidator.
	 * @param a_configuration
	 * 		  Объект конфигурации
	 */
	public ResourceValidator (Configuration a_configuration)
	{
		 m_configuration = Objects.requireNonNull(a_configuration,
				 		   "Объект конфигурации не должен быть равен null.");
	}
	
	/**
	 * Считывает файл конфигурации и проверяет
	 * интерфейсы и xml-файлы.
	 * @throws Exception 
	 */
	private void validateAllResources () throws Exception
	{
		m_valResultRoot = new ValidationResult(Key.INITIAL_INFORMATION,
							  "Отчёт о проверке интерфейсов и xml-файлов");
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
											 IOException,
											 NoSuchElementException
	{
		Type interfaceType = a_interfaces.getType();
		
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
			ValidationResult interfaceValResult =
			getFileExistingResult(interfacePath);
			
			if (interfaceValResult.getResultType() ==
				ValidationResult.Type.ERROR)
			{
				continue;
			}
			
			String interfaceId = idByPath.get(interfacePath);
			
			SourceFile source = new SourceFile(interfaceId,
											   interfaceType,
											   interfacePath);
			
			List<Constant> interfaceConstants = a_interfaceConstRecognizer.
					                            getConstants(source);
		
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
				if (!validateConstantPrefix(interfaceValResult, c))
				{
					errorsExist = true;
				}
				
				String name = c.getName();
				
				Short value = Short.valueOf(c.getValue());
				validConstant = true;
				for (Entry<String, Constant> entry : interfaceConstByName.
													 entrySet())
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
								addEqualConstResult(interfaceValResult, c,
													entryConst);
							}
						}
						else
						{
							validConstant = false;
							errorsExist = true;
							addEqualConstResult(interfaceValResult, c,
												entryConst);
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
						
					if (interfaceType.equals(Type.RESOURCE))
					{
						m_allResourceInterfaceConstantValues.add(value);
					}
					if (interfaceType.equals(Type.PROPERTY))
					{
						m_allPropertyInterfaceConstantValues.add(value);
					}
				}
			}
			
			/*
			 * Если при проверке интерфейса не было найдено ошибок,
			 * в отчёт записывается соответствующее сообщение:
			 */
			if (!errorsExist) new ValidationResult(interfaceValResult,
												   Key.SUCCESS, m_successMessage);
		}
	}
	
	/**
	 * Проверяет префикс константы на соответствие
	 * её типу ("RESOURCE" или "PROPERTY").
	 * Типу "RESOURCE" соответствует префикс "RES",
	 * "PROPERTY" - "PROP".
	 * @param a_parentForErrResult
	 * 		  Объект класса ValidationResult для
	 * 		  создания нового объекта класса
	 * 		  ValidationResult в случае, если
	 * 		  константа имеет неверный префикс
	 * @param a_constant
	 * 		  Константа для проверки
	 * @return true - если префикс константы
	 * соответствует её типу, false - иначе.
	 */
	private boolean validateConstantPrefix (ValidationResult a_parentForErrResult,
											Constant a_constant)
	{
		if (!(a_constant.getName().equals(Constant.ALLOWED_NAME_WITHOUT_PREFIX) ||
			  validateConstantPrefixForType(a_constant, Type.RESOURCE) ||
			  validateConstantPrefixForType(a_constant, Type.PROPERTY)))
		{
			new ValidationResult(a_parentForErrResult, Key.INVALID_CONST_PREFIX,
								 "Константа " + a_constant.getName() +
								 " имеет неверный префикс");
			return false;
		}
		return true;
	}
	
	/**
	 * Проверяет префикс константы на соответствие
	 * типу a_type.
	 * Типу "RESOURCE" соответствует префикс "RES",
	 * "PROPERTY" - "PROP".
	 * @param a_constant
	 * 		  Константа для проверки
	 * @param a_type
	 * 		  Тип для сравнения
	 * @return true - если префикс константы
	 * соответствует типу a_type, false - иначе.
	 */
	private boolean validateConstantPrefixForType (Constant a_constant,
												   Type a_type)
	{
		if (a_constant.getSourceFile().getType() == a_type)
		{
			if (a_constant.getName().startsWith(a_type.getPrefix()))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Возвращает интерфейсы для проверки.
	 * @return интерфейсы для проверки
	 */
	private Interfaces[] getInterfacesForValidation ()
	{
		m_currentValResult = new ValidationResult(m_valResultRoot,
							 					  Key.INITIAL_INFORMATION,
							 					  "Проверка интерфейсов...");
		return removeEqualInterfacePaths(m_configuration.getInterfaces());
	}
	
	/**
	 * Удаляет из одного объекта класса Interfaces
	 * (типа Interfaces.PROPERTY_TYPE) пути,
	 * совпадающие с путями интерфейсов типа
	 * Interfaces.RESOURCE_TYPE.
	 * @param a_interfaces
	 * 		  Интерфейсы для проверки
	 * @return Список интерфейсов с оригинальными
	 * путями
	 */
	private Interfaces[] removeEqualInterfacePaths (Interfaces[] a_interfaces)
	{
		Interfaces resources = new Interfaces();
		Interfaces properties = new Interfaces();
		
		if (a_interfaces[0].getType().equals(Type.RESOURCE))
		{
			resources = a_interfaces[0];
			properties = a_interfaces[1];
		}
		else
		{
			resources = a_interfaces[1];
			properties = a_interfaces[0];
		}
		
		List<String> pathsForRemoving = new ArrayList<>();
		Map<String, String> propIdByPath = properties.getIdByPath();
		for (String resPath : resources.getIdByPath().keySet())
		{
			for (String propPath : propIdByPath.keySet())
			{
				if (propPath.equals(resPath))
				{
					pathsForRemoving.add(propPath);
				}
			}
		}
		
		ValidationResult equalPathValResult = null;
		if (pathsForRemoving.size() != 0)
		{
			equalPathValResult = new ValidationResult(m_currentValResult,
									 Key.SOME_EQUAL_INTERFACE_PATHS,
									 "В интерфейсах типа " + Type.PROPERTY + 
									 " обнаружены пути, совпадающие " +
									 "с путями интерфейсов типа " +
									 Type.RESOURCE + ":");
		}
		
		for (String path : pathsForRemoving)
		{
			new ValidationResult(equalPathValResult, Key.FILE_PATH, path).
			 					 changeResultType(ValidationResult.Type.WARNING);
			
			propIdByPath.remove(path);
			if (propIdByPath.size() == 0)
			{
				throw new IllegalArgumentException("Укажите оригинальные " +
												   "пути к интерфейсам " +
												   "типа " + 
												    Type.PROPERTY +
												    System.lineSeparator() +
												    CONFIG_MESSAGE_END);
			}
		}
		
		return a_interfaces;
	}
		
	/**
	 * @param a_filePath
	 * 		  Путь к файлу
	 * @return если файл существует, возвращает
	 * результат проверки ValidationResult с
	 * ключом Key.FILE_PATH, иначе - с ключом
	 * Key.FILE_NON_EXISTING
	 */
	private ValidationResult getFileExistingResult (String a_filePath)
	{
		ValidationResult result = new ValidationResult(m_currentValResult,
							      Key.FILE_PATH, a_filePath);
		result.changeResultType(ValidationResult.Type.INFO);
		
		File javaFile = new File(a_filePath);
		if (!javaFile.exists())
		{
			return new ValidationResult(result, Key.FILE_NON_EXISTING,
										m_fileNotFoundMessage);
		}
		if (javaFile.isDirectory())
		{
			return new ValidationResult(result, Key.NEED_FOR_INTERFACE_PATH,
										"Укажите путь к файлу интерфейса, " +
					  					"а не к директориии");
		}
		return result;
	}
	
	/**
	 * Если файл не существует, создаёт два объекта
	 * класса ValidationResult (один - с ключом
	 * Key.FILE_PATH, другой - с ключом
	 * Key.FILE_NON_EXISTING).
	 * @param a_file
	 * 		  Файл для проверки на существование
	 */
	private void createFileNonExistingResult (File a_file)
	{
		if (!a_file.exists())
		{
			ValidationResult fileRes = new ValidationResult(m_currentValResult,
									   Key.FILE_PATH, a_file.getAbsolutePath());
			fileRes.changeResultType(ValidationResult.Type.INFO);
			new ValidationResult(fileRes, Key.FILE_NON_EXISTING,
								 m_fileNotFoundMessage);
		}
	}
	
	/**
	 * Создаёт результат проверки ValidationResult с
	 * ключом Key.EQUAL_INTERFACE_CONSTS.
	 * @param a_parent
	 * 		  Объект для добавления к нему нового
	 * 		  результата проверки
	 * @param a_errorConst
	 * 		  Неправильная константа, значение
	 * 		  которой совпадает со значением
	 * 		  константы a_equalConst
	 * @param a_equalConst
	 * 		  Правильная константа
	 */
	private void addEqualConstResult (ValidationResult a_parent,
									  Constant a_invalidConst,
									  Constant a_equalConst)
	{
		String errorMessage = "Константа " + a_invalidConst.getName() +
				   			  " = " + a_invalidConst.getValue() +
				   			  " равна константе " + a_equalConst.getName();
		
		SourceFile equalConstSource = a_equalConst.getSourceFile();
		if (!a_invalidConst.getSourceFile().getPath().equals(equalConstSource.
														     getPath()))
		{
			errorMessage += " (" + equalConstSource.getId() + ")";
		}
		new ValidationResult(a_parent, Key.EQUAL_INTERFACE_CONSTS, errorMessage);
	}
	
	/**
	 * Проверяет xml-файлы.
	 * @throws Exception 
	 */
	private void validateXmlFiles () throws Exception
	{
		m_currentValResult = new ValidationResult(m_valResultRoot,
												  Key.INITIAL_INFORMATION,
												  "Проверка xml-файлов...");
		
		IConstantRecognizer tagRecognizer = new IdParameterRecognizer();
		
		List<File> xmlFilesForValidation = getXmlFilesForValidation();
		if (xmlFilesForValidation.size() == 0)
		{
			new ValidationResult(m_currentValResult, Key.NEED_FOR_XML_FILES,
								 "Не указаны файлы для проверки" +
								 CONFIG_MESSAGE_END);
		}
		for (File xmlFile : xmlFilesForValidation)
		{
			String path = xmlFile.getAbsolutePath();
			
			ValidationResult xmlFileRes = new ValidationResult(m_currentValResult,
												 	  		   Key.FILE_PATH, path);
			xmlFileRes.changeResultType(ValidationResult.Type.INFO);
			
			SourceFile resourceFile = new SourceFile(Type.RESOURCE, path);
			SourceFile propertyFile = new SourceFile(Type.PROPERTY, path);
			
			List<Constant> resourceAttrs = tagRecognizer.
										   getConstants(resourceFile);
			List<Constant> propertyAttrs = tagRecognizer.
										   getConstants(propertyFile);
			
			if (!(!checkXmlParameters(xmlFileRes, resourceAttrs,
									  m_allResourceInterfaceConstantValues) ||
				  !checkXmlParameters(xmlFileRes, propertyAttrs,
									  m_allPropertyInterfaceConstantValues)))
			{
				//Если все параметры правильные:
				new ValidationResult(xmlFileRes, Key.SUCCESS, m_successMessage);
			}
		}
	}
	
	/**
	 * @return список xml-файлов для проверки
	 * @throws IOException
	 */
	private List<File> getXmlFilesForValidation () throws IOException
	{
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
	private List<File> getUniqueXmlFilesForValidation (List<String> a_xmlFilePaths)
	{
		List<String> paths = new ArrayList<>();
		for (String path : a_xmlFilePaths)
		{
			if (!paths.contains(path))
			{
				paths.add(path);
			}
		}
		
		List<File> result = new ArrayList<>();
		addXmlFilesToList(result, paths);
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
									List<String> a_xmlFilePaths)
	{
		for (String path : a_xmlFilePaths)
		{
			File file = new File(path);
			
			createFileNonExistingResult(file);
			
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
	 * Проверяет список констант a_xmlAttributes,
	 * являющихся атрибутами тега Resource или
	 * Property xml-файла, на наличие их значений
	 * в списке a_interfaceConstantValues. Если
	 * значение хотя бы одной константы отсутствует
	 * в списке, создаётся объект класса
	 * ValidationResult с ключом
	 * Key.INVALID_XML_ATTRIBUTE_PARAMETER.
	 * @param a_parentForErrResult
	 * 		  Объект для добавления к нему нового
	 * 		  результата проверки в случае, когда
	 * 		  метод возвращает false
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
	private boolean checkXmlParameters (ValidationResult a_parentForErrResult,
										List<Constant> a_xmlAttributes,
									    List<Short> a_interfaceConstantValues)
	{
		boolean result = true;
		for (Constant par : a_xmlAttributes)
		{
			short value = par.getValue();
			if (!a_interfaceConstantValues.contains(value))
			{
				result = false;
				new ValidationResult(a_parentForErrResult,
									 Key.INVALID_XML_ATTRIBUTE_PARAMETER,
									 "Строка: " + par.getLineNumber() +
									 ". Тег: " + par.getSourceFile().getType() +
									 ". " + par.getName() + " = " + value +
									 ". Параметр не найден в значениях " +
									 "констант соответствующих интерфейсов");
			}
		}
		return result;
	}
	
	/**
	 * Проверяет константы интерфейсов типа
	 * Resource и Property и параметры
	 * атрибутов id тегов Resource и Property
	 * xml-файлов. Результат проверки возвращается
	 * в виде отчёта ValidationReport.
	 * Файлы для проверки указываются в файле
	 * конфигурации.
	 * @return отчёт о результате проверки
	 * @throws Exception
	 */
	public ValidationReport validateAndGetReport () throws Exception
	{
		validateAllResources();
		m_report = new TxtReport(m_valResultRoot);
		return m_report;
	}
	
	/**
	 * @return код результата проверки
	 */
	public Code getValidationResultCode ()
	{
		return m_report.getValidationResultCode();
	}
}
