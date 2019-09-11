package kr.co.nexsys.redis;

import java.util.HashMap;
import java.util.Map;

import kr.ac.kaist.mms_server.MMSConfiguration;
import redis.clients.jedis.Jedis;

public class JedisDao {
	private Jedis jedis;
	private JedisConnectFactory jedisConn;
	


	private static final int TIMEOUT = 3000;
	
	public JedisDao() {
		jedisConn = new JedisConnectFactory(MMSConfiguration.getREDIS_HOST(), MMSConfiguration.getREDIS_PORT(), TIMEOUT);
		jedis = jedisConn.getJedisConnection();
	}

	public boolean findClientInfo(String srcMRN) {
		boolean ret = false;
		Object obj = jedis.get(srcMRN);
		if(null!=obj) {
			ret = true;
		}
		return ret;
	}

	public void registMyScInfo(String key) {
		String value = "1";
		jedis.set(key, value);
	}
	
	
	public Long deleteKey(String key) {
		return jedis.del(key);
	}

	public void insertVisited(String key, Map<String, String> map) {
		jedis.hmset(key, map);
	}

	public String findVisited(String key, String field) {
		String ret = new String();
		Object obj = jedis.hget(key, field);
		if(null==obj) {
			ret = "";
		}else {
			ret = obj.toString();
		}
		return ret;
	}
	
	public Map<String, String> findVisitedMmsInfo(String key) {
		Map<String, String> ret = new HashMap<String, String>();
		ret = (HashMap<String, String>) jedis.hgetAll(key);
		return ret;
	}
}
