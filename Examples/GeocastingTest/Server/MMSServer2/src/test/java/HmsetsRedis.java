import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HmsetsRedis {

	public static void main(String...strings ) {
		String key = "visitSc";///"visitSc";
		String srcMrn = "urn:mrn:corp-see:sp:KR001";
		String vstMrn = "urn:mrn:smart-navi:device:mms1";
		
		String registPntTm = "20190821151130103";
		int expTm = 180;//seconds 60s*60minute*24hours 86400
		
		
		
		VisitedManager vistMng = new VisitedManager("localhost", 6378);
		String redResult = null;
		
		Map<String, String> map = new ConcurrentHashMap<>();
		
		map.put("srcMrn", srcMrn);
		map.put("vstMrn", vstMrn);
		map.put("registPntTm", registPntTm);
		///map.put("expTm", expTm);
		
		Object obj =null;
		
		key = key+":"+srcMrn;
		System.out.println("key=========="+key);
		
		List<String> visitMms = new ArrayList();
		visitMms = vistMng.findVisited(key, vstMrn);
		
		if(null!=visitMms) {
			System.out.println("hmgetVisited==="+ visitMms+""+visitMms.getClass());
		}
		
		if(visitMms==null) {
			String insResult = vistMng.insertVisited(key, map).toString();
			System.out.println("ok?????"+insResult);
			
			if("OK".equals(insResult)) {
				System.out.println("expire???? "+vistMng.setExpire(key, expTm));
			}// 1:complete
			
			
		}
		obj =null;

///		Object map = vistMng.getVisitedList();
		
///		System.out.println("getVisitedList==="+ map+"///size=="+obj.getClass());
	}
}
