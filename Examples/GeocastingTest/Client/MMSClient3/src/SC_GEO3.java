import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;
import net.etri.pkilib.client.ClientPKILibrary;
import net.etri.pkilib.tool.ByteConverter;

/* -------------------------------------------------------- */
/** 
File name : SC_GEO1.java
	Service Consumer which uses the georeporter function. 
Author : Jaehyun Park (jae519@kaist.ac.kr)
Creation Date : 2017-06-27

Rev. history : 2017-07-28
Version : 0.5.9
	Changed from PollingResponseCallback.callbackMethod(Map<String,List<String>> headerField, message) 
	     to PollingResponseCallback.callbackMethod(Map<String,List<String>> headerField, List<String> messages) 
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class SC_GEO3 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:kr:vessel:neonexsoft:sc3";
		//myMRN = args[0];
		MMSConfiguration.MMS_URL="127.0.0.1:8090";
		MMSConfiguration.DEBUG=true;
		//Service Consumer cannot be HTTP server and should poll from MMS. 
		MMSClientHandler polling = new MMSClientHandler(myMRN);

		int pollInterval = 2000;
		String dstMRN = "urn:mrn:smart-navi:device:mms3";
		String svcMRN = "urn:mrn:smart-navi:device:geo-server";

		ClientPKILibrary clientPKILib = ClientPKILibrary.getInstance();
		ByteConverter byteConverter = ByteConverter.getInstance();

		//===== dummy content =====
		byte[] content = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};

		//===== active certificate =====
		String privateKeyPath_active = "PrivateKey_sc3.pem";
		String certPath_active = "Certificate_sc3.pem";

		byte[] signedData_active = clientPKILib.generateSignedData(content, privateKeyPath_active, certPath_active);
		String hexSignedData_active = byteConverter.byteArrToHexString(signedData_active);

		polling.startPolling(dstMRN, svcMRN, hexSignedData_active, pollInterval,3000,
				new MMSClientHandler.PollingResponseCallback() {
			//Response Callback from the polling message
			//it is called when client receives a message
			@Override
			public void callbackMethod(Map<String, List<String>> headerField, List<String> messages) {
				// TODO Auto-generated method stub
				for (String s : messages) {
					System.out.println(s);
				}
			}
		});
		//MMSConfiguration.lat = (float)1.0;
		//MMSConfiguration.lon = (float)1.0;
		//int geoInterval = 1000;
		//polling.startGeoReporting(svcMRN, geoInterval);
	}
}
