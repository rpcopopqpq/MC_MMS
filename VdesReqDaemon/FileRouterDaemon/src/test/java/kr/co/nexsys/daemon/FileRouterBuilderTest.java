package kr.co.nexsys.daemon;

import org.apache.camel.main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileRouterBuilderTest extends Main {
	static Logger LOG = LoggerFactory.getLogger(FileRouterBuilderTest.class);

	public static void main(String[] args) throws Exception {
		FileRouterBuilderTest main = new FileRouterBuilderTest();
		main.addRouteBuilder(new FileRouteBuilder());
		main.run(args);
	}

}
