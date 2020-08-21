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
import dcmdon.resources.validation.model.file.java.Interfaces.TYPE;

/**
 * Распознаватель параметров атрибутов Id тегов Resource
 * и Property xml-файлов.
 */
public class IdParameterRecognizer implements IConstantRecognizer
{
	private DocumentBuilder m_docBuilder;
	
	/**
	 * Конструктор класса IdParameterRecognizer.
	 * Создаёт объект класса DocumentBuilder для
	 * дальнейшего создания документа Document
	 * для работы с файлом.
	 * @throws ParserConfigurationException
	 */
	public IdParameterRecognizer () throws ParserConfigurationException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		m_docBuilder = factory.newDocumentBuilder();
	}
	
	/**
	 * Возвращает список атрибутов Id тегов a_constantType
	 * файла a_xmlFilePath.
	 * @param a_constantType
	 * 		  Имя тега
	 * @param a_xmlFilePath
	 * 		  Путь к xml-файлу с атрибутами
	 * @return распознанные атрибуты
	 */
	@Override
	public List<Constant> getConstants(TYPE a_constantType,
									   String a_xmlFilePath)
									   throws SAXException,
											  IOException,
											  ParserConfigurationException
	{
		Document document = m_docBuilder.newDocument();
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
	    SAXParser parser = factory.newSAXParser();
		XMLHandler handler = new XMLHandler(document);
	    parser.parse(new File(a_xmlFilePath), handler);
	     
	    //Список тегов a_constantType:
		NodeList tags = document.getDocumentElement().
								 getElementsByTagName(a_constantType.toString());
		
		List<Constant> attributes = new ArrayList<>();
		for (int i = 0; i < tags.getLength(); i++)
		{
			Node tag = tags.item(i);
			
			int lineNumber = Integer.parseInt((String)tag.getUserData(
											  Constant.DATA_LINE_NUMBER));
			String name = Constant.NAME_ID;
			short value = -1;
			
			try
			{
				//Получение параметра атрибута Id тега tag:
				value = Short.parseShort(tag.getAttributes().
											 getNamedItem(name).
											 getNodeValue());
			}
			catch (NumberFormatException e)
			{
				throw new NumberFormatException("Строка: " + lineNumber +
												". Значение атрибута " +
												name + " должно " +
												"соответствовать типу " +
												Constant.TYPE + ".");
			}
			
			//Создание объекта константы:
			Constant attribute = new Constant(a_constantType, name, value,
											  lineNumber);
			attributes.add(attribute);
		}
		return attributes;
	}
}
