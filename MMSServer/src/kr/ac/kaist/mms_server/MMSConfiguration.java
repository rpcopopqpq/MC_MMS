package kr.ac.kaist.mms_server;

/* -------------------------------------------------------- */
/** 
File name : MMSConfiguration.java
Author : Jaehee Ha (jaehee.ha@kaist.ac.kr)
Creation Date : 2017-01-24
Version : 0.3.01
*/
/* -------------------------------------------------------- */

public class MMSConfiguration {
	private static final String TAG = "[MMSConfiguration] ";
	public static final boolean LOGGING = true;
	public static final boolean LOG_PROVIDING = true;
	public static final boolean EMPTY_QUEUE = true;
	public static final boolean EMPTY_MNS_DUMMY = true;
	public static final boolean REMOVE_ENTRY_MNS_DUMMY = true;
	public static final int HTTP_PORT = 8088;
	public static final int HTTPS_PORT = 444;
	public static final int UDP_PORT = 8089;
	public static final String MMS_MRN = "urn:mrn:smart-navi:device:mms1";
}
