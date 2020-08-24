package dcmdon.resources.validation.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dcmdon.resources.validation.ResourceValidator;

/**
 * Класс для общего теста программы
 * (теста методов класса ResourceValidator).
 */
public class ResourceValidatorTest
{
	//Пути к файлам конфигурации:
	
	private final String m_validConfigFilePath = "src/test/resources/" +
												 "valid_config.json";
	
	private final String m_invalidConfigFilePath = "src/test/resources/" +
												   "invalid_config.json";
	
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
	public void testValidResources() throws Exception
	{
		test(m_validConfigFilePath, ResourceValidator.OK_RESULT_CODE);
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
	public void testInvalidResources() throws Exception
	{
		test(m_invalidConfigFilePath, ResourceValidator.ERROR_RESULT_CODE);
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
	private void test (String a_filePath, int a_code) throws Exception
	{
		ResourceValidator validator = new ResourceValidator(a_filePath);
		validator.validateAndGetReport();
		assertEquals(validator.getValidationResultCode(), a_code);
	}
}
