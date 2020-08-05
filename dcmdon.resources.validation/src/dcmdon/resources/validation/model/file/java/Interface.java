package dcmdon.resources.validation.model.file.java;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interface
{
	private String type;
	private String path;
	private AllowedEqualConstants[] allowedEqualConstants;
	
	public String getType ()
	{
		return type;
	}
	
	public String getPath ()
	{
		return path;
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
}