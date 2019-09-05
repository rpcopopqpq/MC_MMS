import kr.ac.kaist.mms_server.MMSConfiguration;
import kr.co.nexsys.redis.JedisConnectFactory;
import kr.co.nexsys.redis.JedisDao;
import redis.clients.jedis.Jedis;

public class HmGetsRedis {
	private static JedisDao jedisDao;
	private static final String KEY="VL_";
	
	private static Jedis jedis;
	private static JedisConnectFactory jedisConn;
	
	
	private static final int TIMEOUT = 3000;
	
	
	
	public static void main(String...strings ) {
		jedisConn = new JedisConnectFactory("localhost", 6377, TIMEOUT);
		jedis = jedisConn.getJedisConnection();

		String srcMrn = "urn:mrn:mcl:vessel:dma:poul-lowenorn";
		String dstMrn = "urn:mrn:smart-navi:device:mms1";
		
		
		Object obj = jedis.hgetAll(KEY+srcMrn);
		System.out.println(obj.getClass());
		System.out.println(obj.toString());
	}
	
}
