package dcmdon.resources.validation.model;

import org.eclipse.core.runtime.Assert;

import dcmdon.resources.validation.ResourceValidator;
import dcmdon.resources.validation.model.file.java.Interfaces;

public class Configuration
{
	public static final String KEY_XML_FILE_PATHS = "xmlFilePaths";
	public static final String KEY_INTERFACES = "interfaces";
	public static final String KEY_ALLOWED_EQUAL_CONSTANTS = "allowedEqualConstants";
	
	private String[] xmlFilePaths;
	private Interfaces[] interfaces;
	
	public String[] getXmlFilePaths ()
	{
		Assert.isNotNull(xmlFilePaths, "Укажите пути к xml-файлам или директориям" +
										ResourceValidator.ERROR_MESSAGE_END);
		return xmlFilePaths;
	}
	
	public Interfaces[] getInterfaces ()
	{
		Assert.isNotNull(interfaces, "Укажите информацию об интерфейсах" +
				  					  ResourceValidator.ERROR_MESSAGE_END);
		return interfaces;
	}
}
