package client.mms2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import kr.ac.kaist.mms_client.*;

/* -------------------------------------------------------- */
/** 
File name : ServiceProvider_GEO_Circle.java
	Service Provider sends messages through geocasting.
Author : Jaehyun Park (jae519@kaist.ac.kr)
Version : 0.5.5
Creation Date : 2016-06-27

Rev. history : 2018-07-27
Version : 0.7.2
	Changed geocasting header fields. 
Modifier : Jaehee Ha (jaehee.ha@kaist.ac.kr)
*/
/* -------------------------------------------------------- */

public class ServiceProvider_GEO_Circle_8089 {
	public static void main(String args[]) throws Exception{
		String myMRN = "urn:mrn:smart-navi:device:geo-server2";

		MMSConfiguration.MMS_URL="127.0.0.1:8089";
		
		MMSClientHandler sender = new MMSClientHandler(myMRN);
		sender.setSender((headerField, message) ->System.out.println(message));
		
		// It is able to set its HTTP header field
		Map<String, List<String>> headerfield = new HashMap<String, List<String>>(); // Header field example. You are able to remove this code.
		List<String> geoType = new ArrayList<String>(); 
		geoType.add("circle");
		headerfield.put("geocasting",geoType);
		List<String> latValue = new ArrayList<String>();
		latValue.add("1");
		headerfield.put("lat", latValue);
		List<String> longValue = new ArrayList<String>();
		longValue.add("2");
		headerfield.put("long", longValue);
		List<String> radiusValue = new ArrayList<String>(); 
		radiusValue.add("3");
		headerfield.put("radius",radiusValue);
		
		sender.setMsgHeader(headerfield);
		// Header field example ends.
		
		String dstMRN = "*";//"urn:mrn:smart-navi:device:mms1";
		sender.sendPostMsg(dstMRN, "the Geocasting Circle Message from " + myMRN ,6000);
		
		

	}
}
