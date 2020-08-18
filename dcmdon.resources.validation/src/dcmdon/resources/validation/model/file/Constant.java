package dcmdon.resources.validation.model.file;

import org.eclipse.core.runtime.Assert;

public class Constant
{
	public static final String NAME_ID = "Id";
	
	public static final String TYPE = "short";
	
	public static final String DATA_LINE_NUMBER = "lineNumber";
	public static final String DATA_COLUMN_NUMBER = "columnNumber";
	
	private String m_type;
	private String m_name;
	private short m_value;
	
	private String m_interfacePath;
	
	private int m_lineNumber;
	private int m_columnNumber;
	
	public Constant (String a_type, String a_name, short a_value,
					 String a_interfacePath)
	{
		this(a_type, a_name, a_value, -1, -1);
		Assert.isNotNull(a_interfacePath);
		m_interfacePath = a_interfacePath;
	}
	
	public Constant (String a_type, String a_name, short a_value,
					 int a_lineNumber, int a_columnNumber)
	{
		Assert.isNotNull(a_type);
		Assert.isNotNull(a_name);
		
		m_type = a_type;
		m_name = a_name;
		m_value = a_value;
		
		m_lineNumber = a_lineNumber;
		m_columnNumber = a_columnNumber;
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
	
	public int getLineNumber ()
	{
		return m_lineNumber;
	}
	
	public int getColumnNumber ()
	{
		return m_columnNumber;
	}
}
