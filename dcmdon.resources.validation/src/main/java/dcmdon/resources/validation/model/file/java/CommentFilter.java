package dcmdon.resources.validation.model.file.java;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Класс для фильтрации комментариев,
 * правила построения которых совпадают
 * с синтаксисом java-комментариев.
 */
public class CommentFilter
{
	private String m_filePath;
	
	/**
	 * Конструктор класса CommentFilter.
	 * @param a_filePath
	 * 		  Путь к файлу, содержащему
	 * 		  текст с комментариями
	 */
	public CommentFilter (String a_filePath)
	{
		m_filePath = Objects.requireNonNull(a_filePath, "Путь к файлу " +
														"не должен быть " +
														"равен null.");
	}
	
	/**
	 * @return поток для чтения отфильтрованного
	 * от комментариев текста
	 * @throws IOException
	 */
	public InputStream getFilteredText () throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (BufferedReader reader = new BufferedReader(new FileReader
													   (m_filePath)))
		{
			//Состояние автомата:
			int state = 0;
			String line;
			while ((line = reader.readLine()) != null)
			{
				for (int i = 0; i < line.length(); i++)
				{
					char c = line.charAt(i);
					switch (state)
					{
						//Начальное состояние:
						case 0 :
						{
							/*Прочитанный символ, возможно, является
							началом комментария:*/
							if (c == '/')
							{
								if (i == (line.length() - 1))
								{
									continue;
								}
								else
								{
									//Обработка возможного однострочного комментария:
									if (line.charAt(i + 1) == '/')
									{
										state = 1;
										break;
									}
									//Обработка возможного многострочного комментария:
									if (line.charAt(i + 1) == '*')
									{
										state = 2;
										break;
									}
								}
							}
							out.write(c);
						}
						case 1:
						{
							//Пустой однострочный комментарий:
							if (i == (line.length() - 1)) 
							{
								state = 0;
							}
							break;
						}
						case 2:
						{
							if (c == '*')
							{
								state = 3;
							}
							break;
						}
						case 3:
						{
							if (c == '/')
							{
								state = 0;
							}
							else state = 2;
							break;
						}
					}
				}
			}
		}
		return new ByteArrayInputStream(out.toByteArray());
	}
}
