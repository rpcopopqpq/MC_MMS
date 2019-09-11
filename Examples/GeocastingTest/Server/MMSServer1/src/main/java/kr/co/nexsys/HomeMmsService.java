package kr.co.nexsys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.nexsys.redis.JedisDao;
import kr.com.nexsys.distributed.HomeManager;

public class HomeMmsService {

	private JedisDao jedisDao;
	private String sessionId;
	/**
	 * ScMrnList
	 * home MMS 하위 관리 대상 ScMRN의 list
	 */
	private final static String KEY = "HL_";///"ScMrnList";/// "HL_"
	
	private final static String HOME_KEY = "VL_";///"visitList";/// "VL_"
	public HomeMmsService () {
		jedisDao = new JedisDao();
	}
	
	public boolean findClientInfo(String srcMRN) {
		String key = KEY+srcMRN;
		return jedisDao.findClientInfo(key);
	}

	public void registMyScInfo(String srcMRN) {
		String key = KEY+srcMRN;
		jedisDao.registMyScInfo(key);
	}

	public void insertVisited(String srcMrn, String homeMrn, String visitedMmsIp, int visitedMmsPort) {
		String key = HOME_KEY+srcMrn;
		Map<String, String> map = new HashMap<String, String>();
		map.put("homeMms", homeMrn);
		map.put("ip", visitedMmsIp);
		map.put("port", Integer.toString(visitedMmsPort));
		jedisDao.insertVisited(key, map);
	}
	
	/**
	 * 
	 * @param srcMrn
	 * @param visitMrn       : visited MMS's MRN
	 * @param visitedMmsIp   : visited MMS's ip address
	 * @param visitedMmsPort : visited MMS http server port
	 * ex) when child of MMS3 visited to MMS1, MMS3 insert into it's redis
	 *  VL_urn:mrn:mcl:vessel:dma:poul-lowenorn, ip:127.0.0.1, port:8088, visitMms:urn:mrn:smart-navi:device:mms1
	 */
	public void insertAway(String srcMrn, String visitMrn, String visitedMmsIp, int visitedMmsPort) {
		String key = HOME_KEY+srcMrn;
		Map<String, String> map = new HashMap<String, String>();
		map.put("visitMms", visitMrn);
		map.put("ip", visitedMmsIp);
		map.put("port", Integer.toString(visitedMmsPort));
		jedisDao.insertVisited(key, map);
	}

	public boolean getVisited(String srcMrn) {
		boolean ret;
		String jMrn = "";
		String key = HOME_KEY+srcMrn;
		jMrn = jedisDao.findVisited(key, "visitMms");
		if("".equals(jMrn)) {
			ret = false;
		}else {
			ret = true;
		}
		return ret;
	}

	public long removeAway(String srcMrn) {
		long result =0;
		String key = HOME_KEY+srcMrn;
		result = jedisDao.deleteKey(key);
		return result;
	}

	//TODO Home manager에서 SC MRN으로 해당 SC의 home MMS MRN을 찾는다
	public Map<String, String> findHomeMmsInfo(String visitSc, String sessionId) {
		this.sessionId = sessionId;
		Map<String, String> ret = new HashMap<String, String>();
		List<HashMap<String, String>> homeMmsList = new ArrayList<HashMap<String, String>>();
		HomeManager homeManager = new HomeManager(sessionId);
		homeMmsList = homeManager.getScMrnList();
		
		for(int i=0; i<homeMmsList.size(); i++) {
			Map<String, String> mmsMap = homeMmsList.get(i);
			
			if( visitSc.equals(mmsMap.get("scMrn")) ) {
				ret = mmsMap;
				break;
			}
		}
		
		return ret;
	}

	public Map<String, String> findVisitedMmsInfo(String srcMrn) {
		Map<String, String> ret = new HashMap<String, String>();
		String key = HOME_KEY+srcMrn;
		ret = jedisDao.findVisitedMmsInfo(key);
		return ret;
	}

	public long removeVisitedScMrn(String srcMrn) {
		long result =0;
		String key = HOME_KEY+srcMrn;
		result = jedisDao.deleteKey(key);
		return result;		
	}

}
