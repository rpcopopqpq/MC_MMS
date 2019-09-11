import java.util.Map;

public class GetsRedis2 {

	public static void main(String...strings ) {
		String srcMrn = "urn:mrn:smart-navi:s:kriso";
		String dstMrn = "urn:mrn:smart-navi:device:mms1";
		VisitedManager vistMng = new VisitedManager("localhost", 6378);
		String redResult = null;
		
		Object obj =null;
		
		obj = vistMng.hmgetVisited("srcMrn", srcMrn, "expr");
		
		System.out.println("hmgetVisited==="+ obj+""+obj.getClass());
		obj =null;

///		Object map = vistMng.getVisitedList();
		
///		System.out.println("getVisitedList==="+ map+"///size=="+obj.getClass());
	}
}
