
import java.net.URL;
import java.util.HashSet;

public class processorP {
	private static HashSet<URL> urls = new HashSet<URL>();  // all urls found
	private static HashSet<URL> urlsVisited = new HashSet<URL>(); // all visited urls

	public processorP(HashSet<URL> urls) {
		processorP.urls = urls;
	}

	public synchronized URL findURLS() {// find all urls
		while (urls.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.notify();
		URL url = null;
		for (URL u : urls) {
			url = u;
			break;
		}
		urlsVisited.add(url);// add to visited urls
		urls.remove(url);
		return url;
	}

	public synchronized void inserUrls(URL url) { // synchronly do the insert urls
		if (!urls.contains(url) && !urlsVisited.contains(url))
			urls.add(url);
	}

	public synchronized void inserUrls(HashSet<URL> analyzedURL) {
		for (URL url : analyzedURL) {
			if (!urls.contains(url) && !urlsVisited.contains(url))
				urls.add(url);
		}
	}

}
