package dcmdon.resources.validation.model.file.java;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

import dcmdon.resources.validation.model.file.Constant;
import dcmdon.resources.validation.model.file.IConstantRecognizer;
import dcmdon.resources.validation.model.file.SourceFile;
import dcmdon.resources.validation.util.Util;

/**
 * Распознаватель констант интерфейсов.
 */
public class InterfaceConstantRecognizer implements IConstantRecognizer
{
	@Override
	public List<Constant> getConstants(SourceFile a_sourceFile) throws
									   NoSuchElementException,
			   						   NumberFormatException, IOException
	{
		List<Constant> constants = new ArrayList<>();
		
		String interfacePath = a_sourceFile.getPath();
		
		String interfaceCode = new CommentFilter().getFilteredText(Util.getText(interfacePath));
			
		try (Scanner scanner = new Scanner (interfaceCode))
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
											   interfacePath +
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
														name + " типа " +
														Constant.TYPE + ".");
					}
						
					//Создание объекта константы:
					Constant constant = new Constant(name, value, a_sourceFile);
					constants.add(constant);
				}
			}
		}
		return constants;
	}
}
