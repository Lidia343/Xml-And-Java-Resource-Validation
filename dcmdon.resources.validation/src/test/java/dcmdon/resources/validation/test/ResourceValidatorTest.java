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
	//Путь к файлу конфигурации:
	private final String m_configFilePath = "src/test/resources/config.json";
	
	/**
	 * Вызывает метод "validateAndGetReport"
	 * класса ResourceValidator и проверяет
	 * код, получаемый с помощью метода 
	 * "getValidationResultCode()", на равенство
	 * коду отсутствия любых ошибок во время
	 * выполнения программы проверки файлов,
	 * указанных в файле конфигурации.
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception
	{
		ResourceValidator validator = new ResourceValidator(m_configFilePath);
		validator.validateAndGetReport();
		assertEquals(validator.getValidationResultCode(),
					           ResourceValidator.OK_RESULT_CODE);
	}
}
