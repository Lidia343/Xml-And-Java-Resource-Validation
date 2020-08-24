package dcmdon.resources.validation.model.file.java;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

import dcmdon.resources.validation.model.file.Constant;
import dcmdon.resources.validation.model.file.IConstantRecognizer;
import dcmdon.resources.validation.model.file.java.Interfaces.TYPE;

/**
 * Распознаватель констант интерфейсов.
 */
public class InterfaceConstantRecognizer implements IConstantRecognizer
{
	@Override
	public List<Constant> getConstants(TYPE a_interfaceType,
			   						   String a_interfacePath) throws
									   NoSuchElementException,
			   						   NumberFormatException, IOException
	{
		return getConstants(null, a_interfaceType, a_interfacePath);
	}
	
	/**
	 * Возвращает распознанные константы интерфейса типа a_interfaceType
	 * из файла a_interfacePath.
	 * @param @Nullable a_interfaceId
	 * 				    Id интерфейса. Если значение равно null,
	 * 					id будет установлено равным a_interfacePath
	 * @param a_interfaceType
	 * 		  Тип интерфейса
	 * @param a_interfacePath
	 * 		  Путь к интерфейсу
	 * @return список констант типа short интерфейса a_interfacePath
	 * @throws NoSuchElementException
	 * @throws NumberFormatException
	 * @throws IOException 
	 */
	public List<Constant> getConstants(String a_interfaceId,
									   TYPE a_interfaceType,
									   String a_interfacePath)
									   throws NoSuchElementException,
									   NumberFormatException,
									   IOException
	{
		List<Constant> constants = new ArrayList<>();
		
		try (InputStream in = new CommentFilter(a_interfacePath).getFilteredStream();
			 Scanner scanner = new Scanner (in))
		{
			//Установка разделителей:
			scanner.useDelimiter("[\\p{javaWhitespace}]*=" +
								 "[\\p{javaWhitespace}]*|" +
								 "[\\p{javaWhitespace}]+|;");
			
			while (scanner.hasNext())
			{
				if (scanner.next().equals(Constant.TYPE))
				{
					String errorMessagePart = "В интерфейсе " +
											  a_interfacePath +
							   				  " ожидалось ";
					
					//Чтение имени константы:
					String name = scanner.next();

					Objects.requireNonNull(name, errorMessagePart +
									 	         "имя константы.");
					
					short value = -1;
					try
					{
						/*
						 * Чтение и преобразование к типу short
						 * значения константы:
						 */
						value = Short.parseShort(scanner.next());
					}
					catch (NumberFormatException e)
					{
						throw new NumberFormatException(errorMessagePart +
														"значение константы " +
														name + ".");
					}
					if (a_interfaceId == null)
					{
						a_interfaceId = a_interfacePath;
					}
					
					//Создание объекта константы:
					Constant constant = new Constant(a_interfaceType,
													 name, value,
													 a_interfacePath,
													 a_interfaceId);
					constants.add(constant);
				}
			}
		}
		return constants;
	}
}
