package dcmdon.resources.validation.model.file.java;

import java.util.List;

/**
 * Класс, предоставляющий список имён констант,
 * значения которых могут повторяться в рамках
 * одного типа интерфейсов, а также значение
 * данных констант. Для группы интерфейсов одного
 * типа может существовать ноль и более объектов
 * класса AllowedEqualConstants.
 */
public class AllowedEqualConstants
{
	private List<String> names;
	private String value;
	
	public List<String> getNames ()
	{
		return names;
	}
	
	public String getValue ()
	{
		return value;
	}
}
