package kr.co.nexsys.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


public class JedisConnectFactory {

	private String host;
	private int    port;
	private int timeout;
	private String password;
	private JedisPoolConfig poolConfig;
	private JedisPool pool;
	private Jedis jedis;
	
	public JedisConnectFactory(String host, int port, int timeout) {
		this.host = host;
		this.port = port;
		this.timeout=timeout;
	}
	
	public JedisConnectFactory(String host, int port, int timeout, String password) {
		this.host = host;
		this.port = port;
		this.timeout=timeout;
		this.password=password;
	}
	
	public Jedis getJedisConnection() {
		poolConfig = new JedisPoolConfig();
		pool = new JedisPool(poolConfig, host, port, timeout);
		jedis = pool.getResource();
		return jedis;
	}
	
	public Jedis getJedisConnection(String password) {
		this.password = password;
		poolConfig = new JedisPoolConfig();

		pool = new JedisPool(poolConfig, host, port, timeout, this.password);
		jedis = pool.getResource();
		return jedis;
	}
}
