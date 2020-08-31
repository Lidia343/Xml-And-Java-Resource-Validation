package dcmdon.resources.validation.recognition;

import java.util.List;

import dcmdon.resources.validation.model.file.Constant;
import dcmdon.resources.validation.model.file.SourceFile;

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
