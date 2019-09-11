package kr.co.nexsys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import org.slf4j.Logger;

import kr.ac.kaist.mms_server.MMSConfiguration;

public class HomeMmsHttpSend {
	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(HomeMmsHttpSend.class);
	
	private String srcMrn;
	private String targetMmsIp;
	private String targetMmsPort;
	private String dstMrn;
	private String visitMmsMrn;
	private String httpPort;//this MMS servers http port;
	private String targetUri;//"/register-visiting" , "/remove-mrn"
	private String content;
	
	public HomeMmsHttpSend(String srcMrn, String targetMmsIp, String targetMmsPort, String dstMrn, String visitMmsMrn, String httpPort, String uri, String content) {
		
		this.srcMrn       = srcMrn;
		this.targetMmsIp  = targetMmsIp;
		this.targetMmsPort= targetMmsPort;
		this.dstMrn       = dstMrn;
		this.visitMmsMrn  = visitMmsMrn;
		this.httpPort     = httpPort;
		this.targetUri    = uri;
		this.content      = content;
	}
	
	public void sendToVisitedMmsInfo() {

		
		String homeMmsUrl = "http://"+targetMmsIp+":"+targetMmsPort+targetUri;
		
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(homeMmsUrl).openConnection();
		} catch (IOException e2) {
			LOGGER.info("/-/-/ IOException");
		}	

		conn.setRequestProperty("srcMRN", srcMrn);
		conn.setRequestProperty("dstMRN", dstMrn);
		conn.setRequestProperty("visitMmsMrn", visitMmsMrn);
		conn.setRequestProperty("visitMmsPort", httpPort);
		conn.setRequestProperty("homeMms", MMSConfiguration.getMmsMrn());
			
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	
		try {
			conn.setRequestMethod("POST");
		} catch (ProtocolException e2) {
			LOGGER.info("/-/-/ ProtocolException");
		}
		
		try {
			conn.connect();
		} catch (IOException e1) {
			LOGGER.info("/-/-/ IOException : cannot conncet to mms server...{}", homeMmsUrl);
		}
		
		OutputStream wr = null;
		
		try {
			byte[] b = content.toString().getBytes("UTF-8");
			
			wr = conn.getOutputStream();
			wr.write(b);
		} catch(IOException e1) {
			LOGGER.info("/-/-/ IOException");
		} finally {
			if (wr != null) {
				try {
					wr.flush();
					wr.close();
				} catch (IOException e) {
					LOGGER.info("/-/-/ IOException");
				}
			}
		}
		
		int httpResult = new Integer(0);
		try {
			httpResult = conn.getResponseCode();
		} catch (IOException e2) {
			LOGGER.info("error from home mms {}:{}/{}", targetMmsIp, targetMmsPort, content);
		}

		StringBuffer resultBuff = null;
		String line = null;
		BufferedReader reader= null;
		InputStreamReader inputStreamReader = null;
		
		try{
			if (httpResult == HttpURLConnection.HTTP_OK) {
				resultBuff = new StringBuffer();
				inputStreamReader = new InputStreamReader(conn.getInputStream(), "UTF-8");
				reader = new BufferedReader(inputStreamReader);
	
				while ((line = reader.readLine()) != null) {
					resultBuff.append(line);
				}
				
			} else {
				
			}
		}catch(IOException e1) {
			LOGGER.info("/-/-/ IOException");
		}finally{
			try {
				if(null!=reader) {reader.close();}
				if(null!=inputStreamReader) {inputStreamReader.close();}
			} catch (IOException e) {
				LOGGER.error("error on closing");
			}
		}
		
		if(null!=conn) {
			conn.disconnect();
		}
		LOGGER.debug(resultBuff.toString());
		System.out.println("---------------"+resultBuff.toString());
	}

}