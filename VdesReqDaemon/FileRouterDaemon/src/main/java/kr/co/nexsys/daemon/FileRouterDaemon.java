package kr.co.nexsys.daemon;

import org.apache.camel.main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileRouterDaemon extends Main {
	static Logger LOG = LoggerFactory.getLogger(FileRouterDaemon.class);

	public static void main(String[] args) throws Exception {
		FileRouterDaemon main = new FileRouterDaemon();
		main.addRouteBuilder(new FileRouteBuilder());
		main.run(args);
	}

}
