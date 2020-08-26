package dcmdon.resources.validation.model.file;

/**
 * Константа, которая может являться
 * константой интерфейса или атрибутом
 * одного из тегов xml_файла.
 */
public class Constant
{
	public enum Type
	{
		RESOURCE
		{
			@Override
			public String toString ()
			{
				return "Resource";
			}
		},
		
		PROPERTY
		{
			@Override
			public String toString ()
			{
				return "Property";
			}
		};
		
		public String getPrefix ()
		{
			if (this == RESOURCE)
			{
				return "RES";
			}
			if (this == PROPERTY)
			{
				return "PROP";
			}
			return null;
		}
	}

	public static final String TYPE = "short";
	
	public static final String XML_TAG_ATTRIBUTE = "Id"; //Атрибут "Id" xml-файла
	
	public static final String ALLOWED_NAME_WITHOUT_PREFIX = "ALL";
	
	/*
	 * Ключ номера строки, на которой находится атрибут в xml-файле:
	 */
	public static final String DATA_LINE_NUMBER = "lineNumber";
	
	private String m_name;
	private short m_value;
	
	private SourceFile m_sourceFile;
	
	private int m_lineNumber;
	
	/**
	 * Конструктор класса Constant.
	 * @param a_name
	 * 		  Имя константы (для атрибута xml-файла
	 * 		  имя должно быть равно Constant.NAME_ID)
	 * @param a_value
	 * 		  Значение константы (должно быть типа
	 * 		  short (Constant.TYPE))
	 * @param a_sourceFile
	 * 		  Информация о файле, в котором содержится
	 * 	      константа
	 */
	public Constant (String a_name, short a_value,
					 SourceFile a_sourceFile)
	{
		this(a_name, a_value, a_sourceFile, -1);
	}
	
	/**
	 * Конструктор класса Constant.
	 * @param a_name
	 * 		  Имя атрибута. Должно быть равно
	 * 		  Constant.NAME_ID
	 * @param a_value
	 * 		  Параметр атрибута (должен быть типа
	 * 		  short (Constant.TYPE))
	 * @param a_sourceFile
	 * 		  Информация о файле, в котором содержится
	 * 	      константа
	 * @param a_lineNumber
	 * 		  номер строки, на которой находится
	 * 		  атрибут в xml-файле
	 */
	public Constant (String a_name, short a_value,
					 SourceFile a_sourceFile,
					 int a_lineNumber)
	{
		m_name = a_name;
		m_value = a_value;
		m_sourceFile = a_sourceFile;
		m_lineNumber = a_lineNumber;
	}
	
	public String getName ()
	{
		return m_name;
	}
	
	public short getValue ()
	{
		return m_value;
	}
	
	public SourceFile getSourceFile ()
	{
		return m_sourceFile;
	}
	
	public int getLineNumber ()
	{
		return m_lineNumber;
	}
}
