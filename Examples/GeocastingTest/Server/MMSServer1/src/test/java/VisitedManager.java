import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class VisitedManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VisitedManager.class);
	
	private JedisPoolConfig poolConfig;
	private JedisPool pool;
	private String host = "";
	private int port = 0;
	private static final int TIMEOUT = 3000;
	
	private String password;
	
	private boolean broken = false;
	
	private String srcMrn;
	private String vstMrn;
	private int expTm = 180;//seconds 60s*60minute*24hours 86400
	
	public VisitedManager(///Map<String, Object> vstMap
			String host, int port
			) {
		this.poolConfig = new JedisPoolConfig();
		this.host = host;
		this.port = port;
		this.pool = new JedisPool(poolConfig, this.host, this.port, TIMEOUT);
	}
	
	public VisitedManager(///Map<String, Object> vstMap
			String host, int port, String password
			) {
		this.poolConfig = new JedisPoolConfig();
		this.host = host;
		this.port = port;
		this.password = password;
		this.pool = new JedisPool(poolConfig, this.host, this.port, TIMEOUT, this.password);
	}
	
	/**
	 * this가 create될 때 반드시 수행된다.
	 * key "visitSc"(table id에 해당)를 prefix로 하고 srcMrn(SC의 MRN)을 append해서 redis의 key로 setting
	 */
	private void visitProcess() {
		Map<String, String> map = new ConcurrentHashMap<>();
		String registPntTm = "";
		
		map.put("visitSc", srcMrn);
		map.put("homeMms", vstMrn);
		map.put("registPntTm", registPntTm);
		
		String key = "visitSc";
		
		key = key+":"+srcMrn;
		
		String insResult = this.insertVisited(key, map).toString();
		System.out.println("ok?????"+insResult);
		
		if("OK".equals(insResult)) {
			System.out.println("expire???? "+ this.setExpire(key, expTm));
		}// 1:complete
	}
/*	
	private JedisPool getJedisPool() {
		JedisPool pool = new JedisPool(poolConfig, this.host, this.port, TIMEOUT);
		return pool;
	}
	
	private JedisPool getJedisPool(String password) {
		JedisPool pool = new JedisPool(poolConfig, this.host, this.port, TIMEOUT, password);
		return pool;
	}
*/
	public String getVisited(String srcMrn)
	
	{
		//////JedisPool pool = getJedisPool(password);
		///JedisPool pool = getJedisPool();
		
		Jedis jedis = null;
		String val = null;
		try {
			jedis = pool.getResource();
			
			LOGGER.debug("req srcMRN{}", srcMrn);
			
			val = jedis.get(srcMrn);
			LOGGER.debug("vvaaallllllllllllllllllllllll==={}", val);
		}catch (Exception e){
			broken = true;
		} finally {
			if(jedis.isConnected()) {
				jedis.close();
			}else if(jedis!=null){
				jedis.close();
			}
		}
		return val;
	}
	
public String getVisited(String key, String srcMrn)
	
	{
		//////JedisPool pool = getJedisPool(password);
		///JedisPool pool = getJedisPool();
		
		Jedis jedis = null;
		String val = null;
		try {
			jedis = pool.getResource();
			
			LOGGER.debug("req srcMRN key {}", srcMrn);
			
			val = jedis.hget(key, srcMrn);
			LOGGER.debug("vvaaallllllllllllllllllllllll==={}", val);
		}catch (Exception e){
			broken = true;
		} finally {
			if(jedis.isConnected()) {
				jedis.close();
			}else if(jedis!=null){
				jedis.close();
			}
		}
		return val;
	}
	
	
	public void connClose() {
		if (broken) {
			if(!pool.isClosed()) {
				pool.close();
			}
		} else {
			pool.close();
		}
	}

	public Object setVisited(String srcMrn, String dstMrn) {
		Jedis jedis = null;
		Object obj = null;
		try {
			jedis = pool.getResource();
			obj = jedis.set(srcMrn, dstMrn);
			
		}catch (Exception e){
			broken = true;
		} finally {
			if(jedis.isConnected()) {
				jedis.close();
			}else if(jedis!=null){
				jedis.close();
			}
		}
		return obj;
	}

	public Object hmgetVisited(String key, String srcMrn, String key2) {
		Jedis jedis = null;
		Object obj=null;
		try {
			jedis = pool.getResource();
			obj = jedis.hmget(key, srcMrn, key2);
			
		}catch (Exception e){
			broken = true;
		} finally {
			if(jedis.isConnected()) {
				jedis.close();
			}else if(jedis!=null){
				jedis.close();
			}
		}
		return obj;
	}

	public List<String> findVisited(String key, String field) {
		Jedis jedis = null;
		List<String> ret=null;
		try {
			jedis = pool.getResource();
			ret = jedis.hmget(key, field);
			if(ret.get(0)==null) {
				System.out.println("ret========="+ret);
				ret=null;
			}
		}catch (Exception e){
			broken = true;
		} finally {
			if(jedis.isConnected()) {
				jedis.close();
			}else if(jedis!=null){
				jedis.close();
			}
		}
		return ret;
	}

	public String insertVisited(String key, Map<String, String> map) {
		Jedis jedis = null;
		String ret=null;
		try {
			jedis = pool.getResource();
			ret = jedis.hmset(key, map);
			
		}catch (Exception e){
			broken = true;
		} finally {
			if(jedis.isConnected()) {
				jedis.close();
			}else if(jedis!=null){
				jedis.close();
			}
		}
		return ret;
	}

	public Long setExpire(String key, int expTime) {
		Jedis jedis = null;
		Long ret=null;
		try {
			jedis = pool.getResource();
			ret = jedis.expire(key, expTime);
			
		}catch (Exception e){
			broken = true;
		} finally {
			if(jedis.isConnected()) {
				jedis.close();
			}else if(jedis!=null){
				jedis.close();
			}
		}
		return ret;
	}

	public Long deleteVisited(String key) {
		Jedis jedis = null;
		Long ret=null;
		try {
			jedis = pool.getResource();
			ret = jedis.del(key);
			
		}catch (Exception e){
			broken = true;
		} finally {
			if(jedis.isConnected()) {
				jedis.close();
			}else if(jedis!=null){
				jedis.close();
			}
		}
		return ret;
	}
}
