package dcmdon.resources.validation.model.file.java;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.eclipse.core.runtime.Assert;
import dcmdon.resources.validation.model.file.Constant;
import dcmdon.resources.validation.model.file.IConstantRecognizer;

public class InterfaceConstantRecognizer implements IConstantRecognizer
{
	@Override
	public List<Constant> getConstants(String a_interfaceType,
			   						   String a_interfacePath)
			   						   throws FileNotFoundException,
			   						   NoSuchElementException,
			   						   NumberFormatException
	{
		return getConstants(null, a_interfaceType, a_interfacePath);
	}
	
	/**
	 * Описать
	 * @param @Nullable a_interfaceId
	 * @param a_interfaceType
	 * @param a_interfacePath
	 * @return
	 * @throws FileNotFoundException
	 * @throws NoSuchElementException
	 * @throws NumberFormatException
	 */
	public List<Constant> getConstants(String a_interfaceId,
									   String a_interfaceType,
									   String a_interfacePath)
									   throws FileNotFoundException,
									   NoSuchElementException,
									   NumberFormatException
	{
		List<Constant> constants = new ArrayList<>();
		try (Scanner scanner = new Scanner (new BufferedReader
										   (new FileReader
										   (a_interfacePath))))
		{
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
					
					String name = scanner.next();
					Assert.isNotNull(name, errorMessagePart +
									 "имя константы.");
					
					short value = -1;
					try
					{
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
