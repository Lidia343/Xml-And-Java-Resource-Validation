package dcmdon.resources.validation.model.file;

import dcmdon.resources.validation.model.file.java.Interfaces.Type;

public class SourceFile
{
	private String m_id;
	private Type m_type;
	private String m_path;
	
	public SourceFile (String a_id, Type a_type,
					   String a_path)
	{
		m_id = a_id;
		m_type = a_type;
		m_path = a_path;
	}
	
	public String getId ()
	{
		return m_id;
	}
	
	public Type getType ()
	{
		return m_type;
	}
	
	public String getPath ()
	{
		return m_path;
	}
}
