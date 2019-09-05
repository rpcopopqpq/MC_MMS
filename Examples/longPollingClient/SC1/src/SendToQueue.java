import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class SendToQueue {

  /**
 * @param argv
 * @throws Exception
 */
public static void main(String[] argv) throws Exception {
	String quenmSrc= "urn:mrn:imo:imo-no:100000";
	String quenmSp = "::urn:mrn:smart-navi:device:service-SV10-B";
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    factory.setUsername("guest");
	factory.setPassword("guest");
	factory.setVirtualHost("/");
	factory.setPort(5672);
	

		for (int n = 0; n < 1; n++) {
			for (int i = 1; i < 4; i++) {
				Connection connection = factory.newConnection();
				Channel channel = connection.createChannel();
				StringBuffer quenm = new StringBuffer().append(quenmSrc).append(i).append(quenmSp);

				channel.queueDeclare(quenm.toString(), true, false, false, null);
				String message = System.lineSeparator() + System.lineSeparator()
						+ "sc"+i+" long polling thread ************** thread"+System.lineSeparator();
				channel.basicPublish("", quenm.toString(), null, message.getBytes("UTF-8"));
				System.out.println(" [x] Sent '" + message + "'");

				channel.close();
				connection.close();
			}
			Thread.sleep(3000);
		}
	}
}