package dcmdon.resources.validation.model.file.xml;

import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import dcmdon.resources.validation.model.file.Constant;

/**
 * Обработчик xml-файла.
 */
public class XMLHandler extends DefaultHandler
{
	//Стек элементов файла:
	private Stack<Element> m_elementStack = new Stack<Element>();
	
	private StringBuilder m_textBuffer = new StringBuilder();
	
	//Локатор для получения текущего номера строки
	private Locator m_locator;
	
	private Document m_document;

	/**
	 * Конструктор класса XMLHandler.
	 * @param a_document - документ
	 * для обработки
	 */
	public XMLHandler (Document a_document)
	{
		m_document = a_document;
	}

	@Override
	public void setDocumentLocator (Locator a_locator)
	{
		m_locator = a_locator;
	}

	@Override
	public void startElement (String a_uri, String a_localName,
							  String a_qName, Attributes a_attributes)
							  throws SAXException
	{
		addTextIfNeeded();
		Element el = m_document.createElement(a_qName);
		
		for (int i = 0; i < a_attributes.getLength(); i++)
		{
			String attrName = a_attributes.getQName(i);
			el.setAttribute(attrName, a_attributes.getValue(i));
		}
		
		//Установка пользовательских данных - номера текущей строки:
		el.setUserData(Constant.DATA_LINE_NUMBER,
				   	   String.valueOf(m_locator.getLineNumber()),
				   	   null);
		
		m_elementStack.push(el);
	}

	@Override
	public void endElement (String a_uri, String a_localName,
							String a_qName)
	{
		addTextIfNeeded();
		Element closedEl = m_elementStack.pop();
		if (m_elementStack.isEmpty())
		{
			m_document.appendChild(closedEl);
		}
		else
		{
			Element parentEl = m_elementStack.peek();
			parentEl.appendChild(closedEl);
		}
	}

	@Override
	public void characters (char a_ch[], int a_start, int a_length)
						   throws SAXException
	{
		m_textBuffer.append(a_ch, a_start, a_length);
	}
	
	/**
	 * Добавляет к элементу на вершине стека
	 * узел с содержимым буфера m_textBuffer,
	 * а затем очищает данный буфер.
	 */
	private void addTextIfNeeded ()
	{
		if (m_textBuffer.length() > 0)
		{
			Element el = m_elementStack.peek();
			
			Node textNode = m_document.createTextNode(
							m_textBuffer.toString());
			el.appendChild(textNode);
			
			m_textBuffer.delete(0, m_textBuffer.length());
		}
	}
}
