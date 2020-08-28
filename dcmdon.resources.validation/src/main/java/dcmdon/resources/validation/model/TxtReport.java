package dcmdon.resources.validation.model;

import java.util.List;

import dcmdon.resources.validation.model.ValidationResult.Key;

public class TxtReport extends ValidationReport
{
	StringBuilder m_report = new StringBuilder();
	
	public TxtReport(ValidationResult a_root)
	{
		super(a_root);
	}

	@Override
	public String getText()
	{
		writeAllEntries(m_root);
		return m_report.toString();
	}
	
	private void writeValidationResult (ValidationResult a_result)
	{
		ValidationResult parent = a_result.getParent();
		
		if ((!a_result.isRoot() && parent == a_result.getRoot()) ||
			 parent != null && parent.getKey() == Key.INITIAL_INFORMATION)
		{
			m_report.append(System.lineSeparator());
		}
		m_report.append(a_result.getPrefix() + a_result.getValue() + a_result.getPostfix());
		m_report.append(System.lineSeparator());
	}
	
	private void writeAllEntries (ValidationResult a_parent)
	{
		writeValidationResult(a_parent);
		List<ValidationResult> parentEntries = a_parent.getEntries();
		for (ValidationResult result : parentEntries)
		{
			writeAllEntries(result);
		}
	}
}
