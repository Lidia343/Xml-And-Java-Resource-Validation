package dcmdon.resources.validation.model;

import dcmdon.resources.validation.model.file.java.Interface;

public class Configuration
{
	private String[] xmlFilePaths;
	private Interface[] interfaces;
	
	public String[] getXmlFilePaths ()
	{
		return xmlFilePaths;
	}
	
	public Interface[] getInterfaces ()
	{
		return interfaces;
	}
}
