import java.net.*;
import java.util.*;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.*;


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
public class dataStorage {
	public HashMap<String, List<String> > wordsByUrl = new HashMap<String, List<String> >();
	public HashMap<String, String> m_titleBook = new HashMap<String, String>();
	/* url, pageInfo */
	public HashMap<String, PageWords > m_pageInfo = new HashMap<String, PageWords >();

	/* local data */
	public String urlBook[] = null;
	public Map<String, List< Integer > > findUrlIdByKey = new HashMap<String, List< Integer > >();
	/* key keyInfo */
	public HashMap<String, KeyInfo > m_keysRecord = new HashMap<String, KeyInfo >();

	public boolean addPageInfo(String sUrl, String sContent, List<String> lstLinks, String sTitle) {
		PageWords pageInfo = new PageWords();
		pageInfo.saveURL(sUrl);
	    List<String> lstWords = MyParserCallback.getUniqueWords(sContent);
		wordsByUrl.put(sUrl, lstWords);
		pageInfo.saveText(sContent);

		for (int i=0; i<lstLinks.size(); i++) {
			String sTpUrl = lstLinks.get(i);
			pageInfo.saveLinks(sTpUrl);
		}
		if (null != sTitle)
        {
            pageInfo.saveTitle(sTitle);
            m_titleBook.put(sUrl, sTitle);
        }
        m_pageInfo.put(sUrl, pageInfo);
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
                else
                {
                    writer.write("\n");
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
}
