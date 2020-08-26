package dcmdon.resources.validation.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Util
{
	public static String getText (BufferedReader a_reader) throws IOException
	{
		StringBuilder result = new StringBuilder();
		String line;
		while ((line = a_reader.readLine()) != null)
		{
			result.append(line + System.lineSeparator());
		}
		return result.toString();
	}
	
	public static String getText (String a_filePath) throws IOException
	{
		try (BufferedReader reader = new BufferedReader(new FileReader(a_filePath)))
		{
			return getText(reader);
		}
	}
}
