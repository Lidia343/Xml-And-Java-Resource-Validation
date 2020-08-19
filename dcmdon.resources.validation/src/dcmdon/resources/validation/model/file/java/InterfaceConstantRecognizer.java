package dcmdon.resources.validation.model.file.java;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.annotation.NonNull;
import dcmdon.resources.validation.model.file.Constant;
import dcmdon.resources.validation.model.file.IConstantRecognizer;

public class InterfaceConstantRecognizer implements IConstantRecognizer
{
	@Override
	public List<Constant> getConstants(String a_constantType, String a_fileWithConstantPath) throws FileNotFoundException, NoSuchElementException, NumberFormatException
	{
		List<Constant> constants = new ArrayList<>();
		try (Scanner scanner = new Scanner (new BufferedReader (new FileReader(a_fileWithConstantPath))))
		{
			scanner.useDelimiter("[\\p{javaWhitespace}]*=[\\p{javaWhitespace}]*|[\\p{javaWhitespace}]+|;");
			while (scanner.hasNext())
			{
				if (scanner.next().equals(Constant.TYPE))
				{
					String errorMessagePart = "В интерфейсе " + a_fileWithConstantPath +
							   				  " ожидалось ";
					
					String name = scanner.next();
					Assert.isNotNull(name, errorMessagePart + "имя константы.");
					
					short value = -1;
					try
					{
						value = Short.parseShort(scanner.next());
					}
					catch (NumberFormatException e)
					{
						throw new NumberFormatException(errorMessagePart + "значение константы " +
														name + ".");
					}
					
					Constant constant = new Constant(a_constantType, name, value,
													 a_fileWithConstantPath);
					constants.add(constant);
				}
			}
		}
		return constants;
	}
}
