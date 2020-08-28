package dcmdon.resources.validation.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ValidationResult
{
	public enum Code
	{
		ERROR(1), OK(0), UNKNOWN(0);
		
		private int m_value;
		
		private Code (int a_value)
		{
			m_value = a_value;
		}
		
		public int getValue ()
		{
			return m_value;
		}
	}
	
	public enum Type
	{
		ERROR(Code.ERROR), INFO(Code.OK), UNKNOWN(Code.UNKNOWN), WARNING(Code.OK);
		
		private Code m_code;
		
		private Type (Code a_code)
		{
			m_code = a_code;
		}
		
		public Code getCode ()
		{
			return m_code;
		}
	}
	
	public enum Key
	{
		EQUAL_INTERFACE_CONSTS(Type.ERROR), FILE_NON_EXISTING(Type.ERROR),
		
		FILE_PATH(Type.UNKNOWN), INITIAL_INFORMATION(Type.INFO),
		
		INVALID_CONST_PREFIX(Type.ERROR), INVALID_XML_ATTRIBUTE_PARAMETER(Type.ERROR),
		
		NEED_FOR_INTERFACE_PATH(Type.ERROR), NEED_FOR_XML_FILES(Type.WARNING),
		
		SOME_EQUAL_INTERFACE_PATHS(Type.WARNING), SUCCESS(Type.INFO);
		
		private Type m_keyType;
		
		private Key (Type a_type)
		{
			m_keyType = a_type;
		}
		
		public Type getKeyType ()
		{
			return m_keyType;
		}
	}
	
	private ValidationResult m_root;
	
	private ValidationResult m_parent;
	
	private Key m_key;
	
	private String m_value;
	
	private Type m_resultType;
	
	private Code m_code;
	
	private String m_prefix;
	
	private String m_postFix;
	
	private List<ValidationResult> m_entries = new ArrayList<>();
	
	public ValidationResult (ValidationResult a_parent, Key a_key, String a_value)
	{
		m_key = Objects.requireNonNull(a_key);
		m_resultType = m_key.getKeyType();
		m_code = m_resultType.getCode();
		m_value = Objects.requireNonNull(a_value);
		m_parent = a_parent;
		if (m_parent != null)
		{
			m_parent.addEntry(this);
			m_root = m_parent.getRoot();
		}
	}
	
	public ValidationResult (Key a_key, String a_value)
	{
		this(null, a_key, a_value);
		m_root = this;
	}
	
	public ValidationResult getRoot ()
	{
		return m_root;
	}
	
	public boolean isRoot ()
	{
		return this == m_root ? true : false;
	}
	
	public ValidationResult getParent ()
	{
		return m_parent;
	}
	
	public Key getKey ()
	{
		return m_key;
	}
	
	public String getValue ()
	{
		return m_value;
	}
	
	public Type getResultType ()
	{
		return m_resultType;
	}
	
	public Code getCode ()
	{
		return m_code;
	}
	
	public Code getGenaralCode ()
	{
		if (m_root != null)
		{
			return getGeneralCode(m_root);
		}
		if (m_parent != null)
		{
			return getGeneralCode(m_parent);
		}
		return getGeneralCode(this);
	}
	
	private Code getGeneralCode (ValidationResult a_result)
	{
		List<ValidationResult> entries = a_result.getEntries();
		for (ValidationResult result : entries)
		{
			if (getGeneralCode(result) == Code.ERROR)
			{
				return Code.ERROR;
			}
		}
		return Code.OK;
	}
	
	public List<ValidationResult> getEntries ()
	{
		return m_entries;
	}
	
	public void setPrefix (String a_prefix)
	{
		m_prefix = Objects.requireNonNull(a_prefix);
	}
	
	public String getPrefix ()
	{
		if (m_prefix == null)
		{
			switch(m_resultType)
			{
				case ERROR : return "[ERR!]\t";
			
				case INFO : return "[INFO]\t";
				
				case WARNING : return "[WARN]\t";
				
				default : return "";
			}
		}
		return m_prefix;
	}
	
	public boolean needsChangeResultType ()
	{
		return m_resultType == Type.UNKNOWN ? true : false;
	}
	
	public boolean changeResultType (Type a_type)
	{
		if (needsChangeResultType() && a_type != Type.UNKNOWN)
		{
			m_resultType = a_type;
			m_code = m_resultType.getCode();
			return true;
		}
		return false;
	}
	
	public void setPostfix (String a_postfix)
	{
		m_postFix = Objects.requireNonNull(a_postfix);
	}
	
	public String getPostfix ()
	{
		if (m_postFix == null)
		{
			if(m_resultType == Type.INFO && m_key == Key.FILE_PATH)
			{
				return ":";
			}
			else return "";
		}
		return m_postFix;
	}
	
	private void addEntry (ValidationResult a_entry)
	{
		m_entries.add(a_entry);
	}
}
