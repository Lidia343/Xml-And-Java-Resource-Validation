package dcmdon.resources.validation.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import dcmdon.resources.validation.ResourceValidator;

/**
 * Класс для общего теста программы
 * (теста методов класса ResourceValidator).
 */
public class ResourceValidatorTest
{
	//Пути к файлам конфигурации:

	private final String m_validConfig = "src/test/resources/" +
										 "/valid/config.json";
	
	private final String m_xmlPathsKeyNonExistConfig = "src/test/resources/invalid/" +
													  "xml_paths_key_non_exist_config.json";

	private final String m_interfacesKeyNonExistConfig = "src/test/resources/invalid/" +
			  											 "interfaces_key_non_exist_config.json";

	private final String m_typeKeyNonExistConfig = "src/test/resources/invalid/" +
				 								   "type_key_non_exist_config.json";
	
	private final String m_allowedEqualConstKeyNonExistConfig = "src/test/resources/invalid/" +
				 											    "allowed_equal_const_key_" +
				 											    "non_exist_config.json";
	
	private final String m_allowedEqualConstNamesKeyNonExistConfig = "src/test/resources/" +
																	 "invalid/allowed_" +
																	 "equal_const_names" +
																	 "_key_non_exist_" +
																	 "config.json";
	
	private final String m_allowedEqualConstValueKeyNonExistConfig = "src/test/resources/" +
																	 "invalid/allowed_" +
																	 "equal_const_value" +
																	 "_key_non_exist_" +
																	 "config.json";
	
	private final String m_filesKeyNonExistConfig = "src/test/resources/invalid/" +
				 									"files_key_non_exist_config.json";
	
	private final String m_emptyInterfaceListConfig = "src/test/resources/invalid/" +
				 									  "empty_interface_list_config.json";
	
	private final String m_emptyAllowedEqualConstNamesListConfig = "src/test/resources/invalid/" +
				 											  	   "empty_allowed_equal_const_" +
				 											  	   "names_list" +
				 											       "_config.json";
	
	private final String m_emptyFilesListConfig = "src/test/resources/invalid/" +
				 								  "empty_files_list_config.json";
	
	private final String m_baseInvalidConfig = "src/test/resources/invalid/" +
				 							   "base_config.json";
	
	private final String m_invalidAllowedEqualConstValueConfig = "src/test/resources/invalid/" +
																 "invalid_allowed_equal_const" +
																 "_value_config.json";
	
	private final String m_invalidInterfaceConstValueConfig = "src/test/resources/invalid/" +
				 											  "invalid_interface_const_" +
				 											  "value_config.json";
	
	private final String m_nonUniqueInterfacePathsConfig = "src/test/resources" +
															 "/invalid/non_unique_" +
															 "interface_paths_" +
															 "config.json";
	
	private final String m_emptyXmlFileListConfig = "src/test/resources" +
			 										"/invalid/empty_xml_" +
			 										"file_list_config.json";
	
	private final String m_invalidXmlParConfig = "src/test/resources/invalid/" +
			  										   "invalid_xml_par_config.json";
	
	private final String m_nonExistInterfaceFile = "nonExistInterface.java";
	private final String m_nonExistXmlFile = "nonExistXmlFile.xml";
	
	private final int m_invalXmlParLineNumber = 5;
	
	/**
	 * Проверяет заведомо корректные ресурсы.
	 * Вызывает метод "validateAndGetReport"
	 * класса ResourceValidator и проверяет
	 * код, получаемый с помощью метода 
	 * "getValidationResultCode()", на равенство
	 * коду ResourceValidator.OK_RESULT_CODE.
	 * @throws Exception
	 */
	@Test
	public void testValidResources()
	{
		test(m_validConfig, ResourceValidator.OK_RESULT_CODE, (String[])null);
	}
	
