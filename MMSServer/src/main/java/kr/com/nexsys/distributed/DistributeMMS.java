package kr.com.nexsys.distributed;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kr.ac.kaist.message_relaying.MRH_MessageInputChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import kr.ac.kaist.message_casting.MessageCastingHandler;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel;
import kr.ac.kaist.message_relaying.MRH_MessageOutputChannel.ConnectionThread;
import kr.ac.kaist.message_relaying.MessageTypeDecider;
import kr.ac.kaist.mms_server.MMSConfiguration;

public class DistributeMMS {

	private static final Logger logger = LoggerFactory.getLogger(DistributeMMS.class);
	
	public void visitMMSExecute(MRH_MessageInputChannel.ChannelBean bean, MessageCastingHandler mch ,ArrayList<HashMap<String, String>> mrnIpMapList) {
		HttpHeaders httpHeaders = bean.getReq().headers();
		
		boolean mainMMS = true;
		
		for (Iterator<Map.Entry<String, String>> htr = httpHeaders.iteratorAsString(); htr.hasNext();) {
			Map.Entry<String, String> htrValue = htr.next();
			if(htrValue.getKey().equals("D-MMSMrn") && htrValue.getValue().equals(MMSConfiguration.getMmsMrn())) {
				mainMMS = false;
			}
		}

		if(mainMMS) {
			
			for(int i=0; i<mrnIpMapList.size(); i++) {
				
				if(MMSConfiguration.getMmsMrn() != null && !MMSConfiguration.getMmsMrn().equals(mrnIpMapList.get(i).get("mrn"))) {
					//mch.asynchronizedUnicastDmms(outputChannel, req, mrnIpMapList.get(i).get("ip"), Integer.parseInt(mrnIpMapList.get(i).get("port").toString()), protocol, httpMethod, mrnIpMapList.get(i).get("mrn"), dstMRN); // Execute this relaying process
					 mch.asynchronizedUnicastDmms(bean, mrnIpMapList.get(i).get("mrn"));
				}
			}
			
		}
	}
	
}
