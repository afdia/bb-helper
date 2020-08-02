package bb.helper.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bb.helper.LogAgent;
import net.bytebuddy.agent.ByteBuddyAgent;

public class Main {
	public static final Logger log = LogManager.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
		log.info("Start");
		LogAgent.premain(ByteBuddyAgent.install());
		new OtherClass().sayHello();
		log.info("End");
	}
}
