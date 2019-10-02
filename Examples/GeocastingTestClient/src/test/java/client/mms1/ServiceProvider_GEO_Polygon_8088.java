package client.mms1;

import kr.ac.kaist.mms_client.MMSClientHandler;
import kr.ac.kaist.mms_client.MMSConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* -------------------------------------------------------- */

/**
File name : ServiceProvider_GEO_Polygon.java
	Service Provider sends messages through geocasting.
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Version : 0.7.2
Creation Date : 2018-07-27
*/
/* -------------------------------------------------------- */

public class ServiceProvider_GEO_Polygon_8088 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:smart-navi:device:geo-server1";

		MMSConfiguration.MMS_URL="127.0.0.1:8088";
		MMSConfiguration.DEBUG=true;

		MMSClientHandler sender = new MMSClientHandler(myMRN);
		sender.setSender((headerField, message) ->System.out.println(message));
		
		Map<String, List<String>> headerfield = new HashMap<String, List<String>>();
		List<String> geoType = new ArrayList<String>(); 
		geoType.add("polygon");
		headerfield.put("geocasting",geoType);
		List<String> latValue = new ArrayList<String>();
		latValue.add("1");
		latValue.add("2");
		latValue.add("3");
		latValue.add("4");
		latValue.add("1");
		headerfield.put("lat", latValue);
		List<String> longValue = new ArrayList<String>();
		longValue.add("9");
		longValue.add("7");
		longValue.add("5");
		longValue.add("3");
		longValue.add("1");
		headerfield.put("long", longValue);
		sender.setMsgHeader(headerfield);

		String dstMRN = "*";
		sender.sendPostMsg(dstMRN, "the Geocasting Polygon Message from " + myMRN ,6000);

	}
}
