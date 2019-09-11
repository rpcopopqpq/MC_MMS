import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DelRedis {

	public static void main(String...strings ) {
		String key = "visitSc";///"visitSc";
		String srcMrn = "urn:mrn:corp-see:sp:KR001";
		String vstMrn = "urn:mrn:smart-navi:device:mms1";
		
		String registPntTm = "20190821151130103";
		
		
		VisitedManager vistMng = new VisitedManager("localhost", 6378);
		String redResult = null;
		
		Map<String, String> map = new ConcurrentHashMap<>();
		
		map.put("srcMrn", srcMrn);
		map.put("vstMrn", vstMrn);
		map.put("registPntTm", registPntTm);
		
		Object obj =null;
		
		key = key+":"+srcMrn;
		System.out.println("key=========="+key);
		
		List<String> visitMms = new ArrayList();
		visitMms = vistMng.findVisited(key, "vstMrn");
		
		if(null!=visitMms) {
			System.out.println("hmgetVisited==="+ visitMms+""+visitMms.getClass());
			
			System.out.println("delete?? "+vistMng.deleteVisited(key));
		}
		
		if(visitMms==null) {
			String insResult = vistMng.insertVisited(key, map).toString();
			System.out.println("ok?????"+insResult);
			
			visitMms = vistMng.findVisited(key, "vstMrn");
			if(null!=visitMms) {
				System.out.println("hmgetVisited==="+ visitMms+""+visitMms.getClass());
			}
			
		}
		obj =null;

///		Object map = vistMng.getVisitedList();
		
///		System.out.println("getVisitedList==="+ map+"///size=="+obj.getClass());
	}
}
