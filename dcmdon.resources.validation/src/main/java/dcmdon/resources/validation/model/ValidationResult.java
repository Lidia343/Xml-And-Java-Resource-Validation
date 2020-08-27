package dcmdon.resources.validation.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ValidationResult
{
	public enum Type
	{
		INFO, WARNING, ERROR, UNKNOWN;
	}
	
	public enum Key
	{
		/*VALIDATION_PROCESS(INFO), INTERFACE(), XML_FILE,
		NO_ERRORS, FILE_NON_EXISTING, INVALID_PREFIX,
		EQUAL_INTERFACE_PATHS;*/
		
		//private Type m_keyType;
		
		/*private Key (Type a_type)
		{
			m_resultType = a_type;
		}*/
		
		/*public Type getType ()
		{
			return m_keyType;
		}*/
	}
	
	private Type m_resultType;
	
	private Key m_key;
	
	private String m_value;
	
	private List<ValidationResult> m_entries = new ArrayList<>();
	
	public ValidationResult (Type a_type, Key a_key, String a_value)
	{
		m_resultType = Objects.requireNonNull(a_type);
		if (a_type == Type.UNKNOWN)
		{
			
		}
		m_key = Objects.requireNonNull(a_key);
		m_value = Objects.requireNonNull(a_value);
	}
	
	public ValidationResult (Key a_key, String a_value)
	{
		m_resultType = Objects.requireNonNull(m_resultType);
		m_key = Objects.requireNonNull(a_key);
		m_value = Objects.requireNonNull(a_value);
	}
	
	public Type getType ()
	{
		return m_resultType;
	}
	
	public Key getKey ()
	{
		return m_key;
	}
	
	public String getValue ()
	{
		return m_value;
	}
	
	public String getPrefix ()
	{
		switch(m_resultType)
		{
			case INFO : return "[INFO]\t";
			
			case WARNING : return "[WARN]\t";
			
			case ERROR : return "[ERR!]\t";
			
			default : return null;
		}
	}
	
	public List<ValidationResult> getEntries ()
	{
		return m_entries;
	}
	
	public boolean canChangeResultType ()
	{
		return m_resultType == Type.UNKNOWN ? true : false;
	}
	
	/*public boolean changeResultType (Type a_type)
	{
		//if (canChangeType()
	}*/
	
	public void addEntry (ValidationResult a_entry)
	{
		m_entries.add(a_entry);
	}
}
