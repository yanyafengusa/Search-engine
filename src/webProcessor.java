
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class webProcessor {
	public static class htmlParseOperation {
		public String htlmToContent(Document inputString) {// convert the html to content
			String htmlStr = inputString.toString();
			String textStr = "";
			Pattern pScript, pStyle, pHtml, pFilte;
			Matcher mScript, mStyle, mHtmls, mFilter;
			String regEx_script = "<script[^>]*?>[\\s\\S]*?</script>";
			String regEx_style = "<style[^>]*?>[\\s\\S]*?</style>";
			String regEx_html = "<[^>]+>";
			String[] filter = { "&quot;", "&nbsp;" };
			pScript = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			mScript = pScript.matcher(htmlStr);
			htmlStr = mScript.replaceAll("");
			pStyle = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			mStyle = pStyle.matcher(htmlStr);
			htmlStr = mStyle.replaceAll("");
			pHtml = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			mHtmls = pHtml.matcher(htmlStr);
			htmlStr = mHtmls.replaceAll("");
			for (String s : filter) {
				pFilte = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
				mFilter = pFilte.matcher(htmlStr);
				htmlStr = mFilter.replaceAll("");
			}
			textStr = htmlStr;
			return textStr;
		}

		public HashSet<URL> urlFinder(Document doc) throws MalformedURLException {// find all the urls
			HashSet<URL> result_urls = new HashSet<>();
			Elements newsHeadlines = doc.select("#mp-itn b a");
			for (Element headline : newsHeadlines) {
				result_urls.add(new URL(headline.absUrl("href")));
				if (result_urls.size() >= 5) {
					break;
				}
			}
			for (URL s : result_urls) {
				System.out.println(s);
			}
			return result_urls;
		}
	}

	public HashSet<URL> goAnalyzer(BufferedWriter bufferWriter, URL url, Document htmlDocument) throws IOException {// do the analyse
		System.out.println("The total length of document is: " + htmlDocument.toString().length());
		System.out.println();
		HashSet<URL> urlInHtmlDoc = (new htmlParseOperation()).urlFinder(htmlDocument);
		String s = htmlDocument.toString();
		saveDocument(bufferWriter, url, s);
		return urlInHtmlDoc;
	}

	public void writeOperarion(BufferedWriter bfw, URL url, Document afterDoc) throws IOException {// write something to documents
		String analysed = (new htmlParseOperation()).htlmToContent(afterDoc);
		analysed = analysed.replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1").replaceAll("^((\r\n)|\n)", "");
		saveDocument(bfw, url, analysed);
	}

	private void saveDocument(BufferedWriter bufferWriter, URL url, String htmlDocument) throws IOException {// save the ducument to project
		Date date = new Date();
		String dateStr = "date:" + date.toString() + "\n";
		InetAddress address = InetAddress.getByName(url.getHost());
		String ipString = address.toString();
		ipString = "IP:" + ipString.substring(ipString.indexOf("/") + 1, ipString.length()) + "\n";
		String htmlLen = "length:" + htmlDocument.length() + "\n";
		bufferWriter.append(url.toString() + "\n");
		bufferWriter.append(dateStr);
		bufferWriter.append(ipString);
		bufferWriter.append(htmlLen);
		bufferWriter.newLine();
		bufferWriter.append(htmlDocument);
		bufferWriter.newLine();
		bufferWriter.flush();
	}
}
