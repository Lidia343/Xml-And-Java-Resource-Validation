package dcmdon.resources.validation.model.file.java;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

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
					String name = scanner.next();
					short value = Short.parseShort(scanner.next()); //Добавить проверку на выражение
					Constant constant = new Constant(a_constantType, name, value,
													 a_fileWithConstantPath);
					constants.add(constant);
				}
			}
		}
		return constants;
	}
}
