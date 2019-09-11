import java.util.Map;

public class GetsRedis {

	public static void main(String...strings ) {
		String srcMrn = "urn:mrn:smart-navi:s:kriso";
		String dstMrn = "urn:mrn:smart-navi:device:mms1";
		VisitedManager vistMng = new VisitedManager("localhost", 6378);
		String redResult = vistMng.getVisited(srcMrn);
		
		System.out.println("getVisited==="+ redResult);
		Object obj =null;
		if(null == redResult) {
			obj = vistMng.setVisited(srcMrn, dstMrn);
			System.out.println("set result==="+obj.toString());
		}
		redResult = vistMng.getVisited("srcMrn", srcMrn);
		
		System.out.println("getVisited==="+ redResult);
		obj =null;
		if(null == redResult) {
			obj = vistMng.setVisited(srcMrn, dstMrn);
			System.out.println("set result==="+obj.toString());
		}

///		Map<String, String> map = vistMng.getVisitedList();
		
///		System.out.println("getVisitedList==="+ map+"///size=="+map.size());
	}
}
