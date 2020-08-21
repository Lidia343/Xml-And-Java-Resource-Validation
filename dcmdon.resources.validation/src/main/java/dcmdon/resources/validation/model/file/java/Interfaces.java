package dcmdon.resources.validation.model.file.java;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import dcmdon.resources.validation.ResourceValidator;
import dcmdon.resources.validation.model.Configuration;
import dcmdon.resources.validation.model.file.Constant;

/**
 * Интерфейсы для проверки.
 */
public class Interfaces
{
	public static final String RESOURCE_TYPE = "Resource";
	public static final String PROPERTY_TYPE = "Property";
	
	private String type;
	private AllowedEqualConstants[] allowedEqualConstants;
	private Map<String, String> files;
	
	public String getType ()
	{
		Objects.requireNonNull(type, "Укажите тип интерфейсов" + 
			     			    	 ResourceValidator.ERROR_MESSAGE_END);
		return type;
	}
	
	public Map<Short, List<String>> getAllowedEqualConstNamesByValue ()
	{
		Objects.requireNonNull(allowedEqualConstants, "Укажите элементы массива " +
							   Configuration.KEY_ALLOWED_EQUAL_CONSTANTS +
							   " констант, значения которых могут повторяться, " +
							   "в теле объекта типа " + getType() + " в массиве " +
							   Configuration.KEY_INTERFACES +
							   ResourceValidator.ERROR_MESSAGE_END);
		
		Map<Short, List<String>> constMap = new HashMap<>();
		for (AllowedEqualConstants c : allowedEqualConstants)
		{
			String errorMessagePart = " констант, значения которых " +
									  "могут повторяться, для интерфейсов " +
									  "типа " + getType() +
									  ResourceValidator.ERROR_MESSAGE_END;
			
			List<String> names = c.getNames();
			Objects.requireNonNull(names, "Укажите имена" + errorMessagePart);
			
			String stringValue = c.getValue();
			Objects.requireNonNull(stringValue, "Укажите значение" +
									       errorMessagePart);
			
			short shortValue = -1;
			try
			{
				shortValue = Short.parseShort(stringValue);
			}
			catch (NumberFormatException e)
			{
				throw new NumberFormatException("Значение константы должно " +
												"соответствовать типу " +
												Constant.TYPE);
			}
			
			constMap.put(Short.valueOf(shortValue), names);
		}
		return constMap;
	}
	
	public Map<String, String> getIdByPath ()
	{
		String errorMessage = "Укажите пути и id интерфейсов типа " +
							   getType() + ResourceValidator.ERROR_MESSAGE_END;
		
		Objects.requireNonNull(files, errorMessage);
		
		if (files.size() == 0)
		{
			throw new IllegalArgumentException(errorMessage);
		}
		return files;
	}
}