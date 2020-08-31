import java.net.*;
import java.io.*;
import javax.swing.text.html.*;
import javax.swing.text.html.HTML.*;
import javax.swing.text.html.HTMLEditorKit.*;
import javax.swing.text.html.parser.*;
import javax.swing.text.*;
import java.util.*;

class PageWords {
	public HashMap<String, List<Integer> > m_title = new HashMap<String, List<Integer> >();
	public HashMap<String, List<Integer> > m_text = new HashMap<String, List<Integer> >();
	public HashMap<String, List<Integer> > m_URL = new HashMap<String, List<Integer> >();
	public HashMap<String, List<Integer> > m_links = new HashMap<String, List<Integer> >();

	boolean saveTitle(String text) {
		String[] words = text.split("[0-9\\W]+");

		for (int i = 0; i < words.length; i++) {
			String curWord = words[i];
			if (curWord == null || curWord.equals(""))
			{
				continue;
			}
			curWord = curWord.toLowerCase();
			if (null == m_title.get(curWord)){
				List<Integer> lstTpPos = new ArrayList<Integer>();
				lstTpPos.add(i);
				m_title.put(curWord, lstTpPos);
			}else{
				List<Integer> lstTpPos = m_title.get(curWord);
				lstTpPos.add(i);
				m_title.put(curWord, lstTpPos);
			}
		}

		return true;
	}
	boolean saveText(String text) {
		String[] words = text.split("[0-9\\W]+");

		for (int i = 0; i < words.length; i++) {
			String curWord = words[i];
			if (curWord == null || curWord.equals(""))
			{
				continue;
			}
			curWord = curWord.toLowerCase();
			if (null == m_text.get(curWord)){
				List<Integer> lstTpPos = new ArrayList<Integer>();
				lstTpPos.add(i);
				m_text.put(curWord, lstTpPos);
			}else{
				List<Integer> lstTpPos = m_text.get(curWord);
				lstTpPos.add(i);
				m_text.put(curWord, lstTpPos);
			}
		}

		return true;
	}
	boolean saveURL(String text) {
		String[] words = text.split("[0-9\\W]+");

		for (int i = 0; i < words.length; i++) {
			String curWord = words[i];
			if (curWord == null || curWord.equals(""))
			{
				continue;
			}
			curWord = curWord.toLowerCase();
			if (null == m_URL.get(curWord)){
				List<Integer> lstTpPos = new ArrayList<Integer>();
				lstTpPos.add(i);
				m_URL.put(curWord, lstTpPos);
			}else{
				List<Integer> lstTpPos = m_URL.get(curWord);
				lstTpPos.add(i);
				m_URL.put(curWord, lstTpPos);
			}
		}

		return true;
	}
	boolean saveLinks(String text) {
		String[] words = text.split("[0-9\\W]+");

		for (int i = 0; i < words.length; i++) {
			String curWord = words[i];
			if (curWord == null || curWord.equals(""))
			{
				continue;
			}
			curWord = curWord.toLowerCase();
			if (null == m_links.get(curWord)){
				List<Integer> lstTpPos = new ArrayList<Integer>();
				lstTpPos.add(i);
				m_links.put(curWord, lstTpPos);
			}else{
				List<Integer> lstTpPos = m_links.get(curWord);
				lstTpPos.add(i);
				m_links.put(curWord, lstTpPos);
			}
		}

		return true;
	}
}
public class HelloWorld {
	public HashMap<String, List<String> > wordsByUrl = new HashMap<String, List<String> >();
	public HashMap<String, String> m_titleBook = new HashMap<String, String>();
	/* url, pageInfo */
	public HashMap<String, PageWords > m_pageInfo = new HashMap<String, PageWords >();
	public List<String> urlPool = new ArrayList<String>();
	public List<String> processedUrlPool = new ArrayList<String>();
	public int urlPoolMax = 10;
	public int processedUrlPoolMax = 3;

	public List<String> blacklistUrls = new ArrayList<String>();
	public List<String> blacklistWords = new ArrayList<String>();

