package dcmdon.resources.validation.main;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import dcmdon.resources.validation.ResourceValidator;

public class Application implements IApplication 
{
	private static final String PLUGIN_ID = "dcmdon.resources.validation";
	
	private static final String ERROR_MESSAGE = "Укажите путь к исполняемому файлу " +
												"(" + PLUGIN_ID + ".exe), а также путь " +
												"к файлу конфигурации (как первый " +
												"аргумент команды).";
	@Override
	public Object start(IApplicationContext a_context)
	{
		try
		{
			String [] args = (String [])a_context.getArguments().get(
							  IApplicationContext.APPLICATION_ARGS);
			if (args.length == 0)
			{
				System.out.println(ResourceValidator.ERROR + ERROR_MESSAGE);
				return Integer.valueOf(ResourceValidator.ERROR_RESULT_CODE);
			}
			
			String configFilePath = args[0];
			
			ResourceValidator validator = new ResourceValidator(configFilePath);
			System.out.print(validator.validateAndGetReport());
			return Integer.valueOf(validator.getValidationResultCode());
		}
		catch (NullPointerException e)
		{
			System.out.println(e.getMessage());
			return Integer.valueOf(ResourceValidator.ERROR_RESULT_CODE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return Integer.valueOf(ResourceValidator.ERROR_RESULT_CODE);
		}
	}

	@Override
	public void stop()
	{
		
	}
}
