import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MyPgUtils {
	
	static List<String> getArrayFromFile(String filename) {
		Scanner s;
		ArrayList<String> list = new ArrayList<String>();
		try {
			s = new Scanner(new File(filename));
			while (s.hasNext()) {
				list.add(s.next());
			}
			s.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return list;
	}
	
	static String fixHref(String inStr) {
		String lang = new String(""+'\u2329');
		
//		inStr = inStr.trim();

		if (inStr.substring(inStr.length()-1).equals("#")) {
			inStr = inStr.substring(0, inStr.length()-1);
		}

		if (inStr.contains(lang)) {
			return inStr.replaceAll(lang, "&lang");
		}
		
		
		return inStr;
	}
	
    /* Returns true if url is valid */
    public static boolean isValid(String url) 
    { 
        /* Try creating a valid URL */
        try { 
            new URL(url).toURI(); 
            return true; 
        } 
          
        // If there was an Exception 
        // while creating URL object 
        catch (Exception e) { 
            return false; 
        } 
    } 
    
    public static String rmTail(String url, String tailMark) {
    	int i = url.indexOf(tailMark);
    	return (i >= 0)?url.substring(0, i):url;
    }
    
    public static String rmTail(String url) {
    	return rmTail(rmTail(url, "?"), "#");
    }
    
    public static boolean inBlackList(String url, List<String> blackUrl, List<String> blackUrlWild) {
    	if (blackUrl.contains(url)) return true;
    	else {
    		for (int i=0; i<blackUrlWild.size(); i++) {
    			if (url.indexOf(blackUrlWild.get(i)) >= 0) return true;
    		}
    		return false;
    	}
    }

}
