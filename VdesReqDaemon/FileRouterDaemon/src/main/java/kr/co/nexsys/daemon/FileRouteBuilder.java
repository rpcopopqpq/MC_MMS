package kr.co.nexsys.daemon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileRouteBuilder extends RouteBuilder {
	static Logger LOG = LoggerFactory.getLogger(FileRouteBuilder.class);

	public void configure() {
		from("file://target/input?include=.*\\.json&delay=10000&preMove=staging&move=.completed&moveFailed=error")
		.process(new Processor() {
			@SuppressWarnings({ "unchecked" })
			public void process(Exchange exchange) {
				List<String> jsons = new ArrayList<>();	
				Message msg = exchange.getIn();
				
				File file = msg.getBody(File.class);
				LOG.error("Processing file: " + file);
				
				String contents = msg.getBody(String.class);
				LOG.error("reading file: " + contents);
				
				jsons.add(contents);
				
				JSONArray jr = new JSONArray();
				jr.addAll(jsons);
				
				JSONParser parser = new JSONParser(); 
				Object obj = null;
				JSONObject jsonObj = null; 
				
				String visitSc = null;
				
				if(jr.size()>0) {
					
					try {
						obj = parser.parse( jr.get(0).toString() );
					} catch (ParseException e) {
						///e.printStackTrace();
						LOG.info("error on parse");
					}
					jsonObj = (JSONObject) obj;
					visitSc = (String) jsonObj.get("visitSc");
					
					if(visitSc!=null) {
						System.out.println("\r\rvisit sc  === "+jsonObj.get("visitSc"));
						exchange.getIn().setHeader("visitSc", visitSc);
						
						exchange.getIn().setHeader("srcMRN", visitSc);
						exchange.getIn().setHeader("dstMrn", jsonObj.get("dstMrn"));
						
						if(jsonObj.get("srcMRN")!=null) {
							exchange.getIn().setHeader("srcMRN", jsonObj.get("srcMRN"));
						}
						
					}
					jsonObj.clear();
				}
				System.out.println("\r\r\r\r\rbody===="+msg.getBody());
				
			}
		})
		.to("http4://localhost:8088")
		///.log("${body}")
		.to("mock:result")
		;
	}
}
