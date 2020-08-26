package dcmdon.resources.validation.model.file;

import java.util.List;

/**
 * Интерфейс распознавателя констант.
 */
public interface IConstantRecognizer
{
	/**
	 * Возвращает распознанные константы из файла
	 * a_sourceFile.
	 * @param a_sourceFile
	 * 		  Информация о файле, в котором содержатся
	 * 	      константы
	 * @return распознанные константы
	 * @throws Exception
	 */
	List<Constant> getConstants (SourceFile a_sourceFile) throws Exception;
}
