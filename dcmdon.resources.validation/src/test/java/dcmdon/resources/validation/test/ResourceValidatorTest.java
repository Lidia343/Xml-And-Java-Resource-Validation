package dcmdon.resources.validation.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import dcmdon.resources.validation.ResourceValidator;
import dcmdon.resources.validation.io.ConfigurationReader;
import dcmdon.resources.validation.model.ValidationReport;
import dcmdon.resources.validation.model.ValidationResult;
import dcmdon.resources.validation.model.ValidationResult.Code;
import dcmdon.resources.validation.model.ValidationResult.Key;

/**
 * Класс для общего теста программы.
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
	
	private final String m_nonExistingInterfaceFileConfig = "src/test/resources/invalid/" +
															"non_existing_interface_file_config.json";
	
	private final String m_nonExistingXmlFileConfig = "src/test/resources/invalid/" +
													  "non_existing_xml_file_config.json";
	
	
	/**
	 * Проверяет заведомо корректные ресурсы.
	 * @throws Exception
	 */
	@Test
	public void testValidResources()
	{
		testReport(m_validConfig, ValidationResult.Code.OK, (Key[])null);
	}
	
	/**
	 * Проверяет заведомо некорректные ресурсы.
	 * @throws Exception
	 */
	@Test
	public void testInvalidResources()
	{
		testExceptionMessage(m_xmlPathsKeyNonExistConfig,
							 "Укажите пути к xml");
		
		testExceptionMessage(m_interfacesKeyNonExistConfig,
							 "Укажите ключ \"Interfaces\"");
		
		testExceptionMessage(m_typeKeyNonExistConfig,
							 "Укажите тип интерфейсов");
		
		testExceptionMessage(m_allowedEqualConstKeyNonExistConfig,
							 "Укажите элементы массива");
		
		testExceptionMessage(m_allowedEqualConstNamesKeyNonExistConfig,
							 "Укажите имена констант");
		
		testExceptionMessage(m_allowedEqualConstValueKeyNonExistConfig,
							 "Укажите значение констант");
		
		testExceptionMessage(m_filesKeyNonExistConfig,
							 "Укажите пути и id интерфейсов");
		
		testExceptionMessage(m_emptyInterfaceListConfig,
							 "Укажите информацию об интерфейсах");
		
		testExceptionMessage(m_emptyAllowedEqualConstNamesListConfig,
							 "Массив констант не должен быть пустым");
		
		testExceptionMessage(m_emptyFilesListConfig,
							 "Отображение путей и id интерфейсов");
		
		testExceptionMessage(m_nonUniqueInterfacePathsConfig,
							 "оригинальные пути к интерфейсам");
		
		testExceptionMessage(m_invalidAllowedEqualConstValueConfig,
							 "Значение константы должно");

		testExceptionMessage(m_invalidInterfaceConstValueConfig,
				 			 "ожидалось значение константы");

		testReport(m_emptyXmlFileListConfig, ValidationResult.Code.OK,
				   Key.NEED_FOR_XML_FILES);
		
		testErrorReport(m_nonExistingInterfaceFileConfig, Key.FILE_NON_EXISTING);
		
		testErrorReport(m_nonExistingXmlFileConfig, Key.FILE_NON_EXISTING);
		
		testErrorReport(m_baseInvalidConfig, Key.SOME_EQUAL_INTERFACE_PATHS,
						Key.NEED_FOR_INTERFACE_PATH, Key.INVALID_CONST_PREFIX,
						Key.EQUAL_INTERFACE_CONSTS, Key.INVALID_XML_ATTRIBUTE_PARAMETER);
	}
	
	/**
	 * Проверяет появление искючения с сообщением,
	 * содержащим строку a_exceptionMesssage, при
	 * проверке ресурсов, указанных в файле
	 * a_configFilePath.
	 * @param a_configFilePath
	 * 	 	  Файл конфигурации
	 * @param a_exceptionMesssage
	 * 		  Строка, которая должна содержаться в 
	 * 		  сообщении исключения
	 */
	private void testExceptionMessage (String a_configFilePath, String a_exceptionMesssage)
	{
		try
		{
			ResourceValidator validator = new ResourceValidator(
					  					  new ConfigurationReader().
					  					  read(a_configFilePath));
			validator.validateAndGetReport();
			fail();
		}
		catch (Exception e)
		{
			if (a_exceptionMesssage == null) fail();
			if (!e.getMessage().contains(a_exceptionMesssage))
			{
				fail();
			}
		}
	}
	
	
	/**
	 * Проверяет наличие ключей a_keysForSearch в
	 * узлах дерева результатов проверки всех
	 * ресурсов, указанных в файле a_configFilePath,
	 * а также общий код реультатов проверки на
	 * соответствие коду Code.ERROR.
	 * @param a_configFilePath
	 * 		  Файл конфигурации
	 * @param a_keysForSearch
	 * 		  Ключи для поиска
	 */
	private void testErrorReport (String a_configFilePath,
								  Key... a_keysForSearch)
	{
		testReport(a_configFilePath, ValidationResult.Code.ERROR,
				   a_keysForSearch);
	}
	
	/**
	 * Проверяет наличие ключей a_keysForSearch в
	 * узлах дерева результатов проверки всех
	 * ресурсов, указанных в файле a_configFilePath,
	 * а также общий код реультатов проверки на
	 * соответствие коду a_codeForEqual.
	 * @param a_configFilePath
	 * 		  Файл конфигурации
	 * @param a_codeForEqual
	 * 		  Код для сравнения
	 * @param a_keysForSearch
	 * 		  Ключи для поиска
	 */
	private void testReport (String a_configFilePath, Code a_codeForEqual,
							 Key... a_keysForSearch)
	{
		List<Key> keys = null;
		if (a_keysForSearch != null)
		{
			keys = Arrays.asList(a_keysForSearch);
		}
		try
		{
			ResourceValidator validator = new ResourceValidator(
					  					  new ConfigurationReader().
					  					  read(a_configFilePath));
			
			ValidationReport report = validator.validateAndGetReport();
			
			assertNotEquals(report.getText(), null);
			assertEquals(report.getValidationResultCode(), a_codeForEqual);
			
			if (keys == null) return;
			
			for (Key key : keys)
			{
				if (!report.containsKey(key))
				{
					fail();
				}
			}
		}
		catch (Exception e)
		{
			fail();
		}
	}
}
