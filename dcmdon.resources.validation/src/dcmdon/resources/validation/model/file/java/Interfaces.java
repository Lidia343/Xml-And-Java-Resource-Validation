package dcmdon.resources.validation.model.file.java;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interfaces
{
	public static final String RESOURCE_TYPE = "Resource";
	public static final String PROPERTY_TYPE = "Property";
	
	private String type;
	private AllowedEqualConstants[] allowedEqualConstants;
	private String[] paths;
	
	public String getType ()
	{
		return type;
	}
	
	public Map<Short, List<String>> getAllowedEqualConstNamesByValue ()
	{
		Map<Short, List<String>> constMap = new HashMap<>();
		for (AllowedEqualConstants c : allowedEqualConstants)
		{
			constMap.put(c.getValue(), c.getNames());
		}
		return constMap;
	}
	
	public String[] getPaths ()
	{
		return paths;
	}
}