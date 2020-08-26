package dcmdon.resources.validation.model;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Класс для чтения конфигурации.
 */
public class ConfigurationReader
{
	private final String m_nonNullMessage = " не должен быть равен null.";
	
	/**
	 * @param a_reader
	 * 		  Объект для чтения конфигурации
	 * @return объект конфигурации
	 */
	public Configuration read (Reader a_reader)
	{
		Objects.requireNonNull(a_reader,
		"Объект для чтения файла конфигурации" + m_nonNullMessage);
		
		Gson gson = new GsonBuilder().create();
		return gson.fromJson(a_reader, Configuration.class);
	}
	
	
	/**
	 * @param a_filePath
	 * 		  Путь к файлу конфигурации
	 * @return объект конфигурации
	 * @throws IOException
	 */
	public Configuration read (String a_filePath) throws IOException
	{
		Objects.requireNonNull(a_filePath,
		"Путь к файлу конфигурации" + m_nonNullMessage);
		
		try (Reader reader = new FileReader(a_filePath))
		{
			return read(reader);
		}
	}
}