	/**
	 * Вызывает метод "validateAndGetReport"
	 * класса ResourceValidator и проверяет
	 * код, получаемый с помощью метода 
	 * "getValidationResultCode()", на равенство
	 * коду a_code.
	 * @param a_code
	 * 		  Код для сравнения
	 * @param a_filePath
	 * 		  Путь к файлу конфигурации
	 * @throws Exception
	 */
	private void testInvalidResources (String a_configFilePath,
									   String... a_messagesForEqual)
	{
		test(a_configFilePath, ResourceValidator.ERROR_RESULT_CODE,
			 a_messagesForEqual);
	}
	
	private void test (String a_filePath, int a_codeForEqual,
					   String... a_messagesForEqual)
	{
		List<String> messages = null;
		if (a_messagesForEqual != null)
		{
			messages = Arrays.asList(a_messagesForEqual);
		}
		try
		{
			ResourceValidator validator = new ResourceValidator(a_filePath);
			String report = validator.validateAndGetReport();
			assertEquals(validator.getValidationResultCode(), a_codeForEqual);
			
			if (messages == null) return;
			
			for (String message : messages)
			{
				if (!report.contains(message))
				{
					fail();
				}
			}
		}
		catch (Exception e)
		{
			boolean fail = true;
			for (String message : messages)
			{
				if (e.getMessage().contains(message))
				{
					fail = false;
				}
			}
			if (fail) fail();
		}
	}
	
	/**
	 * Проверяет заведомо некорректные ресурсы.
	 * Вызывает метод "validateAndGetReport"
	 * класса ResourceValidator и проверяет
	 * код, получаемый с помощью метода 
	 * "getValidationResultCode()", на равенство
	 * коду ResourceValidator.ERROR_RESULT_CODE.
	 * @throws Exception
	 */
	@Test
	public void testInvalidResources()
	{
		testInvalidResources(m_xmlPathsKeyNonExistConfig,
							 "Укажите пути к xml");
		
		testInvalidResources(m_interfacesKeyNonExistConfig,
							 "Укажите ключ \"Interfaces\"");
		
		testInvalidResources(m_typeKeyNonExistConfig,
							 "Укажите тип интерфейсов");
		
		testInvalidResources(m_allowedEqualConstKeyNonExistConfig,
							 "Укажите элементы массива");
		
		testInvalidResources(m_allowedEqualConstNamesKeyNonExistConfig,
							 "Укажите имена констант");
		
		testInvalidResources(m_allowedEqualConstValueKeyNonExistConfig,
							 "Укажите значение констант");
		
		testInvalidResources(m_filesKeyNonExistConfig,
							 "Укажите пути и id интерфейсов");
		
		testInvalidResources(m_emptyInterfaceListConfig,
							 "Укажите информацию об интерфейсах");
		
		testInvalidResources(m_emptyAllowedEqualConstNamesListConfig,
							 "Массив констант не должен быть пустым");
		
		testInvalidResources(m_emptyFilesListConfig,
							 "Отображение путей и id интерфейсов");
		
		testInvalidResources(m_nonUniqueInterfacePathsConfig,
							 "оригинальные пути к интерфейсам");
		
		test(m_emptyXmlFileListConfig, ResourceValidator.OK_RESULT_CODE,
			 "Не указаны файлы для проверки");
		
		String fileNonExisting = ":" + System.lineSeparator() +
						     	 ResourceValidator.ERROR + "Файл не найден";
		
		testInvalidResources(m_baseInvalidConfig, m_nonExistXmlFile +
							 fileNonExisting, m_nonExistInterfaceFile +
							 fileNonExisting, "обнаружены пути, совпадающие",
						     "Укажите путь к файлу интерфейса",
						     "имеет неверный префикс", "равна константе",
						     "Строка: " + m_invalXmlParLineNumber);
		
		testInvalidResources(m_invalidXmlParConfig,
				 			 "Строка:");
		
		testInvalidResources(m_invalidAllowedEqualConstValueConfig,
							 "Значение константы должно");
		
		testInvalidResources(m_invalidInterfaceConstValueConfig,
							 "ожидалось значение константы");
	}
}
