import java.net.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.*;

class URLExtractionAlgorithm{

	// ---------------------------------------------------
	// Handle URLs
	// ---------------------------------------------------
	public String seedURL = "http://www.comp.hkbu.edu.hk/";
//	public String seedURL = "https://sles.hkbu.edu.hk/";
//	public String seedURL = "http://www.comp.hkbu.edu.hk/v1/?page=job_vacancies";
//	String seedurl = "http://www.sci.hkbu.edu.hk";
	public int X_URLs = 10;
	public int Y_URLs = 100;
	public String m_ignoreWords = "src/ignorelist";
	public String m_blacUrl = "src/blackUrl";
	public String sUrlFileName = "D:\\eclipseWorkSpace\\book.txt";
	public String sKeyFileName = "D:\\eclipseWorkSpace\\key.txt";
	public static List<String> URL_Pool = new ArrayList<String>();
	public static List<String> Processed_URL_Pool = new ArrayList<String>();

	// ---------------------------------------------------
	// Handle Text of A Page
	// ---------------------------------------------------
	public String content = new String();
//	public static List<String> Blacklist_of_Words = new ArrayList<String>();
//	public static List<String> Blacklist_of_Urls = new ArrayList<String>();
	public static List<String> Ignore_of_Words = new ArrayList<String>();


    public static String getRedirectUrl(String stringurl) throws MalformedURLException, IOException {
    	URLConnection con = new URL( stringurl ).openConnection();
    	con.connect();
    	InputStream is = con.getInputStream();
    	is.close();
    	return con.getURL().toString();
    }


	public void Algorithm(dataStorage oLocalData) throws IOException {

//		Blacklist_of_Urls = MyFileUtils.getArrayFromFile("blackUrl");
//		Ignore_of_Words = MyFileUtils.getArrayFromFile("ignorelist");

		// Handle the seedurl
//		URL_Pool.add(getRedirectUrl(seedURL));
		URL_Pool.add(seedURL);
		CallbackParams callbackParams = new CallbackParams(X_URLs, Y_URLs, m_ignoreWords, m_blacUrl,
                                                     URL_Pool, Processed_URL_Pool);


		int pcount = 0;
		while (Processed_URL_Pool.size() < Y_URLs && URL_Pool.size() > 0) {
			pcount++;

			String ta = URL_Pool.get(0);
			if (ta.lastIndexOf("lang=tc") >= 0 || ta.lastIndexOf("lang=sc") >= 0 || ta.indexOf("www.facebook.com") >= 0) System.out.println(ta);
			System.out.println("Current url: " + ta);
			Processed_URL_Pool.add(ta);
			URL_Pool.remove(ta);


//		MyParserCallback Parser = new MyParserCallback(callbackParams, new URL(ta));

			try {
				MyParserCallback oUrlCallback = new MyParserCallback(callbackParams, ta);
				if (oUrlCallback.connect()){
                    oLocalData.addPageInfo(ta, oUrlCallback.content, oUrlCallback.urls_within_a_page, oUrlCallback.title);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}

//		Check urlPool and processed Url for each parse
//			if(pcount < 70)
			System.out.println("--- Iteration: " + pcount + "; URL_Pool Size: " + URL_Pool.size() +
                      "; Processed_URL_Pool Size: " +
                      Processed_URL_Pool.size());
//			System.out.println("... Contents of URL_Pool ...");
//			for (int i=0; i<URL_Pool.size(); i++) {
//				System.out.println(URL_Pool.get(i));
//			}
//			System.out.println("... Contents of Processed_URL_Pool ......");
//			for (int i=0; i<Processed_URL_Pool.size(); i++) {
//				System.out.println(Processed_URL_Pool.get(i));
//			}

		}

		System.out.println("... Contents of Processed_URL_Pool ......");
		for (int i=0; i<Processed_URL_Pool.size(); i++) {
			System.out.println(Processed_URL_Pool.get(i));
		}
		/*
		List<String> t1 = new ArrayList<String>();
		t1 = Parser.getURLs(ta);
		for(int i = 0; i < t1.size(); i++) {
			if (!URL_Pool.contains(t1.get(i)) && URL_Pool.size() < X_URLs && !Processed_URL_Pool.contains(t1.get(i))) {
				URL_Pool.add(t1.get(i));
			}
		}


		// All Rounds
		while(URL_Pool.size() != 0 ) {
			if(Processed_URL_Pool.size() >= Y_URLs) {
				break;
			}
			// First in First Out -> URL_Pool
			String tb = URL_Pool.get(0);
			Processed_URL_Pool.add(tb);
			URL_Pool.remove(tb);
			List<String> t2 = new ArrayList<String>();
			t2 = Parser.getURLs(tb);
			for(int i = 0; i < t2.size(); i++) {
				if (!URL_Pool.contains(t2.get(i)) && URL_Pool.size() < X_URLs && !Processed_URL_Pool.contains(t2.get(i))) {
					System.out.println("URL_Pool pops: "+tb+ ", then URL_Pool size: "+URL_Pool.size()+", new urls: "+t2.get(i));
					URL_Pool.add(t2.get(i));
				}
			}
		}
		*/

	}

}


public class main {

    // main
    public static void main(String[] args) throws Exception {
//    	URL u = new URL("http://baeldung.com/articles/index.php?topic=java&version=8");
//    	System.out.println("Path: " + u.getPath());
//    	System.out.println("File: " + u.getFile());
//    	System.out.println("Query: " + u.getQuery());
    	URLExtractionAlgorithm URLs = new URLExtractionAlgorithm();

    	dataStorage oLocalData = new dataStorage();
    	URLs.Algorithm(oLocalData);
    	oLocalData.recordUrlsAndWords();
		oLocalData.saveBook(URLs.sUrlFileName);
		oLocalData.saveKey(URLs.sKeyFileName);
    }
}
