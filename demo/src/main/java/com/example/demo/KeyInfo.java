package com.example.demo;
import java.util.*;

public class KeyInfo {
    /* urlId, <type, pos[]> */
	public HashMap<Integer, HashMap<String, ArrayList<Integer> > > m_keyPos = new HashMap<Integer, HashMap<String, ArrayList<Integer> > >();

	boolean saveList(int iUrlId, String sLabel, ArrayList<Integer> lstKeyPos)
	{
	    ArrayList<Integer> lstTpPos = (ArrayList<Integer>)((ArrayList<Integer>) lstKeyPos).clone();
	    HashMap<String, ArrayList<Integer> > oKeyInPage = m_keyPos.get(iUrlId);
        if (null == oKeyInPage){
            oKeyInPage = new HashMap<String, ArrayList<Integer> >();
            oKeyInPage.put(sLabel, lstTpPos);
            m_keyPos.put(iUrlId, oKeyInPage);
        }else{
            oKeyInPage.put(sLabel, lstTpPos);
            m_keyPos.put(iUrlId, oKeyInPage);
        }
	    return true;
	}
}
