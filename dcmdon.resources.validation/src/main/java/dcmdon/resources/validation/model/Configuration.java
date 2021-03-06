package dcmdon.resources.validation.model;

import java.util.List;
import java.util.Objects;

import dcmdon.resources.validation.ResourceValidator;
import dcmdon.resources.validation.model.file.Constant;
import dcmdon.resources.validation.model.file.java.Interfaces;

/**
 * Конфигурация, содержащая пути к xml-файлам и
 * информацию о проверяемых интерфейсах.
 */
public class Configuration
{
	public static final String KEY_XML_FILE_PATHS = "xmlFilePaths";
	public static final String KEY_INTERFACES = "interfaces";
	public static final String KEY_ALLOWED_EQUAL_CONSTANTS =
	"allowedEqualConstants";
	
	private List<String> xmlFilePaths;
	private Interfaces[] interfaces;
	
	/**
	 * @return пути к xml-файлам. Пути должны быть
	 * указаны в файле конфигурации
	 */
	public List<String> getXmlFilePaths ()
	{
		Objects.requireNonNull(xmlFilePaths,
						 	   "Укажите пути к xml-файлам или директориям" +
						 	   ResourceValidator.CONFIG_MESSAGE_END);
		return xmlFilePaths;
	}
	
	/**
	 * @return информацию об интерфейсах. Она должна
	 * быть указана в файле конфигурации
	 */
	public Interfaces[] getInterfaces ()
	{
		Objects.requireNonNull(interfaces, "Укажите ключ \"Interfaces\" " +
										   "и его значение" +
										   ResourceValidator.CONFIG_MESSAGE_END);
		
		if (interfaces.length < 2)
		{
			throw new IllegalArgumentException("Укажите информацию об интерфейсах " +
					  						   "двух типов (" +
					  						   Constant.Type.RESOURCE.toString().toUpperCase() +
					  						   " и " +
					  						   Constant.Type.PROPERTY.toString().toUpperCase() +
					  						   ")" + ResourceValidator.CONFIG_MESSAGE_END);
		}
		return interfaces;
	}
}
