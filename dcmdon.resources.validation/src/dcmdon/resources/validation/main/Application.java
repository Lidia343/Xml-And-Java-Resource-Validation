package dcmdon.resources.validation.main;

import java.io.File;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import dcmdon.resources.validation.ResourceValidator;

public class Application implements IApplication 
{	
	private final String m_configFilePath = System.getProperty("user.home") + File.separator +
											"dcmdon.resources.validation" + File.separator + "config.json";
	
	@Override
	public Object start(IApplicationContext a_context)
	{
		try
		{
			ResourceValidator validator = new ResourceValidator(m_configFilePath);
			System.out.print(validator.validateAndGetReport());
			return Integer.valueOf(validator.getValidationResultCode());
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
