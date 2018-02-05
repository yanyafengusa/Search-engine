
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class pre_Search {
	public static class findPage {
		// Deal with the created documents
		public static class DealDoc implements Runnable {
			private processorP disp;
			private webProcessor analyzer = new webProcessor();// web analyzer
			private File file;
			private File afterfile; // the processed file
			private BufferedWriter bfWriter;
			private BufferedWriter bfWriter2;

			public DealDoc(String ID, processorP dis) { // initiate the
														// documents
				this.disp = dis;
				file = new File("OriginalDoc//HTMLPAGE_" + ID + ".txt");
				afterfile = new File("DocAfterAna//processedHTML_" + ID + ".txt");
				try {
					file.createNewFile();
					afterfile.createNewFile();
					bfWriter = new BufferedWriter(new FileWriter(file));
					bfWriter2 = new BufferedWriter(new FileWriter(afterfile));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			public void run() {
				int counter = 0;
				while (counter++ <= 2) {
					URL url = disp.findURLS();
					System.out.println("The programming is working to analyse:" + url.toString());
					Document htmlDoc = null;
					try {
						htmlDoc = Jsoup.connect("https://en.wikipedia.org/wiki/Main_Page").get(); // connect
																									// the
																									// pages
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (htmlDoc != null) {
						HashSet<URL> newURL = null;
						try {
							newURL = analyzer.goAnalyzer(bfWriter, url, htmlDoc);// do
																					// the
																					// analyze
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							analyzer.writeOperarion(bfWriter2, url, htmlDoc);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (newURL.size() != 0)
							disp.inserUrls(newURL);
					}
				}
			}
		}

		private HashSet<URL> urls;// used set to store the urls
		private int gatherNum = 5;

		public findPage() {

		}

		public findPage(HashSet<URL> urls) {// find all pages
			this.urls = urls;
		}

		public void start() {
			processorP disp = new processorP(urls);

			for (int i = 0; i < gatherNum; i++) {
				Thread gather = new Thread(new DealDoc(String.valueOf(i + 1), disp));
				gather.start();
			}
		}

		public String getDocumentAt(URL url) {// get the documents
			StringBuffer document = new StringBuffer();

			// read the documents
			try {
				URLConnection conn = url.openConnection();
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line = "";
				document = new StringBuffer();

				while ((line = reader.readLine()) != null) {
					if (!line.trim().isEmpty())
						document.append(line + "\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return document.toString();
		}
	}

	public static void main(String[] args) throws IOException {
		HashSet<URL> urls = new HashSet<URL>();
		urls.add(new URL("https://en.wikipedia.org/wiki/Main_Page"));// create
																		// an
																		// url
		findPage page = new findPage(urls); // find pages on that page
		page.start();
		processorP disp = new processorP(urls); // process the url
		webProcessor web = new webProcessor(); // do web alalyze
	}
}
