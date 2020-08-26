package dcmdon.resources.validation.model.file;

import dcmdon.resources.validation.model.file.Constant.Type;

/**
 * Класс, содержащий информацию о файле
 * - источнике данных (id, тип, путь).
 */
public class SourceFile
{
	private String m_id;
	private Type m_type;
	private String m_path;
	
	/**
	 * Конструктор класса SourceFile.
	 * @param a_id
	 * 		  Id файла
	 * @param a_type
	 * 		  Тип файла
	 * @param a_path
	 * 		  Путь к файлу
	 */
	public SourceFile (String a_id, Type a_type,
					   String a_path)
	{
		m_id = a_id;
		m_type = a_type;
		m_path = a_path;
	}
	
	/**
	 * Конструктор класса SourceFile.
	 * @param a_type
	 * 		  Тип файла
	 * @param a_path
	 * 		  Путь к файлу
	 */
	public SourceFile (Type a_type, String a_path)
	{
		this(a_path, a_type, a_path);
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
