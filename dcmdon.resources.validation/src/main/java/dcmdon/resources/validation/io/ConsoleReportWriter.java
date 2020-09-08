package dcmdon.resources.validation.io;

public class ConsoleReportWriter implements ReportWriter
{
	@Override
	public void write(String a_text)
	{
		System.out.println();
		System.out.print(a_text);
	}
}
