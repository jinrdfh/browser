import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.parser.ParserDelegator;

public class MyParserCallback extends HTMLEditorKit.ParserCallback {

    public String content = new String();
    public String title = new String();
    public String debugTitle = new String();
    public String aText = new String();
    public List<String> urls_within_a_page = new ArrayList<String>();

    private URL startUrl;
    private String urlString;
    private List<String> urlPool;
    private List<String> processedUrl;
    private List<String> ignorelist;
    private List<String> blackUrl;
    private List<String> blackUrlWild;
	private int maxUrl;
	private int maxProcessedUrl;

//    private boolean isTargetTag = true;
    private boolean isTitleTag = false;
    private boolean isStyleTag = false;
    private boolean isScriptTag = false;
    private boolean isATag = false;

	public MyParserCallback(CallbackParams callbackParams, String urlString) {
		// TODO Auto-generated constructor stub

		urlPool = callbackParams.getUrlPool();
		processedUrl = callbackParams.getProcessedUrl();
		ignorelist = callbackParams.getIgnorelist();
		blackUrl = callbackParams.getBlackUrl();
		blackUrlWild = callbackParams.getBlackUrlWild();
		maxUrl = callbackParams.getMaxUrl();
		maxProcessedUrl = callbackParams.getMaxProcessedUrl();
		this.urlString = urlString;

	}
	public boolean connect() throws IOException {

        ParserDelegator parser = new ParserDelegator();
        URL url = new URL(urlString);
		this.startUrl = url;

        /*
         * POINT 1: Code to get ContentType and Charset
         */
        URLConnection con = startUrl.openConnection();
        InputStream is = con.getInputStream();
        String contentType = con.getContentType();
        String[] values = contentType.split(";");
        String charset = "";
        for (String value: values) {
        	value = value.trim();
        	if (value.toLowerCase().startsWith("charset=")) {
        		charset = value.substring("charset=".length());
        	}
        }
        if ("".equals(charset)) {
        	charset = "UTF-8";
        }

        if (contentType.indexOf("html") >= 0) {

//        	System.out.println("Content Type: " + con.getContentType());
        	InputStreamReader reader = new InputStreamReader(is, charset);

        	parser.parse(reader, this, true);
        	System.out.println("DEBUG title: " + title);
        	System.out.println("DEBUG debug title: " + debugTitle);
        	System.out.println("DEBUG content: " + content);
        }
        else {
//        	System.out.println("Content Type: " + con.getContentType());
        	return false;
        }
		return true;
	}


    // Override ParserCallback.handleText Function
    @Override
    public void handleText(char[] data, int pos) {
        String sCurPhrase = new String(data);
        String[] szWords = sCurPhrase.split(" ");
        String sClearPhrase = new String();
   		// System.out.println("DEBUG data " + data);

        for (int i = 0; i < szWords.length; ++i)
        {
            if (!(ignorelist.contains(szWords[i])))
            {
                sClearPhrase += " " + szWords[i];
            }
        }

    	if (!(isTitleTag||isStyleTag||isScriptTag)) {
    		content += sClearPhrase;
//    		System.out.println(data);
    	}
    	if (isTitleTag) {
    		//title = sClearPhrase;
    		title = new String(data);
//    		System.out.println(data);
            debugTitle = "debug";
    	}
    	if (isATag) {
    		aText += sClearPhrase;
    	}
    }

