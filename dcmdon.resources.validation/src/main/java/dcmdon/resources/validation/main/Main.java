package dcmdon.resources.validation.main;

import dcmdon.resources.validation.ResourceValidator;

/**
 * Главный класс приложения. Запускает проверку
 * интерфейсов и xml-файлов.
 */
public class Main
{
	private static final String PLUGIN_ID = "dcmdon.resources.validation";
	
	private static final String ERROR_MESSAGE =
	"Укажите путь к исполняемому файлу (" + PLUGIN_ID + ".exe), а также путь " +
	"к файлу конфигурации (как первый аргумент команды).";
	
	/**
	 * Точка входа в програму.
	 * @param a_args
	 * 		  Аргументы командной строки
	 */
	public static void main (String[] a_args)
	{
		try
		{
			if (a_args.length == 0)
			{
				System.out.println(ResourceValidator.ERROR + ERROR_MESSAGE);
				System.exit(ResourceValidator.ERROR_RESULT_CODE);
			}
			
			String configFilePath = a_args[0];
			
			ResourceValidator validator = new ResourceValidator(configFilePath);
			System.out.print(validator.validateAndGetReport());
			if (validator.getValidationResultCode() ==
				ResourceValidator.ERROR_RESULT_CODE)
			{
				System.exit(ResourceValidator.ERROR_RESULT_CODE);
			}
		}
		catch (Exception e)
		{
			if (e instanceof NullPointerException ||
				e instanceof IllegalArgumentException ||
				e instanceof NumberFormatException)
			{
				System.out.println(e.getMessage());
			}
			else
			{
				e.printStackTrace();
			}
			System.exit(ResourceValidator.ERROR_RESULT_CODE);
		}
	}
}
