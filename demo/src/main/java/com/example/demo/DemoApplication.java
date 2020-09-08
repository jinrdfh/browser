package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;
import java.util.*;
import java.net.*;
import java.io.*;

@SpringBootApplication
@RestController
public class DemoApplication {
	/* local data */
	public String urlBook[] = null;
	public String titleBook[] = null;
	public Map<String, List< Integer > > findUrlIdByKey = new HashMap<String, List< Integer > >();
	public boolean readDataFlag = false;
	public HashMap<String, KeyInfo> m_keysRecord = new HashMap<String, KeyInfo>();

	/** modify */
	public String sUrlFileName = "D:\\eclipseWorkSpace\\book.txt";
	public String sKeyFileName = "D:\\eclipseWorkSpace\\key.txt";
	/** */

	boolean readBook(String fileName) {
		BufferedReader reader = null;
		String tpState = "start";
		int iBookLen = 0;
		try {
			FileInputStream fileInputStream = new FileInputStream(fileName);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String[] words = tempString.split(",", 3);
				if (tpState.equals("start"))
				{
					if (words[0].equals("Book"))
					{
						/* right file */
						iBookLen = Integer.valueOf(words[1]);
						urlBook = new String[iBookLen];
						titleBook = new String[iBookLen];
						/* next state */
						tpState = "Book";
					}
					else
					{
						System.out.println("ERROR wrong file: " + fileName + " word: " + words[0]);
						return false;
					}
				}
				else if (tpState.equals("Book"))
				{
                    int urlId = Integer.valueOf(words[0]);
                    urlBook[urlId] = words[1];
                    titleBook[urlId] = words[2];
				}
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

	boolean readKey(String fileName) {
		BufferedReader reader = null;
		int iBookLen = 0;
		readDataFlag = true;
		try {
			FileInputStream fileInputStream = new FileInputStream(fileName);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			String sCurKey = null;
			int iCurUrlId = -1;
			KeyInfo oCurKey = new KeyInfo();
			HashMap<String, ArrayList<Integer> > mpCurPage = new HashMap<String, ArrayList<Integer> >();
			ArrayList<Integer> lstPos = new ArrayList<Integer>();
			while ((tempString = reader.readLine()) != null) {
				String[] words = tempString.split(",");
                //System.out.println("DEBUG line: " + words[0]);
				if (words[0].equals("k"))
                {
                    /* get new key */
                    /* handle prev key */
                    if (null != sCurKey)
                    {
                        if (-1 != iCurUrlId)
                        {
                            oCurKey.m_keyPos.put(iCurUrlId, mpCurPage);
                        }
                        m_keysRecord.put(sCurKey, oCurKey);
                    }
                    sCurKey = words[1];
                    oCurKey = new KeyInfo();
                    iCurUrlId = -1;
                    //System.out.println("DEBUG get key: " + sCurKey);
                }
                else if (words[0].equals("u"))
                {
                    /* get url id */
                    /* handle prev url */
                    if (-1 != iCurUrlId)
                    {
                        oCurKey.m_keyPos.put(iCurUrlId, mpCurPage);
                    }
                    iCurUrlId = Integer.valueOf(words[1]);
                    mpCurPage = new HashMap<String, ArrayList<Integer> >();
                }
                else if (words[0].equals("t"))
                {
                    /* title */
                    lstPos = new ArrayList<Integer>();
                    for (int i = 1; i < words.length; ++i) {
                        int iTpPos = Integer.valueOf(words[i]);
                        lstPos.add(iTpPos);
                    }
                    mpCurPage.put("t", lstPos);
                }
                else if (words[0].equals("a"))
                {
                    /* title */
                    lstPos = new ArrayList<Integer>();
                    for (int i = 1; i < words.length; ++i) {
                        int iTpPos = Integer.valueOf(words[i]);
                        lstPos.add(iTpPos);
                    }
                    mpCurPage.put("a", lstPos);
                }
                else if (words[0].equals("U"))
                {
                    /* title */
                    lstPos = new ArrayList<Integer>();
                    for (int i = 1; i < words.length; ++i) {
                        int iTpPos = Integer.valueOf(words[i]);
                        lstPos.add(iTpPos);
                    }
                    mpCurPage.put("U", lstPos);
                }
                else if (words[0].equals("l"))
                {
                    /* title */
                    lstPos = new ArrayList<Integer>();
                    for (int i = 1; i < words.length; ++i) {
                        int iTpPos = Integer.valueOf(words[i]);
                        lstPos.add(iTpPos);
                    }
                    mpCurPage.put("l", lstPos);
                }
			}
            if (null != sCurKey)
            {
                if (-1 != iCurUrlId)
                {
                    oCurKey.m_keyPos.put(iCurUrlId, mpCurPage);
                }
                m_keysRecord.put(sCurKey, oCurKey);
            }
            //System.out.println("DEBUG read key book size: " + m_keysRecord.size());
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
    ArrayList<Integer> searchPos(String sQueryWord, String sType) {
        ArrayList<Integer> lstUrlId = new ArrayList<Integer>();
        KeyInfo oKeyNode = m_keysRecord.get(sQueryWord);
        if (null != oKeyNode)
        {
            for(Map.Entry<Integer, HashMap<String, ArrayList<Integer> > > entryPage : oKeyNode.m_keyPos.entrySet()){
                int iUrlId = entryPage.getKey();
                HashMap<String, ArrayList<Integer> > mpPageInfo = entryPage.getValue();
                List<Integer> lstPos = mpPageInfo.get(sType);
                if ((null != lstPos) && (0 < lstPos.size()))
                {
                    lstUrlId.add(iUrlId);
                }
            }
        }
        return lstUrlId;
    }
    ArrayList<Integer> searchPos(String sQueryWord, Integer iUrlId, String sType) {
        ArrayList<Integer> lstPos = new ArrayList<Integer>();
        KeyInfo oKeyNode = m_keysRecord.get(sQueryWord);
        if (null != oKeyNode)
        {
            HashMap<String, ArrayList<Integer> > mpPageInfo = oKeyNode.m_keyPos.get(iUrlId);
            lstPos = mpPageInfo.get(sType);
        }
        return lstPos;
    }
    ArrayList<Integer> searchPos(String[] sQueryWords, String sType) {
        ArrayList<Integer> lstUrlId = new ArrayList<Integer>();
        /* id cnt */
        HashMap<Integer, Integer> mpCnt = new HashMap<Integer, Integer>();
        for (int i = 0; i < sQueryWords.length; ++i)
        {
            ArrayList<Integer> lstTpUrls = searchPos(sQueryWords[i], sType);
            System.out.println("DEBUT word: " + sQueryWords[i] + " size: " + lstTpUrls.size());
            for (Integer iUrlId : lstTpUrls)
            {
                Integer iCurCnt = mpCnt.get(iUrlId);
                if (null == iCurCnt)
                {
                    mpCnt.put(iUrlId, 1);
                }
                else
                {
                    iCurCnt = iCurCnt + 1;
                    mpCnt.put(iUrlId, iCurCnt);
                }
            }
        }
        ArrayList<Integer> lstAllUrlId = new ArrayList<Integer>();
        for(Map.Entry<Integer, Integer> entryCnt : mpCnt.entrySet()){
            int iUrlId = entryCnt.getKey();
            int iCnt = entryCnt.getValue();
            System.out.println("DEBUT URLId: " + iUrlId + " cnt: " + iCnt);
            if (iCnt == sQueryWords.length)
            {
                lstAllUrlId.add(iUrlId);
            }
        }
        /* filter */
        for(Integer iTpUrlId : lstAllUrlId)
        {
            ArrayList<ArrayList<Integer> > lstlstPos = new ArrayList<ArrayList<Integer> >();
            int iMaxPos = 0;
            for (int i = 0; i < sQueryWords.length; ++i)
            {
                ArrayList<Integer> lstPos = searchPos(sQueryWords[i], iTpUrlId, sType);
                int iCurMax = Collections.max(lstPos);
                if (iCurMax > iMaxPos)
                {
                    iMaxPos = iCurMax;
                }
                lstlstPos.add(lstPos);
            }
            boolean res = cmpPos(lstlstPos, sQueryWords.length, iMaxPos, sQueryWords);
            if (res)
            {
                lstUrlId.add(iTpUrlId);
            }
        }
        return lstUrlId;
    }
    String getMouldColor(String[] sQueryWords)
    {
        HashMap<String, Integer> mpColorMap = new HashMap<String, Integer>();
        char[] szcMould = new char[sQueryWords.length];
        for (int i = 0; i < sQueryWords.length; ++i)
        {
            mpColorMap.put(sQueryWords[i], Integer.valueOf(String.valueOf('0')) + i + 1);
        }
        for (int i = 0; i < sQueryWords.length; ++i)
        {
            szcMould[i] = Integer.toString(mpColorMap.get(sQueryWords[i])).toCharArray()[0];
        }
        return new String(szcMould);
    }
    boolean cmpPos(ArrayList<ArrayList<Integer> > lstlstPos, int iSzLen, int iMaxPos, String[] sQueryWords)
    {
        char[] szPos = new char[iMaxPos + 1];
        /* init */
        for (int i = 0; i <= iMaxPos; ++i)
        {
            szPos[i] = '0';
        }
        /* fill */
        for (int i = 0; i < iSzLen; ++i)
        {
            char cCurColor = Integer.toString(Integer.valueOf(String.valueOf('0')) + i + 1).toCharArray()[0];
            for (Integer iCurPos : lstlstPos.get(i))
            {
                szPos[iCurPos] = cCurColor;
            }
        }
        /* mould */
        String sMould = getMouldColor(sQueryWords);
        String sPos = new String(szPos);
        if (sPos.contains(sMould))
        {
            System.out.println("DEBUG color: " + sPos + " mould: " + sMould);
            return true;
        }
        return false;
    }

    List<Integer> searchAll(String sQueryWord) {
        List<Integer> lstUrlId = new ArrayList<Integer>();
        lstUrlId = searchPos(sQueryWord, "a");
        List<Integer> tpUrlPosTitle = searchPos(sQueryWord, "t");
        for (Integer iPos : tpUrlPosTitle) {
            if (!lstUrlId.contains(iPos))
            {
                lstUrlId.add(iPos);
            }
        }
        List<Integer> tpUrlPosURL = searchPos(sQueryWord, "U");
        for (Integer iPos : tpUrlPosURL) {
            if (!lstUrlId.contains(iPos))
            {
                lstUrlId.add(iPos);
            }
        }
        List<Integer> tpUrlPosLinks = searchPos(sQueryWord, "l");
        for (Integer iPos : tpUrlPosLinks) {
            if (!lstUrlId.contains(iPos))
            {
                lstUrlId.add(iPos);
            }
        }
        return lstUrlId;
    }
    List<Integer> searchAll(String[] sQueryWords) {
        List<Integer> lstUrlId = new ArrayList<Integer>();
        lstUrlId = searchPos(sQueryWords, "a");
        List<Integer> tpUrlPosTitle = searchPos(sQueryWords, "t");
        for (Integer iPos : tpUrlPosTitle) {
            if (!lstUrlId.contains(iPos))
            {
                lstUrlId.add(iPos);
            }
        }
        List<Integer> tpUrlPosURL = searchPos(sQueryWords, "U");
        for (Integer iPos : tpUrlPosURL) {
            if (!lstUrlId.contains(iPos))
            {
                lstUrlId.add(iPos);
            }
        }
        List<Integer> tpUrlPosLinks = searchPos(sQueryWords, "l");
        for (Integer iPos : tpUrlPosLinks) {
            if (!lstUrlId.contains(iPos))
            {
                lstUrlId.add(iPos);
            }
        }
        return lstUrlId;
    }

	public static void main(String[] args) {
		DemoApplication oneDemo = new DemoApplication();
		oneDemo.readBook(oneDemo.sUrlFileName);
		oneDemo.readKey(oneDemo.sKeyFileName);
        for (int i = 0; i < oneDemo.urlBook.length; i++) {
            System.out.println(oneDemo.urlBook[i]);
        }
        System.out.println(oneDemo.findUrlIdByKey.entrySet());

		SpringApplication.run(DemoApplication.class, args);
	}

	// @GetMapping("**")
	@GetMapping("/load")
	@ResponseBody
	String load(HttpServletRequest request) {
		return String.format("You are browsing %s with %s!",
			request.getRequestURI(), request.getQueryString());
	}
	@GetMapping("search")
	@ResponseBody
	String search(
		@RequestParam(name = "query", required = false, defaultValue = "there")
		String sQuery,
		@RequestParam(name = "scope", required = false, defaultValue = "all")
		String sScope) {
		if (!readDataFlag)
		{
            readBook(sUrlFileName);
            readKey(sKeyFileName);
			System.out.println("init");
		}
        System.out.println("DEBUG scope: " + sScope);
        List<Integer> tpUrlPos = null;
        boolean bPhraseFlag = false;
        String sLowQuery = sQuery.toLowerCase();
        if (sLowQuery.contains("\""))
        {
            /* phrase */
			System.out.println("DEBUG phrase: " + sLowQuery);
			String sPhrase = sLowQuery.split("\"")[1];
			String[] szQueryWords = sPhrase.split(" ");
			//System.out.println("DEBUG phrase length: " + szQueryWords.length + szQueryWords[0] + szQueryWords[1]);
			bPhraseFlag = true;
			if (sScope.equals("title"))
            {
                System.out.println("DEBUG get into phrase title");
                tpUrlPos = searchPos(szQueryWords, "t");
            }
            else if (sScope.equals("text"))
            {
                tpUrlPos = searchPos(szQueryWords, "a");
            }
            else if (sScope.equals("URL"))
            {
                tpUrlPos = searchPos(szQueryWords, "U");
            }
            else if (sScope.equals("links"))
            {
                tpUrlPos = searchPos(szQueryWords, "l");
            }
            else if (sScope.equals("all"))
            {
                tpUrlPos = searchAll(szQueryWords);
            }
        }
        else if (sScope.equals("title"))
        {
            tpUrlPos = searchPos(sLowQuery, "t");
        }
        else if (sScope.equals("text"))
        {
            tpUrlPos = searchPos(sLowQuery, "a");
        }
        else if (sScope.equals("URL"))
        {
            tpUrlPos = searchPos(sLowQuery, "U");
        }
        else if (sScope.equals("links"))
        {
            tpUrlPos = searchPos(sLowQuery, "l");
        }
        else if (sScope.equals("all"))
        {
            tpUrlPos = searchAll(sLowQuery);
        }
		String tpContent = "<h1>My Search Engine</h1>";
		tpContent += "<form action=\"search\" method=\"GET\">";
		tpContent += "<p>Search: <input type=\"text\" name=\"query\" /> <input type=\"submit\"/>";
		tpContent += "<label><input name=\"scope\" type=\"radio\" value=\"title\" />title</label>";
		tpContent += "<label><input name=\"scope\" type=\"radio\" value=\"text\" />text</label>";
		tpContent += "<label><input name=\"scope\" type=\"radio\" value=\"URL\" />URL</label>";
		tpContent += "<label><input name=\"scope\" type=\"radio\" value=\"links\" />links</label>";
		tpContent += "</p></form>";

		if ((null == tpUrlPos) || (0 == tpUrlPos.size()))
		{
			/* empty */
			tpContent += "<h1>no record</h1>";
			return tpContent;
		}
        for (int iPos : tpUrlPos) {
            tpContent += "<h1><a href = \"" + urlBook[iPos] + "\">" + titleBook[iPos] + "</a></h1>";
        }
        System.out.println("DEBUG record length: " + tpUrlPos.size());
        return tpContent;

	}
	@GetMapping("greeting")
	@ResponseBody
	String sayHello(
		@RequestParam(name = "name", required = false, defaultValue = "there")
		String name) {

		return "<h1>Hello " + name + "!</h1>";
	}
}
