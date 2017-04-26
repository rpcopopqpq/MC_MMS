package kr.ac.kaist.message_relaying;

/* -------------------------------------------------------- */
/** 
File name : MessageTypeDecision.java
	It decides type of a message.
Author : Jaehyun Park (jae519@kaist.ac.kr)
	Jin Jung (jungst0001@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01

Rev. history : 2017-02-01
	Added log providing features.
	Added locator registering features.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)

Rev. history : 2017-04-25 
Version : 0.5.0
	Revised class name from MessageTypeDecision to MessageTypeDecider.
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

import io.netty.handler.codec.http.HttpMethod;
import kr.ac.kaist.message_casting.MessageCastingHandler;
import kr.ac.kaist.mms_server.MMSConfiguration;

public class MessageTypeDecider {
	
	private static final String TAG = "[MessageTypeDecider] ";
	
	static final int POLLING = 1; // it means polling message 
	static final int RELAYING_TO_SC = 2; // it means relaying to SC
	static final int RELAYING_TO_SERVER = 3; // it means relaying to SR, IR or SP
	static final int REGISTER_CLIENT = 4; // it means registering MMS client 
	static final int UNKNOWN_MRN = 5; // it means unknown MRN
	static final int UNKNOWN_HTTP_TYPE = 6; // it means unknown http type
	static final int STATUS = 7;
	static final int LOGS = 8;
	static final int CLEAN_LOGS = 9;
	static final int SAVE_LOGS = 10;
	static final int EMPTY_QUEUE = 11;
	static final int EMPTY_MNSDummy = 12;
	static final int REMOVE_MNS_ENTRY = 13;
	
	int decideType(MessageParser parser, MessageCastingHandler mch) {
		String srcMRN = parser.getSrcMRN();
		String dstMRN = parser.getDstMRN();
		HttpMethod httpMethod = parser.getHttpMethod();
		String uri = parser.getUri();
		
//    	When polling
    	if (httpMethod == HttpMethod.POST && uri.equals("/polling") && dstMRN.equals(MMSConfiguration.MMS_MRN)) {
    		return POLLING; 
    	}
    	
//		when registering
    	else if (httpMethod == httpMethod.POST && uri.equals("/registering") && dstMRN.equals(MMSConfiguration.MMS_MRN)) {
    		return REGISTER_CLIENT;
    	}
    	
//		when LOGGING
    	else if (MMSConfiguration.LOG_PROVIDING && httpMethod == HttpMethod.GET && uri.equals("/logs")){
    		return LOGS;
    	} else if (MMSConfiguration.LOG_PROVIDING && httpMethod == HttpMethod.GET && uri.equals("/status")){
    		return STATUS;
    	} else if (MMSConfiguration.LOG_PROVIDING && httpMethod == HttpMethod.GET && uri.equals("/cleanlogs")){ 
    		return CLEAN_LOGS;
    	} else if (MMSConfiguration.LOG_PROVIDING && httpMethod == HttpMethod.GET && uri.equals("/savelogs")){    		
    		return SAVE_LOGS;
    	} else if (MMSConfiguration.EMPTY_QUEUE && httpMethod == HttpMethod.GET && uri.equals("/emptyqueue")){ 
    		return EMPTY_QUEUE;
    	} else if (MMSConfiguration.EMPTY_MNS_DUMMY && httpMethod == HttpMethod.GET && uri.equals("/emptymnsdummy")){ 
    		return EMPTY_MNSDummy;
    	} else if (MMSConfiguration.REMOVE_ENTRY_MNS_DUMMY && httpMethod == HttpMethod.GET && uri.regionMatches(0, "/removemnsentry", 0, 15)){ 
    		return REMOVE_MNS_ENTRY;
    	}
    	
//    	When relaying
    	else if (httpMethod == HttpMethod.POST || httpMethod == HttpMethod.GET) {
    		String dstInfo = mch.requestDstInfo(dstMRN);
    		
        	if (dstInfo.equals("No")) {
        		return UNKNOWN_MRN;
        	}

        	parser.parseDstInfo(dstInfo);
        	int model = parser.getDstModel();
        	
        	if (model == 2) {//model B (destination MSR, MIR, or MSP as servers)
        		return RELAYING_TO_SERVER;
        	} else {//when model A, it puts the message into the queue
        		return RELAYING_TO_SC;
        	}
    	} else {
    		return UNKNOWN_HTTP_TYPE;
    	}
	}
	

}
