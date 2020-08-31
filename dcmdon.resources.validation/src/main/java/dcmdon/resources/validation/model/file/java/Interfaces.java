package dcmdon.resources.validation.model.file.java;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import dcmdon.resources.validation.ResourceValidator;
import dcmdon.resources.validation.model.Configuration;
import dcmdon.resources.validation.model.file.Constant;
import dcmdon.resources.validation.model.file.Constant.Type;

/**
 * Интерфейсы для проверки.
 */
public class Interfaces
{
	private Type type;
	private AllowedEqualConstants[] allowedEqualConstants;
	private Map<String, String> files;
	
	public Type getType ()
	{
		Objects.requireNonNull(type, "Укажите тип интерфейсов" + 
			     			    	 ResourceValidator.CONFIG_MESSAGE_END);
		return type;
	}
	
	public Map<Short, List<String>> getAllowedEqualConstNamesByValue ()
	{
		Objects.requireNonNull(allowedEqualConstants, "Укажите элементы массива " +
							   Configuration.KEY_ALLOWED_EQUAL_CONSTANTS +
							   " констант, значения которых могут повторяться, " +
							   "в теле объекта типа " + getType() + " в массиве " +
							   Configuration.KEY_INTERFACES +
							   ResourceValidator.CONFIG_MESSAGE_END);
		
		Map<Short, List<String>> constMap = new HashMap<>();
		for (AllowedEqualConstants c : allowedEqualConstants)
		{
			String errorMessagePart = " констант, значения которых " +
									  "могут повторяться, для интерфейсов " +
									  "типа " + getType() +
									  ResourceValidator.CONFIG_MESSAGE_END;
			
			List<String> names = c.getNames();
			Objects.requireNonNull(names, "Укажите имена" + errorMessagePart);
			
			if (names.size() == 0)
			{
				throw new IllegalArgumentException("Массив констант не должен " +
												   "быть пустым.");
			}
			
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
		Objects.requireNonNull(files, "Укажите пути и id интерфейсов типа " +
				   					  getType() +
				   					  ResourceValidator.CONFIG_MESSAGE_END);
		
		if (files.size() == 0)
		{
			throw new IllegalArgumentException("Отображение путей и id интерфейсов " +
											   "не должно быть пустым.");
		}
		return files;
	}
}