
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
/*
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
*/
import org.slf4j.Logger;

public class HomeMmsHttpSend {
	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(HomeMmsHttpSend.class);
	private static String TAG = "homeMmsSendHttp";
	private static String srcMrn;
	private String homeMmsIp;
	private String homeMmsPort;
	private static String dstMrn;
	private static String visitMms;
	/*
	public HomeMmsHttpSend(String srcMrn, String homeMmsIp, String homeMmsPort, String dstMrn, String visitMms) {
		
		this.srcMrn     = "urn:mrn:smart-navi:device:service-SV10-B";
		this.homeMmsIp  = homeMmsIp;
		this.homeMmsPort=homeMmsPort;
		this.dstMrn     = "urn:mrn:imo:imo-no:1000002";
		this.visitMms   = visitMms;
	}
	*/
	//public void sendTOHomeMms() {
	public static void main(String[] args) {
		srcMrn     = "urn:mrn:smart-navi:device:service-SV10-B";
		dstMrn     = "urn:mrn:imo:imo-no:1000002";
		
		String homeMmsUrl = "http://localhost:8088/";
		
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(homeMmsUrl).openConnection();
		} catch (IOException e2) {
			LOGGER.info("/-/-/ IOException");
		}	

			conn.setRequestProperty("srcMRN", srcMrn);
			conn.setRequestProperty("dstMRN", dstMrn);
			conn.setRequestProperty("visitMms", visitMms);
			conn.setRequestProperty("IPAddr", "127.0.0.1");
			
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		URL obj =null;
		try {
			obj = new URL(homeMmsUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) obj.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//add request header
		try {
			con.setRequestMethod("POST");
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		con.setRequestProperty("User-Agent", "MMSClient/0.9.1");
		con.setRequestProperty("Accept-Charset", "UTF-8");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("srcMRN", srcMrn);
		
		if (dstMrn != null) {
			con.setRequestProperty("dstMRN", dstMrn);
		}
		
		//con.addRequestProperty("Connection","keep-alive");
		
		
		//load contents
		String urlParameters = "data?문자열, 보낸다. 내용 테스트";//"register-visiting";
		urlParameters= urlParameters+"urn:mrn:imo:imo-no:1000002..."+System.lineSeparator()+"urn:mrn:smart-navi:device:service-SV10-B message\t....";

		 {System.out.println(TAG+"urlParameters: "+urlParameters);}
		
		// Send post request
		con.setDoOutput(true);
		BufferedWriter wr=null;
		try {
			wr = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(),Charset.forName("UTF-8")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		{System.out.println(TAG+"Trying to send message");}
		try {
			wr.write(urlParameters);
			wr.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//wr.close();

		int responseCode = 0;
		InputStream inStream = null;
			try {
				responseCode = con.getResponseCode();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				inStream = con.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		List<String> responseCodes = new ArrayList<String>();
		responseCodes.add(responseCode+"");
		
			System.out.println("\n"+TAG+"Sending 'POST' request to URL : " + homeMmsUrl);
			System.out.println(TAG+"Post parameters : " + urlParameters);
			System.out.println(TAG+"Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
		        new InputStreamReader(inStream,Charset.forName("UTF-8")));
		String inputLine;
		StringBuffer response = new StringBuffer();
		
		try {
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
		
			try {
				wr.close();
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(TAG+"Response: " + response.toString() + "\n");
		con.disconnect();
	}

}