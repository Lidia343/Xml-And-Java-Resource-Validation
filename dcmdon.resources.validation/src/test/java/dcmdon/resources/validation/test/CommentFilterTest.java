package dcmdon.resources.validation.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import dcmdon.resources.validation.model.file.java.CommentFilter;
import dcmdon.resources.validation.util.Util;

/**
 * Тест метода "getFilteredText" класса CommentFilter.
 */
public class CommentFilterTest
{
	private final String m_fileWithComments = "src/test/resources/valid/file_with_comments.java";
	private final String m_fileWithoutComments = "src/test/resources/valid/file_without_comments.java";;
	
	/**
	 * Проверяет на равенство строку, полученную путём
	 * фильтрации от комментариев содержимого одного
	 * файла, и содержимое другого, аналогичного файла,
	 * но без комментариев.
	 * @throws IOException
	 */
	@Test
	public void test () throws IOException
	{
		String textWithComments = Util.getText(m_fileWithComments);
		String textWithoutComments = Util.getText(m_fileWithoutComments);
		
		String filteredText = new CommentFilter().getFilteredText(textWithComments);
		//System.out.println(filteredText);
		assertEquals(filteredText, textWithoutComments);
	}
}
