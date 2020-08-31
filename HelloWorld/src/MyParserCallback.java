import javax.swing.text.html.*;
import javax.swing.text.html.HTML.*;
import javax.swing.text.html.HTMLEditorKit.*;
import javax.swing.text.html.parser.*;
import javax.swing.text.*;
import java.util.*;

class MyParserCallback extends HTMLEditorKit.ParserCallback {
	public String content = new String();
	public String m_title = new String();
	public List<String> urls = new ArrayList<String>();
	public Map<String, Integer> wordsPos = new HashMap<String, Integer>(); 
	int m_iMax = 10;
	boolean bTitleFlag = false;

	@Override
	public void handleText(char[] data, int pos) {
		String tpWord = new String(data);
		content += " " + tpWord;
		if (null == wordsPos.get(tpWord))
		{
			/* new */
			wordsPos.put(tpWord, pos);
		}
		if (bTitleFlag)
		{
			m_title = tpWord;
		}
	}
	@Override
	public void handleStartTag(Tag tag, MutableAttributeSet attrSet, int pos) 
	{
		// System.out.println("DEBUG handle startTag");
		if (tag.toString().equals("a")) {
			// System.out.println("DEBUG get target");
			Enumeration e = attrSet.getAttributeNames();

			while (e.hasMoreElements()) {

				Object aname = e.nextElement();

				if (aname.toString().equals("href")) {
					String u = (String) attrSet.getAttribute(aname);
					String[] words = u.split("[#\\?]");
					if (words.length <= 0)
					{
						continue;
					}
					// u = words[0];
					if (urls.size() < m_iMax && !urls.contains(words[0]))
						urls.add(words[0]);
				}
			}
		}else if (tag.toString().equals("title")){
			bTitleFlag = true;
		}
	}

	public void handleEndTag(Tag tag, MutableAttributeSet attrSet, int pos) 
	{
		if (tag.toString().equals("title")){
			bTitleFlag = false;
		}
	}
}