	/* local data */
	public String urlBook[] = null;
	public Map<String, List< Integer > > findUrlIdByKey = new HashMap<String, List< Integer > >();
	/* key keyInfo */
	public HashMap<String, KeyInfo > m_keysRecord = new HashMap<String, KeyInfo >();


	boolean readBlacklist(String fileUrls, String fileWords) {
		BufferedReader reader = null;
		/* urls */
		try {
			FileInputStream fileInputStream = new FileInputStream(fileUrls);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String[] tpFrag = tempString.split("\\*");
				System.out.println("DEBUG url: " + tpFrag[0]);
				if (!blacklistUrls.contains(tpFrag[0]))
					blacklistUrls.add(tpFrag[0]);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		/* words */
		try {
			FileInputStream fileInputStream = new FileInputStream(fileWords);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				if (!blacklistWords.contains(tempString))
					blacklistWords.add(tempString);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}


	boolean saveBook(String bookFile) {
		BufferedWriter writer = null;
        File file = new File(bookFile);
        // new
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        // write
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,false), "UTF-8"));

            writer.write("Book," + urlBook.length + "\n");
	        for (int i = 0; i < urlBook.length; i++) {
	            writer.write(i + "," + urlBook[i]);
                String sCurTitle = m_titleBook.get(urlBook[i]);
                if (null != sCurTitle)
                {
                    writer.write("," + sCurTitle + "\n");
                }
	        }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
	}
	boolean saveKey(String keyFile) {
		BufferedWriter writer = null;
        File file = new File(keyFile);
        // new
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        // write
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,false), "UTF-8"));
	        for(Map.Entry<String, KeyInfo > entry : m_keysRecord.entrySet()){
			    String sTpKey = entry.getKey();
			    KeyInfo oTpKeyInfo = entry.getValue();
		        writer.write("k," + sTpKey + "\n");
                for(Map.Entry<Integer, HashMap<String, List<Integer> > > entryPage : oTpKeyInfo.m_keyPos.entrySet()){
                    int iUrlId = entryPage.getKey();
                    HashMap<String, List<Integer> > mpPageInfo = entryPage.getValue();
                    writer.write("u," + iUrlId + "\n");
                    for(Map.Entry<String, List<Integer> > entryTpye : mpPageInfo.entrySet()){
                        String sType = entryTpye.getKey();
                        List<Integer> lstPos = entryTpye.getValue();
                        writer.write(sType);
                        for (Integer iPos : lstPos) {
                            writer.write("," + iPos);
                        }
                        writer.write("\n");
                    }
                }
			}
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
	}
	boolean recordUrlsAndWords() {
		int i = 0;
		urlBook = new String[wordsByUrl.keySet().size()];
		for(Map.Entry<String, List<String> > entry : wordsByUrl.entrySet()){
		    String mapKey = entry.getKey();
		    List<String> mapValue = entry.getValue();

		    urlBook[i] = mapKey;
	        for (String str : mapValue) {
	        	List< Integer > tpCurPosList = findUrlIdByKey.get(str);
	        	if (null == tpCurPosList)
	        	{
	        		/* new */
	        		tpCurPosList = new ArrayList< Integer >();
	        	}
	        	/* record url position */
	        	tpCurPosList.add(i);
	        	findUrlIdByKey.put(str, tpCurPosList);
	        }
	        /** record key position list */
	        PageWords oPageInfo = m_pageInfo.get(mapKey);
            if (null != oPageInfo)
            {

                for(HashMap.Entry<String, List<Integer> > tpEntry : oPageInfo.m_title.entrySet()){
                    String tpKey = tpEntry.getKey();
                    List<Integer> lstTpPos = tpEntry.getValue();
                    KeyInfo oTpKeyInfo = m_keysRecord.get(tpKey);
                    if (null == oTpKeyInfo)
                    {
                        oTpKeyInfo = new KeyInfo();
                    }
                    oTpKeyInfo.saveList(i, "t", lstTpPos);
                    m_keysRecord.put(tpKey, oTpKeyInfo);
                }
                for(HashMap.Entry<String, List<Integer> > tpEntry : oPageInfo.m_text.entrySet()){
                    String tpKey = tpEntry.getKey();
                    List<Integer> lstTpPos = tpEntry.getValue();
                    KeyInfo oTpKeyInfo = m_keysRecord.get(tpKey);
                    if (null == oTpKeyInfo)
                    {
                        oTpKeyInfo = new KeyInfo();
                    }
                    oTpKeyInfo.saveList(i, "a", lstTpPos);
                    m_keysRecord.put(tpKey, oTpKeyInfo);
                }
                for(HashMap.Entry<String, List<Integer> > tpEntry : oPageInfo.m_URL.entrySet()){
                    String tpKey = tpEntry.getKey();
                    List<Integer> lstTpPos = tpEntry.getValue();
                    KeyInfo oTpKeyInfo = m_keysRecord.get(tpKey);
                    if (null == oTpKeyInfo)
                    {
                        oTpKeyInfo = new KeyInfo();
                    }
                    oTpKeyInfo.saveList(i, "U", lstTpPos);
                    m_keysRecord.put(tpKey, oTpKeyInfo);
                }
                for(HashMap.Entry<String, List<Integer> > tpEntry : oPageInfo.m_links.entrySet()){
                    String tpKey = tpEntry.getKey();
                    List<Integer> lstTpPos = tpEntry.getValue();
                    KeyInfo oTpKeyInfo = m_keysRecord.get(tpKey);
                    if (null == oTpKeyInfo)
                    {
                        oTpKeyInfo = new KeyInfo();
                    }
                    oTpKeyInfo.saveList(i, "l", lstTpPos);
                    m_keysRecord.put(tpKey, oTpKeyInfo);
                }
            }
	        /** */
		    ++i;
		}
		return true;
	}

	static String loadPlainText(String urlString) throws IOException {
		MyParserCallback callback = new MyParserCallback();
		ParserDelegator parser = new ParserDelegator();

		URL url = new URL(urlString);
		InputStreamReader reader = new InputStreamReader(url.openStream());
		parser.parse(reader, callback, true);

		System.out.println("DEBUG POS: " + callback.wordsPos.entrySet());

		return callback.content;
	}
	public List<String> getWordsPos(String text) {
		String[] words = text.split("[0-9\\W]+");
		ArrayList<String> uniqueWords = new ArrayList<String>();
		// System.out.println("words begin: " + text);

		for (String w : words) {
			if (w == null || w.equals(""))
			{
				continue;
			}
			w = w.toLowerCase();
			// System.out.println(w);

			if ((!uniqueWords.contains(w)) && (!blacklistWords.contains(w)))
				uniqueWords.add(w);
		}
		// System.out.println("words end");

		uniqueWords.sort(new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				return a.compareTo(b);
			}
		});

		return uniqueWords;
	}
	public List<String> getUniqueWords(String text) {
		String[] words = text.split("[0-9\\W]+");
		ArrayList<String> uniqueWords = new ArrayList<String>();
		// System.out.println("words begin: " + text);

		for (String w : words) {
			if (w == null || w.equals(""))
			{
				continue;
			}
			w = w.toLowerCase();
			// System.out.println(w);

			if ((!uniqueWords.contains(w)) && (!blacklistWords.contains(w)))
				uniqueWords.add(w);
		}
		// System.out.println("words end");

		uniqueWords.sort(new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				return a.compareTo(b);
			}
		});

		return uniqueWords;
	}
	static boolean isAbsURL(String str) {
		return str.matches("^[a-z0-9]+://.+");
	}
	static URL toAbsURL(String str, URL ref) throws MalformedURLException {
		URL url = null;

		String prefix = ref.getProtocol() + "://" + ref.getHost();

		if (ref.getPort() > -1)
			prefix += ":" + ref.getPort();

		if (!str.startsWith("/")) {
			int len = ref.getPath().length() - ref.getFile().length();
			String tmp = "/" + ref.getPath().substring(0, len) + "/";
			prefix += tmp.replace("//", "/");
		}
		url = new URL(prefix + str);

		return url;
	}
	List<String> getURLs(String srcPage) throws IOException {
		URL url = new URL(srcPage);
		InputStreamReader reader = new InputStreamReader(url.openStream());

		ParserDelegator parser = new ParserDelegator();
		MyParserCallback callback = new MyParserCallback();
		parser.parse(reader, callback, true);
		System.out.println("DEBUG title: " + callback.m_title + "\n");
		m_titleBook.put(srcPage, callback.m_title);

		List<String> absUrls =  new ArrayList<String>();
		for (int i=0; i<callback.urls.size(); i++) {
			String str = callback.urls.get(i);
			// System.out.println("DEBUG url: " + str);
			if (!isAbsURL(str)){
				String tpStr = toAbsURL(str, url).toString();
				callback.urls.set(i, tpStr);
				if (!absUrls.contains(tpStr))
					absUrls.add(tpStr);
			}else{
				absUrls.add(str);
			}
		}

		return absUrls;
	}

	boolean chechInBlacklistUrls(String url)
	{
		for (String blackUrl : blacklistUrls) {

			if (url.contains(blackUrl))
			{
				System.out.println("DEBUG get blacklist url: " + url);
				return true;
			}
		}
		return false;
	}
	boolean processOneUrl(String url) {
		PageWords pageInfo = new PageWords();
		String content = new String();

		pageInfo.saveURL(url);
		try {
			content = loadPlainText(url);
		} catch (IOException e) {
			System.out.println("ERROR url: " + url + " message: " + e);
			urlPool.remove(url);
			return false;
		}
		List<String> words = getUniqueWords(content);
		wordsByUrl.put(url, words);
		pageInfo.saveText(content);

		List<String> urls = new ArrayList<String>();
		try {
			urls = getURLs(url);
		} catch (IOException e) {
			System.out.println("ERROR url: " + url + " message: " + e);
			urlPool.remove(url);
			return false;
		}
		for (int i=0; i<urls.size(); i++) {
			String str = urls.get(i);
			pageInfo.saveLinks(str);
			if ((!urlPool.contains(str)) &&
				(!processedUrlPool.contains(str)) &&
				(!chechInBlacklistUrls(str)))
			{
				if (urlPool.size() < urlPoolMax)
				{
					urlPool.add(str);
					System.out.println("DEBUG new url: " + str);
				}
			}
		}
		String sCurTitle = m_titleBook.get(url);
		if (null != sCurTitle)
        {
            pageInfo.saveTitle(sCurTitle);
        }
        m_pageInfo.put(url, pageInfo);
		urlPool.remove(url);
		processedUrlPool.add(url);
		if (processedUrlPool.size() >= processedUrlPoolMax)
		{
			/* end */
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HelloWorld oneWorld = new HelloWorld();
		String url = new String();
		boolean endFlag = false;

		oneWorld.readBlacklist("D:\\eclipseWorkSpace\\blacklistUrls.txt",
			"D:\\eclipseWorkSpace\\blacklistWords.txt");

		 oneWorld.urlPool.add("http://www.comp.hkbu.edu.hk");
//		oneWorld.urlPool.add("http://localhost:8080/");

		while (!oneWorld.urlPool.isEmpty())
		{
			url = oneWorld.urlPool.get(0);

			System.out.println("DEBUG process url: " + url);
			endFlag = oneWorld.processOneUrl(url);
			if (endFlag)
			{
				break;
			}
			System.out.println("DEBUG size: " + oneWorld.urlPool.size() + oneWorld.urlPool.isEmpty());
		}
		System.out.println("DEBUG size: " + oneWorld.urlPool.isEmpty() + endFlag);
		System.out.println(oneWorld.wordsByUrl.entrySet());
		System.out.println(oneWorld.wordsByUrl.keySet());
		oneWorld.recordUrlsAndWords();
        /*for (int i = 0; i < oneWorld.urlBook.length; i++) {
            System.out.println(oneWorld.urlBook[i]);
        }*/
		//System.out.println(oneWorld.findUrlIdByKey);
		oneWorld.saveBook("D:\\eclipseWorkSpace\\book.txt");
		oneWorld.saveKey("D:\\eclipseWorkSpace\\key.txt");
	}

}