    // Override ParserCallback.handleStartTag Function
    @Override
    public void handleStartTag(Tag tag, MutableAttributeSet attrSet, int pos)
    {
//    	isTargetTag = !(tag == Tag.STYLE || tag == Tag.SCRIPT);
//    	System.out.println("<Start: " + tag);
    	isStyleTag = tag == Tag.STYLE;
    	isScriptTag = tag == Tag.SCRIPT;
    	isTitleTag = tag == Tag.TITLE;
    	isATag= tag == Tag.A;

    	if (isTitleTag)
        {
            debugTitle = "yes";
        }

    	if (tag == Tag.A) {

//    	}
//
//        if (tag.toString().equals("a")) {
            Enumeration e = attrSet.getAttributeNames();
            while (e.hasMoreElements()) {
                Object aname = e.nextElement();
                if (aname.toString().equals("href")) {

            		String u = attrSet.getAttribute(HTML.Attribute.HREF).toString().trim();

/*
 * POINT 2 Code to handle javascript:, mailto:, ? and # in url
 * POINT 3 Code to replace " " with "+" in url
 */
            		u = MyPgUtils.rmTail(u, "javascript:");
            		u = MyPgUtils.rmTail(u, "mailto:");
//            		u = MyPgUtils.rmTail(u, "#"); // used when including query str
            		u = MyPgUtils.rmTail(u); // used when excluding query str
            		u = u.replace(" ", "+");


        			if (!isAbsURL(u)) {

    					try {
							u = toAbsURL(u, this.startUrl).toString(); // used when including query str
						} catch (MalformedURLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

        			}

//        			if (urlPool.size() < maxUrl && !urlPool.contains(u) && !processedUrl.contains(u) && !blackUrl.contains(u)) {
//        				urlPool.add(u);
//        			}

        			if (!MyPgUtils.inBlackList(u, blackUrl, blackUrlWild)) {
        				urls_within_a_page.add(u);
                        if (urlPool.size() < maxUrl && !urlPool.contains(u) && !processedUrl.contains(u)) {
                            urlPool.add(u);
                        }
                    }

                }
            }
        }

    }

    @Override
    public void handleEndTag(Tag tag, int pos) {
//    	System.out.println(">End: " + tag + '\n');
    	if (tag == Tag.TITLE) {
    		isTitleTag = false;

    		//System.out.println(title);
    		//title = "";
            debugTitle = "end";
    	}
    	if (isStyleTag) isStyleTag = false;
    	if (isScriptTag) isScriptTag = false;
    	if (isATag) isATag = false;

    }

    public void handleComment(char[] text, int pos) {

    }

    public void handleSimpleTag(Tag tag, MutableAttributeSet attrSet, int pos) {

    }

    public void handleError(String eMessage, int pos) {
//    	System.out.println(eMessage);
    }

    // Customized Function for Parsed Text
    public String getPlainText() throws IOException {

        return content;

    }

    // Customized Function for Unique Words
    public static List<String> getUniqueWords(String text) {
        String[] words = text.split("[0-9\\W]+");
        ArrayList<String> uniqueWords = new ArrayList<String>();

        for (String w : words) {
            w = w.toLowerCase();

            if (!uniqueWords.contains(w))
                uniqueWords.add(w);
        }

        uniqueWords.sort(new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.compareTo(b);
            }
        });

        return uniqueWords;
    }

/*
    // Customized Function for Getting URLs within a Page
    public static List<String> getURLs(String srcPage) throws IOException {
//    	URL url = new URL(srcPage);
    	URL url = new URL(getRedirectUrl(srcPage));
        InputStreamReader reader = new InputStreamReader(url.openStream());

        ParserDelegator parser = new ParserDelegator();
        MyParserCallback callback = new MyParserCallback();
        parser.parse(reader, callback, true);

        for (int i=0; i<callback.urls_within_a_page.size(); i++) {
            String str = callback.urls_within_a_page.get(i);
            if (!isAbsURL(str)) {
                callback.urls_within_a_page.set(i, toAbsURL(str, url).toString());
            } else {
            }
        }

        return callback.urls_within_a_page;
    }
*/
    public static boolean isAbsURL(String str) {
        return str.matches("^[a-z0-9]+://.+");
    }

    public static URL toAbsURL(String str, URL ref) throws MalformedURLException {
        URL url = null;

        String prefix = ref.getProtocol() + "://" + ref.getHost();

        if (ref.getPort() > -1)
            prefix += ":" + ref.getPort();

        if (!str.startsWith("/")) {
//            int len = ref.getPath().length() - ref.getFile().length();

        	String tmp;
        	if (ref.getPath().equals("/")) {
        		tmp = "//";
        	} else {
        		tmp = "/" + ref.getPath() + "/";
        	}
            prefix += tmp.replace("//", "/");
        }
//        url = new URL(prefix + str);
        try {
			url = (new URL(prefix + str)).toURI().normalize().toURL();
		} catch (Exception e) {
//		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return url;
    }

    public static String rmQueryStr(URL u) throws MalformedURLException {
    	String result = u.getProtocol() + "://" + u.getHost();
        if (u.getPort() > -1)
        	result += ":" + u.getPort();
        result += u.getPath();

        if (result.indexOf("?")>=0) {
        	System.out.println("***** Path: " + u.getPath());
        }

        return result;

    }

    public static String rmQueryStr(String u) throws MalformedURLException {
    	return rmQueryStr(new URL(u));
    }


    public static String getRedirectUrl(String stringurl) throws MalformedURLException, IOException {
    	URLConnection con = new URL( stringurl ).openConnection();
    	con.connect();
    	InputStream is = con.getInputStream();
    	is.close();
    	return con.getURL().toString();
    }

}
