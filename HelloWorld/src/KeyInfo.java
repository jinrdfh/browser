
import java.util.*;

public class KeyInfo {
    /* urlId, <type, pos[]> */
	public HashMap<Integer, HashMap<String, List<Integer> > > m_keyPos = new HashMap<Integer, HashMap<String, List<Integer> > >();

	boolean saveList(int iUrlId, String sLabel, List<Integer> lstKeyPos)
	{
	    List<Integer> lstTpPos = (ArrayList<Integer>)((ArrayList<Integer>) lstKeyPos).clone();
	    HashMap<String, List<Integer> > oKeyInPage = m_keyPos.get(iUrlId);
        if (null == oKeyInPage){
            oKeyInPage = new HashMap<String, List<Integer> >();
            oKeyInPage.put(sLabel, lstTpPos);
            m_keyPos.put(iUrlId, oKeyInPage);
        }else{
            oKeyInPage.put(sLabel, lstTpPos);
            m_keyPos.put(iUrlId, oKeyInPage);
        }
	    return true;
	}
}
