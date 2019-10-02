package client.mms1;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;
import net.etri.pkilib.client.ClientPKILibrary;
import net.etri.pkilib.tool.ByteConverter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

public class SC_GEO1 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:kr:vessel:neonexsoft:sc1";

		MMSConfiguration.MMS_URL="127.0.0.1:8088";
		MMSConfiguration.DEBUG=true;
		MMSClientHandler polling = new MMSClientHandler(myMRN);

		int pollInterval = 2000;
		String dstMRN = "urn:mrn:smart-navi:device:mms1";
		String svcMRN = "urn:mrn:smart-navi:device:geo-server";

		ClientPKILibrary clientPKILib = ClientPKILibrary.getInstance();
		ByteConverter byteConverter = ByteConverter.getInstance();

		//===== dummy content =====
		byte[] content = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};

		//===== active certificate =====
		String privateKeyPath_active = "PrivateKey_sc1.pem";
		String certPath_active = "Certificate_sc1.pem";

		byte[] signedData_active = clientPKILib.generateSignedData(content, privateKeyPath_active, certPath_active);
		String hexSignedData_active = byteConverter.byteArrToHexString(signedData_active);
		for(int i = 1; i<4;i++){
		polling.startPolling(dstMRN, svcMRN+i, hexSignedData_active, pollInterval,6000,
				(headerField, messages)-> System.out.println(messages.stream().collect(Collectors.joining())));
		}

	}
}
