package dcmdon.resources.validation.main;

import dcmdon.resources.validation.ResourceValidator;

/**
 * Главный класс приложения. Запускает проверку
 * интерфейсов и xml-файлов.
 */
public class Main
{
	private static final String PLUGIN_ID = "dcmdon.resources.validation";
	
	private static final String HELP_MESSAGE = "Запуск приложения " + PLUGIN_ID +
												".jar для проверки интерфейсов " +
												"и xml-файлов." +
												System.lineSeparator() +
												System.lineSeparator() +
												"java -jar путь/к/приложению/" +
												PLUGIN_ID + ".jar <путь/к/" +
												"файлу/конфигурации>" + 
												System.lineSeparator() +
												System.lineSeparator() +
												"Формат файла конфигурации - " +
												"JSON." + System.lineSeparator();
	
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
				System.out.println(HELP_MESSAGE);
				System.exit(ResourceValidator.ERROR_RESULT_CODE);
			}
			
			String configFilePath = a_args[0];
			
			ResourceValidator validator = new ResourceValidator(configFilePath);
			System.out.print(validator.validateAndGetReport() +
									   System.lineSeparator());
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
