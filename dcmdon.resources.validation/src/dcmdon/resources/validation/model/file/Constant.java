package dcmdon.resources.validation.model.file;

public class Constant
{
	public static final String NAME_ID = "Id";
	
	public static final String TYPE = "short";
	
	public static final String DATA_LINE_NUMBER = "lineNumber";
	
	private String m_type;
	private String m_name;
	private short m_value;
	
	private String m_interfacePath;
	private String m_interfaceId;
	
	private int m_lineNumber;
	
	public Constant (String a_type, String a_name, short a_value,
					 String a_interfacePath, String a_interfaceId)
	{
		this(a_type, a_name, a_value, -1);
		m_interfacePath = a_interfacePath;
		m_interfaceId = a_interfaceId;
	}
	
	public Constant (String a_type, String a_name, short a_value,
					 int a_lineNumber)
	{
		m_type = a_type;
		m_name = a_name;
		m_value = a_value;
		
		m_lineNumber = a_lineNumber;
	}
	
	public String getType ()
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
