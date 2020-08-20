package dcmdon.resources.validation.model.file;

import java.util.List;

public interface IConstantRecognizer
{
	List<Constant> getConstants (String a_constantType,
								 String a_fileWithConstantPath) throws Exception;
}
