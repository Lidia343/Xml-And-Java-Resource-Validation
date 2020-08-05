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
	public Object start(IApplicationContext a_context) throws Exception
	{
		ResourceValidator validator = new ResourceValidator(m_configFilePath);
		System.out.println(validator.validateAndGetReport());
		return Integer.valueOf(validator.getValidationResultCode());
	}

	@Override
	public void stop()
	{
		
	}
}
