package dcmdon.resources.validation.model.file.java;

import java.util.Objects;

/**
 * Класс для фильтрации комментариев,
 * правила построения которых совпадают
 * с синтаксисом java-комментариев.
 */
public class CommentFilter
{
	/**
	 * @param a_text
	 * 		  Строка с комментариями
	 * @return строку без комментариев (не null)
	 * @throws IOException
	 */
	public String getFilteredText (String a_text)
	{
		Objects.requireNonNull(a_text);
		
		String[] lines = a_text.split(System.lineSeparator());
		
		StringBuilder result = new StringBuilder();
		
		//Состояние автомата:
		int state = 0;
		for (String line : lines)
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
						result.append(c);
						break;
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
						if (c == '/' && i != 0)
						{
							state = 0;
						}
						else state = 2;
						break;
					}
				}
				if (i == (line.length() - 1) && state == 0)
				{
					result.append(System.lineSeparator());
				}
			}
		}
		return result.toString();
	}
}
