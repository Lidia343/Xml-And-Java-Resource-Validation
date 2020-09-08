package dcmdon.resources.validation.model;

import java.util.List;
import java.util.Objects;

import dcmdon.resources.validation.model.ValidationResult.Code;
import dcmdon.resources.validation.model.ValidationResult.Key;

/**
 * Отчёт о результатах проверки ресурсов.
 */
public abstract class ValidationReport
{
	protected ValidationResult m_root;
	
	/**
	 * Конструктор класса ValidationReport.
	 * @param a_root
	 * 		  Результат проверки, являющийся
	 * 		  корнем дерева, содержащего все
	 * 		  результаты проверки ValidationResult
	 */
	public ValidationReport (ValidationResult a_root)
	{
		m_root = Objects.requireNonNull(a_root);
	}
	
	/**
	 * @return отчёт в виде строки
	 */
	public abstract String getText ();
	
	/**
	 * Записывает данные одного результата проверки в
	 * отчёт.
	 * @param a_result
	 * 		  Результат проверки для записи в отчёт
	 */
	protected abstract void writeValidationResult (ValidationResult a_result);
	
	/**
	 * @param a_key
	 * 		  Ключ для поиска
	 * @return true - если в дереве всех результатов
	 * проверки содержится объект с ключом a_key,
	 * false - иначе
	 */
	public boolean containsKey (Key a_key)
	{
		return containsKey(m_root, a_key);
	}
	
	/**
	 * @param a_parent
	 * 		  Корень дерева результатов проверки
	 * @param a_key
	 * 		  Ключ для поиска
	 * @return true - если в дереве результатов проверки
	 * с корнем a_parent содержится объект с ключом a_key,
	 * false - иначе
	 */
	protected boolean containsKey (ValidationResult a_parent, Key a_key)
	{
		List<ValidationResult> entries = a_parent.getNodes();
		if (a_parent.getKey() == a_key) return true;
		for (ValidationResult entry : entries)
		{
			if (containsKey(entry, a_key)) return true;
		}
		return false;
	}
	
	/**
	 * @return корень дерева всех результатов проверки
	 */
	public ValidationResult getValidationResultRoot ()
	{
		return m_root;
	}
	
	/**
	 * @return код результата проверки всех ресурсов
	 */
	public Code getValidationResultCode ()
	{
		return m_root.getGenaralCode();
	}
	
	/**
	 * @return количество ошибок, найденных в ресурсах
	 */
	public int getErrorCount ()
	{
		return m_root.getErrorCount();
	}
	
	/**
	 * Записывает все данные вложений объекта a_parent
	 * и данные самого объекта a_parent в отчёт.
	 * @param a_parent
	 * 		  Корень дерева результатов проверки для
	 * 		  записи в отчёт данных входящих в дерево
	 * 		  узлов
	 */
	protected void writeAllEntries (ValidationResult a_parent)
	{
		writeValidationResult(a_parent);
		List<ValidationResult> parentEntries = a_parent.getNodes();
		for (ValidationResult result : parentEntries)
		{
			writeAllEntries(result);
		}
	}
}
