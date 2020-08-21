package dcmdon.resources.validation.model.file;

import dcmdon.resources.validation.model.file.java.Interfaces.TYPE;

/**
 * Константа, которая может являться
 * константой интерфейса или атрибутом
 * одного из тегов xml_файла.
 */
public class Constant
{
	public static final String NAME_ID = "Id"; //Атрибут "Id" xml-файла
	
	public static final String TYPE = "short";
	
	/*
	 * Ключ номера строки, на которой находится атрибут в xml-файле:
	 */
	public static final String DATA_LINE_NUMBER = "lineNumber";
	
	private TYPE m_type;
	private String m_name;
	private short m_value;
	
	private String m_interfacePath;
	private String m_interfaceId;
	
	private int m_lineNumber;
	
	/**
	 * Конструктор класса Constant.
	 * @param a_type
	 * 		  Тип константы (Interfaces.RESOURCE_TYPE
	 * 		  или Interfaces.PROPERTY_TYPE)
	 * @param a_name
	 * 		  Имя константы (для атрибута xml-файла
	 * 		  имя должно быть равно Constant.NAME_ID)
	 * @param a_value
	 * 		  Значение константы (должно быть типа
	 * 		  short (Constant.TYPE))
	 * @param a_interfacePath
	 * 		  Путь к интерфейсу, содержащему константу
	 * @param a_interfaceId
	 * 		  Id интерфейса, содержащего константу
	 *    	  (необязательно указывать уникальный id)
	 */
	public Constant (TYPE a_type, String a_name, short a_value,
					 String a_interfacePath, String a_interfaceId)
	{
		this(a_type, a_name, a_value, -1);
		m_interfacePath = a_interfacePath;
		m_interfaceId = a_interfaceId;
	}
	
	/**
	 * Конструктор класса Constant.
	 * @param a_type
	 * 		  Тип атрибута (Interfaces.RESOURCE_TYPE
	 * 		  или Interfaces.PROPERTY_TYPE)
	 * @param a_name
	 * 		  Имя атрибута. Должно быть равно
	 * 		  Constant.NAME_ID
	 * @param a_value
	 * 		  Параметр атрибута (должен быть типа
	 * 		  short (Constant.TYPE))
	 * @param a_lineNumber
	 * 		  номер строки, на которой находится
	 * 		  атрибут в xml-файле
	 */
	public Constant (TYPE a_type, String a_name, short a_value,
					 int a_lineNumber)
	{
		m_type = a_type;
		m_name = a_name;
		m_value = a_value;
		
		m_lineNumber = a_lineNumber;
	}
	
	public TYPE getType ()
	{
		return m_type;
	}
	
	public String getName ()
	{
		return m_name;
	}
	
	public short getValue ()
	{
		return m_value;
	}
	
	public String getInterfacePath ()
	{
		return m_interfacePath;
	}
	
	public String getInterfaceId ()
	{
		return m_interfaceId;
	}
	
	public int getLineNumber ()
	{
		return m_lineNumber;
	}
}
