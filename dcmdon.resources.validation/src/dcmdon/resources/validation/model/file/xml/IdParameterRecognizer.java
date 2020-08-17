package dcmdon.resources.validation.model.file.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import dcmdon.resources.validation.model.file.Constant;
import dcmdon.resources.validation.model.file.IConstantRecognizer;

public class IdParameterRecognizer implements IConstantRecognizer
{
	private DocumentBuilder m_docBuilder;
	
	public IdParameterRecognizer () throws ParserConfigurationException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		m_docBuilder = factory.newDocumentBuilder();
	}
	
	@Override
	public List<Constant> getConstants(String a_constantType, String a_fileWithConstantPath) throws SAXException, IOException
	{
		Document document = m_docBuilder.parse(a_fileWithConstantPath);
		
		NodeList nodes = document.getDocumentElement().getElementsByTagName(a_constantType);
		
		List<Constant> parameters = new ArrayList<>();
		for (int i = 0; i < nodes.getLength(); i++)
		{
			Node node = nodes.item(i);
			String name = Constant.NAME_ID;
			Constant parameter = new Constant(a_constantType, name, Short.parseShort(node.
											  getAttributes().getNamedItem(name).
											  getNodeValue()));
			parameters.add(parameter);
		}
		return parameters;
	}
}
