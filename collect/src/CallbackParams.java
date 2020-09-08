import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CallbackParams {

	private List<String> ignorelist = null;
	private List<String> blackUrl = null;
	private List<String> blackUrlWild = null;
	private List<String> urlPool = null;
	private List<String> processedUrl = null;
	private int maxUrl = 10;
	private int maxProcessedUrl = 100;
	
	public CallbackParams(
			int maxUrl, 
			int maxProcessedUrl, 
			String ignorelistFileName, 
			String blackUrlFileName, 
			List<String> urlPool, 
			List<String> processedUrl) {
		this.ignorelist = MyPgUtils.getArrayFromFile(ignorelistFileName);
		this.blackUrl = MyPgUtils.getArrayFromFile(blackUrlFileName);
		this.blackUrlWild = new ArrayList<String>();
		this.urlPool = urlPool;
		this.processedUrl = processedUrl;
		this.setMaxUrl(maxUrl);
		this.setMaxProcessedUrl(maxProcessedUrl);
		
		Iterator itr = this.blackUrl.iterator();
		while (itr.hasNext()) {
			String tmp = (String)itr.next();
			int i = tmp.indexOf("*");
			if (i>=0) {
				itr.remove();
				this.blackUrlWild.add(tmp.substring(0,i));
			}
		}
		
		for (String url: this.blackUrl) {

		}
		
		System.out.println("--- URL Blacklist ---");
		for (int i=0; i<blackUrl.size(); i++) {
			System.out.println(blackUrl.get(i));
		}
		System.out.println("--- URL BlacklistWild ---");
		for (int i=0; i<blackUrlWild.size(); i++) {
			System.out.println(blackUrlWild.get(i));
		}
		
//		System.out.println("--- Ignorelist ---");
//		for (int i=0; i<ignorelist.size(); i++) {
//			System.out.println(ignorelist.get(i));
//		}
		

	}

	public List<String> getIgnorelist() {
		return ignorelist;
	}

	public void setIgnorelist(List<String> ignorelist) {
		this.ignorelist = ignorelist;
	}

	public List<String> getBlackUrl() {
		return blackUrl;
	}

	public void setBlackUrl(List<String> blackUrl) {
		this.blackUrl = blackUrl;
	}

	public List<String> getBlackUrlWild() {
		return blackUrlWild;
	}

	public void setBlackUrlWild(List<String> blackUrlWild) {
		this.blackUrlWild = blackUrlWild;
	}

	public List<String> getUrlPool() {
		return urlPool;
	}

	public void setUrlPool(List<String> urlPool) {
		this.urlPool = urlPool;
	}

	public List<String> getProcessedUrl() {
		return processedUrl;
	}

	public void setProcessedUrl(List<String> processedUrl) {
		this.processedUrl = processedUrl;
	}

	public int getMaxUrl() {
		return maxUrl;
	}

	public void setMaxUrl(int maxUrl) {
		this.maxUrl = maxUrl;
	}

	public int getMaxProcessedUrl() {
		return maxProcessedUrl;
	}

	public void setMaxProcessedUrl(int maxProcessedUrl) {
		this.maxProcessedUrl = maxProcessedUrl;
	}

	

}
