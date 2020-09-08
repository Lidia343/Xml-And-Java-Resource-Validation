package dcmdon.resources.validation.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Единичный результат проверки.
 */
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
	
	private Code m_generalCode = Code.OK;
	
	private String m_prefix;
	
	private String m_postFix;
	
	private List<ValidationResult> m_nodes = new ArrayList<>();
	
	private int m_errorCount = 0;
	
	/**
	 * Конструктор класса ValidationResult.
	 * @param a_parent
	 * 		  Родительский узел для добавления
	 * 		  к нему данного результата
	 * @param a_key
	 * 		  Ключ результата проверки
	 * @param a_value
	 * 		  Значение результата проверки
	 */
	public ValidationResult (ValidationResult a_parent, Key a_key, String a_value)
	{
		m_key = Objects.requireNonNull(a_key);
		m_resultType = m_key.getKeyType();
		m_code = m_resultType.getCode();
		m_value = Objects.requireNonNull(a_value);
		m_parent = a_parent;
		if (m_parent != null)
		{
			m_parent.addNode(this);
			m_root = m_parent.getRoot();
		}
		if (m_key.getKeyType().getCode() == Code.ERROR)
		{
			incErrorCountAndSetErrorCode();
		}
	}
	
	/**
	 * Конструктор класса ValidationResult.
	 * Создаёт корень дерева результатов
	 * проверки.
	 * @param a_key
	 * 		  Ключ результата проверки
	 * @param a_value
	 * 		  Значение результата проверки
	 */
	public ValidationResult (Key a_key, String a_value)
	{
		this(null, a_key, a_value);
		m_root = this;
	}
	
	/**
	 * @return корень дерева, в которое
	 * входит данный объект.
	 * Возвращает данный объект, если он
	 * является корнем.
	 */
	public ValidationResult getRoot ()
	{
		return m_root;
	}
	
	/**
	 * @return true - если данный объект
	 * является корнем, false - иначе
	 */
	public boolean isRoot ()
	{
		return this == m_root ? true : false;
	}
	
	/**
	 * @return родительский узел данного
	 * объекта
	 */
	public ValidationResult getParent ()
	{
		return m_parent;
	}
	
	/**
	 * @return ключ результата проверки
	 */
	public Key getKey ()
	{
		return m_key;
	}
	
	/**
	 * @return значение результата проверки
	 */
	public String getValue ()
	{
		return m_value;
	}
	
	/**
	 * @return тип результата проверки
	 */
	public Type getResultType ()
	{
		return m_resultType;
	}
	
	/**
	 * @return код результата проверки
	 */
	public Code getCode ()
	{
		return m_code;
	}
	
	/**
	 * @return общий код результата проверки
	 * для дерева, включающего данный объект.
	 * Если хотя бы один узел дерева имеет
	 * код Code.ERROR, возвращает Code.ERROR,
	 * иначе - Code.OK
	 */
	public Code getGenaralCode ()
	{
		return m_generalCode;
	}
	
	/**
	 * @return узлы данного объекта
	 */
	public List<ValidationResult> getNodes ()
	{
		return m_nodes;
	}
	
	/**
	 * @param a_prefix
	 * 		  Префикс сообщения результата
	 * 		  проверки
	 */
	public void setPrefix (String a_prefix)
	{
		m_prefix = Objects.requireNonNull(a_prefix);
	}
	
	/**
	 * @return префикс сообщения результата
	 * проверки
	 */
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
	
	/**
	 * @return true - если тип результата проверки
	 * равен Type.UNKNOWN, false - иначе
	 */
	public boolean needsChangeResultType ()
	{
		return m_resultType == Type.UNKNOWN ? true : false;
	}
	
	/**
	 * @param a_type
	 * 		  Новый тип результата проверки
	 * @return true - если тип был изменён,
	 * false - иначе
	 */
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
	
	/**
	 * @param a_postfix
	 * 		  Постфикс сообщения результата
	 * 		  проверки
	 */
	public void setPostfix (String a_postfix)
	{
		m_postFix = Objects.requireNonNull(a_postfix);
	}
	
	/**
	 * @return постфикс сообщения результата
	 * проверки
	 */
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
	
	/**
	 * Добавляет узел к данному результату
	 * проверки.
	 * @param a_entry
	 * 		  Узел для добавления
	 */
	private void addNode (ValidationResult a_entry)
	{
		m_nodes.add(a_entry);
	}
	
	/**
	 * Увеличивает значение счётчика ошибок
	 * на один и изменяет общий код результата
	 * проверки на Code.ERROR.
	 */
	private void incErrorCountAndSetErrorCode ()
	{
		if (m_root == null || m_root == this)
		{
			m_errorCount++;
			if (m_generalCode != Code.ERROR)
			{
				m_generalCode = Code.ERROR;
			}
		}
		else
		{
			m_root.incErrorCountAndSetErrorCode();
		}
	}
	
	/**
	 * @return значение счётчика ошибок
	 */
	public int getErrorCount ()
	{
		if (m_root == null || m_root == this)
		{
			return m_errorCount;
		}
		return m_root.getErrorCount();
	}
}
