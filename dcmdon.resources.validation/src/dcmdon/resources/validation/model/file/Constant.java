package dcmdon.resources.validation.model.file;

public class Constant
{
	public static final String NAME_ID = "Id";
	
	public static final String TYPE = "short";
	
	private String m_type;
	private String m_name;
	private short m_value;
	
	public Constant (String a_type, String a_name, short a_value)
	{
		m_type = a_type;
		m_name = a_name;
		m_value = a_value;
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
}
