package dcmdon.resources.validation.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

/**
 * Утилитарный класс.
 */
public class Util
{
	/**
	 * @param a_reader
	 * 		  Объект для чтения
	 * @return строку, прочитанную с помощью
	 * a_reader
	 * @throws IOException
	 */
	public static String getText (BufferedReader a_reader) throws IOException
	{
		Objects.requireNonNull(a_reader);
		StringBuilder result = new StringBuilder();
		String line;
		while ((line = a_reader.readLine()) != null)
		{
			result.append(line + System.lineSeparator());
		}
		return result.toString();
	}
	
	/**
	 * @param a_filePath
	 * 		  Файл для чтения
	 * @return содержимое файла a_filePath в виде
	 * строки
	 * @throws IOException
	 */
	public static String getText (String a_filePath) throws IOException
	{
		Objects.requireNonNull(a_filePath);
		try (BufferedReader reader = new BufferedReader(new FileReader(a_filePath)))
		{
			return getText(reader);
		}
	}
}
