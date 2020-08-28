package dcmdon.resources.validation.model;

import java.util.Objects;

public abstract class ValidationReport
{
	protected ValidationResult m_root;
	
	public ValidationReport (ValidationResult a_root)
	{
		m_root = Objects.requireNonNull(a_root);
	}
	
	public abstract String getText ();
	
	public ValidationResult getValidationResultRoot ()
	{
		return m_root;
	}
	
	public int getValidationResultCode ()
	{
		return m_root.getGenaralCode().getValue();
	}
}
