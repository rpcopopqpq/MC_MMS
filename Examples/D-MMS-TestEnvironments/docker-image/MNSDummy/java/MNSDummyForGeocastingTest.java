import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class MNSDummyForGeocastingTest {
	private static int UNICASTING = 1;
	private static int GEOCASTING = 2;
	private static int GROUPCASTING = 3;


	private static HashMap<String, String> MRNtoIP = new HashMap<String, String>();


	public static void main(String argv[]) throws Exception
	{

		final int mnsPort = 8588;
		String serverNo = argv==null || argv.length < 1 ? "1" : argv[0];
		ServerSocket Sock = new ServerSocket(mnsPort);
		int gcSuggestion = 0;

		if(Integer.parseInt(serverNo)>3){
			throw new RuntimeException("unsupported serverNo");
		}

		MRNtoIP.put("urn:mrn:kr:vessel:neonexsoft:sc" + serverNo,"127.0.0.1:0:1:1.0-1.0-1.0-1.0");
		MRNtoIP.put("urn:mrn:smart-navi:device:geo-server" + serverNo,"127.0.0.1:0:2");

		System.out.println("Listen:"+mnsPort);
		//-----------------------------------------------------

		while(true)
		{
			gcSuggestion++;
			if (gcSuggestion > 1000) {
				System.gc();
				gcSuggestion = 0;
			}
			Socket connectionSocket = Sock.accept();


			//logger.debug("Packet incomming.");

			InputStreamReader in = new InputStreamReader(connectionSocket.getInputStream());
			BufferedReader br = new BufferedReader(in);
			PrintWriter pw = new PrintWriter(connectionSocket.getOutputStream());

			String inputLine;
			StringBuffer buf = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				buf.append(inputLine.trim());
			}
			if (!connectionSocket.isInputShutdown()) {
				connectionSocket.shutdownInput();
			}
			String data = buf.toString();
			
			
			// newly designed interfaces
			if (data.startsWith("{")) {
				try {
					String dataToReply = "";

					JSONParser queryParser = new JSONParser();

					JSONObject query = (JSONObject) queryParser.parse(data);

					if (query.get("unicasting") != null) {
						JSONObject unicastingQuery = (JSONObject) query.get("unicasting");
						String srcMRN = unicastingQuery.get("srcMRN").toString();
						String dstMRN = unicastingQuery.get("dstMRN").toString();
						String IPAddr = unicastingQuery.get("IPAddr").toString();

						String dstInfo = (String)MRNtoIP.get(dstMRN);
						if (dstInfo != null) {
							String splittedDstInfo[] = dstInfo.split(":");
							if (splittedDstInfo[2].equals("1")) { //polling model
								JSONObject connTypePolling = new JSONObject();
								connTypePolling.put("connType", "polling");
								connTypePolling.put("dstMRN", dstMRN);
								connTypePolling.put("netType", "LTE-M");
								dataToReply = connTypePolling.toJSONString();
							}
							else if (splittedDstInfo[2].equals("2")) { //push model
								JSONObject connTypePush = new JSONObject();
								connTypePush.put("connType", "push");
								connTypePush.put("dstMRN", dstMRN);
								connTypePush.put("IPAddr", splittedDstInfo[0]);
								connTypePush.put("portNum", splittedDstInfo[1]);
								dataToReply = connTypePush.toJSONString();
							}
						}
						else {
							dataToReply = "No";
						}


					} 
					else if (query.get("geocasting_circle") != null) {
						JSONObject geocastingQuery = (JSONObject) query.get("geocasting_circle");
						String srcMRN = geocastingQuery.get("srcMRN").toString();
						String dstMRN = geocastingQuery.get("dstMRN").toString();
						String geoLat = geocastingQuery.get("lat").toString();
						String geoLong = geocastingQuery.get("long").toString();
						String geoRadius = geocastingQuery.get("radius").toString();

						float lat = Float.parseFloat(geoLat); 
						float lon = Float.parseFloat(geoLat);
						float rad = Float.parseFloat(geoRadius);

						if ( 20000 >= rad && 90 >= Math.abs(lat) && 180 >= Math.abs(lon)) {
							Set<String> keys = MRNtoIP.keySet();

							Iterator<String> keysIter = keys.iterator();
							// MRN lists are returned by json format.
							JSONArray objList = new JSONArray();


							if (keysIter.hasNext()){
								do{
									String key = keysIter.next();
									String value = MRNtoIP.get(key);
									String[] parsedVal = value.split(":");
									if (parsedVal.length == 4){ // Geo-information exists.
										String[] curGeoMRN = parsedVal[3].split("-");
										float curLat = Float.parseFloat(curGeoMRN[1]); 
										float curLong = Float.parseFloat(curGeoMRN[3]);


										if (((lat-curLat)*(lat-curLat) + (lon-curLong)*(lon-curLong)) < rad * rad){
											JSONObject item = new JSONObject();
											item.put("dstMRN", key);
											item.put("netType", "LTE-M");
											if (parsedVal[2].equals("1")) {
												item.put("connType", "polling");
											}
											else if (parsedVal[1].equals("2")) {
												item.put("connType", "push");
											}
											objList.add(item);
										}
									}


								}while(keysIter.hasNext());
							}
							dataToReply = objList.toJSONString();
						}
					}
					else if (query.get("geocasting_polygon") != null) {
						JSONObject geocastingQuery = (JSONObject) query.get("geocasting_polygon");
						String srcMRN = geocastingQuery.get("srcMRN").toString();
						String dstMRN = geocastingQuery.get("dstMRN").toString();
						String geoLat = geocastingQuery.get("lat").toString();
						String geoLong = geocastingQuery.get("long").toString();

						Set<String> keys = MRNtoIP.keySet();
						Iterator<String> keysIter = keys.iterator();
						JSONArray objList = new JSONArray();

						if (keysIter.hasNext()){
							do {
								String key = keysIter.next();
								String value = MRNtoIP.get(key);
								String[] parsedVal = value.split(":");

								if(parsedVal.length == 4) { // Geo-information exists.
									String[] curGeoMRN = parsedVal[3].split("-");

									JSONObject item = new JSONObject();
									item.put("dstMRN", key);
									item.put("netType", "LTE-M");
									if(parsedVal[2].equals("1")) {
										item.put("connType", "polling");
									}else if(parsedVal[1].equals("2")) {
										item.put("connType", "push");
									}
									objList.add(item);

								}
							}while(keysIter.hasNext());
						}
						dataToReply = objList.toJSONString();

						//System.out.println("Geocating polygon, srcMRN="+srcMRN+", dstMRN="+dstMRN+", geoLat="+geoLat+", geoLong="+geoLong);
						//dataToReply = "[{\"exception\":\"absent MRN\"}]";
					}
					System.out.println("dataToReply : " + dataToReply);
					pw.println(dataToReply);
					pw.flush();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (pw != null) {
						pw.close();
					}
					if (br != null) {
						br.close();						
					}
					if (in != null) {
						in.close();
					}
					if (connectionSocket != null) {
						connectionSocket.close();
					}
				}
				continue;
			}
			//logger.debug(data);

			String dataToReply = "MNSDummy-Reply:";

			if (data.regionMatches(0, "MRN-Request:", 0, 12)){

				data = data.substring(12);



				//loggerdebug("MNSDummy:data=" + data);
				if (!data.regionMatches(0, "urn:mrn:mcs:casting:geocast:smart:",0,34)){
					try {
						if (MRNtoIP.containsKey(data)) {
							dataToReply += MRNtoIP.get(data);
						}
						else {
							//loggerdebug("No MRN to IP Mapping.");
							dataToReply = "No";
						}
						//loggerdebug(dataToReply);

						pw.println(dataToReply);
						pw.flush();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					finally {
						if (pw != null) {
							pw.close();
						}
						if (br != null) {
							br.close();						
						}
						if (in != null) {
							in.close();
						}
						if (connectionSocket != null) {
							connectionSocket.close();
						}
					}
				}
				else { // if geocasting (urn:mrn:mcs:casting:geocasting:smart:-)


					String geoMRN = data.substring(34);
					String[] parsedGeoMRN = geoMRN.split("-");
					//loggerinfo("Geocasting MRN="+geoMRN+".");
					float lat = Float.parseFloat(parsedGeoMRN[1]); 
					float lon = Float.parseFloat(parsedGeoMRN[3]);
					float rad = Float.parseFloat(parsedGeoMRN[5]);

					if ( 20000 <= rad && 90 >= Math.abs(lat) && 180 >= Math.abs(lon)) {
						try {
							Set<String> keys = MRNtoIP.keySet();

							Iterator<String> keysIter = keys.iterator();
							// MRN lists are returned by json format.
							// {"poll":[{"mrn":"urn:mrn:-"},{"mrn":"urn:mrn:-"},{"mrn":"urn:mrn:-"},....]}
							JSONArray objlist = new JSONArray();


							if (keysIter.hasNext()){
								do{
									String key = keysIter.next();
									String value = MRNtoIP.get(key);
									String[] parsedVal = value.split(":");
									if (parsedVal.length == 4){ // Geo-information exists.
										String[] curGeoMRN = parsedVal[3].split("-");
										float curLat = Float.parseFloat(curGeoMRN[1]); 
										float curLong = Float.parseFloat(curGeoMRN[3]);


										if (((lat-curLat)*(lat-curLat) + (lon-curLong)*(lon-curLong)) < rad * rad){
											JSONObject item = new JSONObject();
											item.put("dstMRN", key);
											objlist.add(item);
										}
									}


								} while(keysIter.hasNext());
							}
							JSONObject dstMRNs = new JSONObject();
							dstMRNs.put("poll", objlist);

							pw.println("MNSDummy-Reply:" + dstMRNs.toString());
							pw.flush();
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						finally {
							if (pw != null) {
								pw.close();
							}
							if (br != null) {
								br.close();						
							}
							if (in != null) {
								in.close();
							}
							if (connectionSocket != null) {
								connectionSocket.close();
							}
						}
					} 
					else {
						try {
							JSONArray objlist = new JSONArray();
							JSONObject dstMRNs = new JSONObject();
							dstMRNs.put("poll", objlist);

							pw.println("MNSDummy-Reply:" + dstMRNs.toString());
							pw.flush();
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						finally {
							if (pw != null) {
								pw.close();
							}
							if (br != null) {
								br.close();						
							}
							if (in != null) {
								in.close();
							}
							if (connectionSocket != null) {
								connectionSocket.close();
							}
						}
					}
				}
			} 
			else if (data.regionMatches(0, "Location-Update:", 0, 16)){
				try {
					data = data.substring(16);

					//loggerinfo("MNSDummy:data=" + data);
					String[] data_sub = data.split(",");
					
					if (MRNtoIP.get(data_sub[1]) == null || MRNtoIP.get(data_sub[1]).split(":").length == 3 ) {
						// data_sub = IP_address, MRN, Port
						MRNtoIP.put(data_sub[1], data_sub[0] + ":" + data_sub[2] + ":" + data_sub[3]);
					}

					pw.println("OK");
					pw.flush();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (pw != null) {
						pw.close();
					}
					if (br != null) {
						br.close();						
					}
					if (in != null) {
						in.close();
					}
					if (connectionSocket != null) {
						connectionSocket.close();
					}
				}

			} 
			else if (data.regionMatches(0, "Dump-MNS:", 0, 9)){
				try {
					if (!MRNtoIP.isEmpty()){
						SortedSet<String> keys = new TreeSet<String>(MRNtoIP.keySet());
						for (String key : keys) {
							String value = MRNtoIP.get(key);
							String values[] = value.split(":");
							dataToReply = dataToReply + "<tr>"
										+ "<td>" + key + "</td>"
										+ "<td>" + values[0] + "</td>"
										+ "<td>" + values[1] + "</td>"
										+ "<td>" + values[2] + "</td>"
										+ "</tr>";
						}
					}
					else{
						//loggerdebug("No MRN to IP Mapping.");
						dataToReply = "No";
					}

					pw.println(dataToReply);
					pw.flush();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (pw != null) {
						try {
							pw.close();
						}
						catch (Exception e) {

						}
					}
					if (br != null) {
						try {
							br.close();	
						}
						catch (IOException e) {

						}
					}
					if (in != null) {
						try {
							in.close();
						}
						catch (IOException e) {

						}
					}
					if (connectionSocket != null) {
						try {
							connectionSocket.close();
						}
						catch (IOException e) {

						}
					}
				}

			}
			else if (data.equals("Empty-MNS:")){
				try {
					MRNtoIP.clear();
					//loggerwarn("MNSDummy:EMPTY.");
					pw.println("");
					pw.flush();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (pw != null) {
						try {
							pw.close();
						}
						catch (Exception e) {

						}
					}
					if (br != null) {
						try {
							br.close();	
						}
						catch (IOException e) {

						}
					}
					if (in != null) {
						try {
							in.close();
						}
						catch (IOException e) {

						}
					}
					if (connectionSocket != null) {
						try {
							connectionSocket.close();
						}
						catch (IOException e) {

						}
					}
				}

			}
			else if (data.regionMatches(0, "Remove-Entry:", 0, 13)){
				try {
					String mrn = data.substring(13);
					MRNtoIP.remove(mrn);
					//loggerwarn("MNSDummy:REMOVE="+mrn+".");
					pw.println("");
					pw.flush();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (pw != null) {
						try {
							pw.close();
						}
						catch (Exception e) {

						}
					}
					if (br != null) {
						try {
							br.close();	
						}
						catch (IOException e) {

						}
					}
					if (in != null) {
						try {
							in.close();
						}
						catch (IOException e) {

						}
					}
					if (connectionSocket != null) {
						try {
							connectionSocket.close();
						}
						catch (IOException e) {

						}
					}
				}
			}
			else if (data.regionMatches(0, "Add-Entry:", 0, 10)){
				try {
					String[] params = data.substring(10).split(",");
					String mrn = params[0];
					String locator = params[1] +":"+ params[2] +":"+ params[3];
					System.out.println(mrn+locator);
					MRNtoIP.put(mrn, locator);
					//loggerwarn("MNSDummy:ADD="+mrn+".");

					//Geo-location update function.  
					pw.println("");
					pw.flush();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (pw != null) {
						try {
							pw.close();
						}
						catch (Exception e) {

						}
					}
					if (br != null) {
						try {
							br.close();	
						}
						catch (IOException e) {

						}
					}
					if (in != null) {
						try {
							in.close();
						}
						catch (IOException e) {

						}
					}
					if (connectionSocket != null) {
						try {
							connectionSocket.close();
						}
						catch (IOException e) {

						}
					}
				}
			}
			else if (data.regionMatches(0, "Geo-location-Update:", 0, 20)){
				try {
					//data format: Geo-location-update:
					String[] data_sub = data.split(",");
					//loggerdebug("MNSDummy:Geolocationupdate "+data_sub[1]);
					MRNtoIP.put(data_sub[1], "127.0.0.1" + ":" + data_sub[2] + ":" + data_sub[3] + ":" + data_sub[4]);
					pw.println("");
					pw.flush();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (pw != null) {
						try {
							pw.close();
						}
						catch (Exception e) {
							
						}
					}
					if (br != null) {
						try {
							br.close();	
						}
						catch (IOException e) {
							
						}
					}
					if (in != null) {
						try {
							in.close();
						}
						catch (IOException e) {
							
						}
					}
					if (connectionSocket != null) {
						try {
							connectionSocket.close();
						}
						catch (IOException e) {
							
						}
					}
				}
			} 
			else if(data.regionMatches(0, "IP-Request:", 0, 11)){
				try {
					String address = data.substring(11).split(",")[0];
					String[] parseAddress = address.split(":");
					String mrn = null;
					for(String value : MRNtoIP.keySet()){
						String[] parseValue = MRNtoIP.get(value).split(":");
						if(parseAddress[0].equals(parseValue[0]) 
								&& parseAddress[1].equals(parseValue[1])){
							mrn = value;
							break;
						}
					}

					if(mrn == null){
						dataToReply += "Unregistered MRN in MNS";
					} 
					else {
						dataToReply += mrn;
					}

					pw.println(dataToReply);
					pw.flush();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (pw != null) {
						try {
							pw.close();
						}
						catch (Exception e) {

						}
					}
					if (br != null) {
						try {
							br.close();	
						}
						catch (IOException e) {

						}
					}
					if (in != null) {
						try {
							in.close();
						}
						catch (IOException e) {

						}
					}
					if (connectionSocket != null) {
						try {
							connectionSocket.close();
						}
						catch (IOException e) {

						}
					}
				}
			}
		}
	}
}
