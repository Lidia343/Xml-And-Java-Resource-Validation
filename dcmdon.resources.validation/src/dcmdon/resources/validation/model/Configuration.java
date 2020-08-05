package dcmdon.resources.validation.model;

import dcmdon.resources.validation.model.file.java.Interface;

public class Configuration
{
	private String xmlDir;
	private Interface[] interfaces;
	
	public String getXmlDir ()
	{
		return xmlDir;
	}
	
	public Interface[] getInterfaces ()
	{
		return interfaces;
	}
}
