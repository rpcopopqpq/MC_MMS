package kr.com.nexsys.distributed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.message_relaying.MRH_MessageInputChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import kr.ac.kaist.message_casting.MessageCastingHandler;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel;
import kr.ac.kaist.message_relaying.MessageTypeDecider;


public class HomeManager {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeManager.class);
	private String SESSION_ID = "";
	private ArrayList<HashMap<String, String>> mrnIpMapList = new ArrayList<HashMap<String, String>>();
	
	private List<HashMap<String, String>> myScList = new ArrayList<HashMap<String,String>>();
	
	
	public HomeManager(String sessionId){
		this.SESSION_ID = sessionId;
		
		// key : mrn, value : ip
		HashMap<String, String> mrnIpMap = new HashMap<String, String>();
		mrnIpMap.put("mrn", "urn:mrn:smart-navi:device:mms1");
		mrnIpMap.put("ip", "127.0.0.1");
		mrnIpMap.put("port", "8088");
		mrnIpMapList.add(mrnIpMap);
		
		HashMap<String, String> mrnIpMap2 = new HashMap<String, String>();
		mrnIpMap2.put("mrn", "urn:mrn:smart-navi:device:mms2");
		mrnIpMap2.put("ip", "127.0.0.1");
		mrnIpMap2.put("port", "8089");
		mrnIpMapList.add(mrnIpMap2);
		
		HashMap<String, String> mrnIpMap3 = new HashMap<String, String>();
		mrnIpMap3.put("mrn", "urn:mrn:smart-navi:device:mms3");
		mrnIpMap3.put("ip", "127.0.0.1");
		mrnIpMap3.put("port", "8090");
		mrnIpMapList.add(mrnIpMap3);
		
		
		//mms1
		HashMap<String, String> scMrns = new HashMap<String, String>();
		scMrns.put("scMrn", "urn:mrn:imo:imo-no:1000001");
		scMrns.put("homeMrn", "urn:mrn:smart-navi:device:mms1");
		scMrns.put("ip", "127.0.0.1");
		scMrns.put("port", "8088");
		myScList.add(scMrns);
		
		scMrns = new HashMap<String, String>();
		scMrns.put("scMrn", "urn:mrn:imo:imo-no:1000002");
		scMrns.put("homeMrn", "urn:mrn:smart-navi:device:mms1");
		scMrns.put("ip", "127.0.0.1");
		scMrns.put("port", "8088");
		myScList.add(scMrns);
		
		scMrns = new HashMap<String, String>();
		scMrns.put("scMrn", "urn:mrn:imo:imo-no:1000003");
		scMrns.put("homeMrn", "urn:mrn:smart-navi:device:mms1");
		scMrns.put("ip", "127.0.0.1");
		scMrns.put("port", "8088");
		myScList.add(scMrns);
		
		//mms2
		scMrns = new HashMap<String, String>();
		scMrns.put("scMrn", "urn:mrn:imo:imo-no:2000001");
		scMrns.put("homeMrn", "urn:mrn:smart-navi:device:mms2");
		scMrns.put("ip", "127.0.0.1");
		scMrns.put("port", "8089");
		myScList.add(scMrns);
		scMrns = new HashMap<String, String>();
		scMrns.put("scMrn", "urn:mrn:imo:imo-no:2000002");
		scMrns.put("homeMrn", "urn:mrn:smart-navi:device:mms2");
		scMrns.put("ip", "127.0.0.1");
		scMrns.put("port", "8089");
		myScList.add(scMrns);
		scMrns = new HashMap<String, String>();
		scMrns.put("scMrn", "urn:mrn:imo:imo-no:2000003");
		scMrns.put("homeMrn", "urn:mrn:smart-navi:device:mms2");
		scMrns.put("ip", "127.0.0.1");
		scMrns.put("port", "8089");
		myScList.add(scMrns);
		
		//mms3
		scMrns = new HashMap<String, String>();
		scMrns.put("scMrn", "urn:mrn:imo:imo-no:3000001");
		scMrns.put("homeMrn", "urn:mrn:smart-navi:device:mms3");
		scMrns.put("ip", "127.0.0.1");
		scMrns.put("port", "8090");
		myScList.add(scMrns);
		
		scMrns = new HashMap<String, String>();
		scMrns.put("scMrn", "urn:mrn:imo:imo-no:3000002");
		scMrns.put("homeMrn", "urn:mrn:smart-navi:device:mms3");
		scMrns.put("ip", "127.0.0.1");
		scMrns.put("port", "8090");
		myScList.add(scMrns);
		
		scMrns = new HashMap<String, String>();
		scMrns.put("scMrn", "urn:mrn:imo:imo-no:3000003");
		scMrns.put("homeMrn", "urn:mrn:smart-navi:device:mms3");
		scMrns.put("ip", "127.0.0.1");
		scMrns.put("port", "8090");
		myScList.add(scMrns);
		
		scMrns = new HashMap<String, String>();
		scMrns.put("scMrn", "urn:mrn:mcl:vessel:dma:poul-lowenorn");
		scMrns.put("homeMrn", "urn:mrn:smart-navi:device:mms3");
		scMrns.put("ip", "127.0.0.1");
		scMrns.put("port", "8090");
		myScList.add(scMrns);
	}
	
	public ArrayList<HashMap<String, String>> getMrnIpMapList() {
		return mrnIpMapList;
	}
	
	public List<HashMap<String, String>> getScMrnList() {
		return myScList;
	}
	
	public void visitMMSCall(MRH_MessageInputChannel.ChannelBean bean,MessageCastingHandler mch) {
		DistributeMMS obj = new DistributeMMS();
		obj.visitMMSExecute(bean,mch, mrnIpMapList);
	}
}
