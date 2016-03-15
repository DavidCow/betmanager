package eastbridge;


/**
 *
 * @author Praveen
 */
import java.io.File;
import java.util.Properties;

import jayeson.lib.recordfetcher.DeltaCrawlerSession;

public class DeltaCallBackSubscriber {
	
	public static void main(String args[]) {

		Properties systemProps = System.getProperties();
	
		// setup key stores for secure connections
		systemProps.put("javax.net.ssl.trustStore", "conf/client.ts");
		systemProps.put("javax.net.ssl.keyStore", "conf/client.ks");
		systemProps.put("javax.net.ssl.trustStorePassword", "password");
		systemProps.put("javax.net.ssl.keyStorePassword", "password");
		//setup the configuration file
		systemProps.put("deltaCrawlerSessionConfigurationFile", "conf/deltaCrawlerSession.json");
		
		DeltaCrawlerSession cs = new DeltaCrawlerSession();
		
		cs.connect();
		cs.waitConnection();
		
		DeltaFeedTracker dft = new DeltaFeedTracker();
		cs.addDeltaEventHandler(dft);
		
		while (true) {
	
			try {
				Thread.sleep(5000);
			} catch (Exception ex) {

			}
		}
		
	}
}
