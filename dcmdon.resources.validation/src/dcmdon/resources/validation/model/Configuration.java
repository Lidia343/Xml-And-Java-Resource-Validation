package dcmdon.resources.validation.model;

import dcmdon.resources.validation.model.file.java.Interfaces;

public class Configuration
{
	private String[] xmlFilePaths;
	private Interfaces[] interfaces;
	
	public String[] getXmlFilePaths ()
	{
		return xmlFilePaths;
	}
	
	public Interfaces[] getInterfaces ()
	{
		return interfaces;
	}
}
