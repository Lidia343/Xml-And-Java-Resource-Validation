package dcmdon.resources.validation.model.file;

import java.util.List;

import dcmdon.resources.validation.model.file.java.Interfaces.Type;

/**
 * Интерфейс распознавателя констант.
 */
public interface IConstantRecognizer
{
	/**
	 * Возвращает распознанные константы типа a_constantType
	 * в файле, имеющем путь a_fileWithConstantPath.
	 * @param a_constantType
	 * 		  Тип констант
	 * @param a_fileWithConstantPath
	 * 		  Путь к файлу с константами
	 * @return распознанные константы
	 * @throws Exception
	 */
	List<Constant> getConstants (Type a_constantType,
								 String a_fileWithConstantPath) throws Exception;
}
