package dcmdon.resources.validation.model;

import dcmdon.resources.validation.model.ValidationResult.Key;

/**
 * Текстовый отчёт о результатах проверки ресурсов.
 */
public class TxtReport extends ValidationReport
{
	StringBuilder m_report = new StringBuilder();
	
	/**
	 * Конструктор класса TxtReport.
	 * @param a_root
	 * 		  Результат проверки, являющийся
	 * 		  корнем дерева, содержащего все
	 * 		  результаты проверки ValidationResult
	 */
	public TxtReport (ValidationResult a_root)
	{
		super(a_root);
		writeAllEntries(m_root);
		writeErrorCount();
	}

	@Override
	public String getText ()
	{
		return m_report.toString();
	}
	
	@Override
	protected void writeValidationResult (ValidationResult a_result)
	{
		ValidationResult parent = a_result.getParent();
		
		if ((!a_result.isRoot() && parent == a_result.getRoot()) ||
			 parent != null && parent.getKey() == Key.INITIAL_INFORMATION)
		{
			m_report.append(System.lineSeparator());
		}
		m_report.append(a_result.getPrefix() + a_result.getValue() +
						a_result.getPostfix());
		m_report.append(System.lineSeparator());
	}
	
	private void writeErrorCount ()
	{
		m_report.append(System.lineSeparator() +
						"Количество найденных ошибок: " +
				 		getErrorCount() + "." +
				 		System.lineSeparator());
	}
}