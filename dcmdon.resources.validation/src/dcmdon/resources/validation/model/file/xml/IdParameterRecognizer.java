package dcmdon.resources.validation.model.file.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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
	public List<Constant> getConstants(String a_constantType, String a_fileWithConstantPath) throws SAXException, IOException, ParserConfigurationException
	{
		Document document = m_docBuilder.newDocument();
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
	    SAXParser parser = factory.newSAXParser();
		XMLHandler handler = new XMLHandler(document);
	    parser.parse(new File(a_fileWithConstantPath), handler);
	     
		NodeList nodes = document.getDocumentElement().getElementsByTagName(a_constantType);
		
		List<Constant> parameters = new ArrayList<>();
		for (int i = 0; i < nodes.getLength(); i++)
		{
			Node node = nodes.item(i);
			
			int lineNumber = Integer.parseInt((String)node.getUserData(Constant.DATA_LINE_NUMBER));
			int columnNumber =  Integer.parseInt((String)node.getUserData(Constant.DATA_COLUMN_NUMBER));
			String name = Constant.NAME_ID;
			short value = -1;
			
			try
			{
				value = Short.parseShort(node.getAttributes().getNamedItem(name).
						  		 		 getNodeValue());
			}
			catch (NumberFormatException e)
			{
				throw new NumberFormatException("Строка: " + lineNumber +
												". Столбец: " + columnNumber +
												". Значение атрибута " +
												name + " должно соответствовать " +
												"типу " + Constant.TYPE + ".");
			}
			
			Constant parameter = new Constant(a_constantType, name, value,
											  lineNumber, columnNumber);
			parameters.add(parameter);
		}
		return parameters;
	}
}
