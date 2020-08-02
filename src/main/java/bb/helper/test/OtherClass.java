package bb.helper.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OtherClass {
	public static final Logger log = LogManager.getLogger(OtherClass.class);

	public void sayHello() {
		log.info("Hello");
		sayHelloNested();
	}

	public void sayHelloNested() {
		log.info("Hello Nestes");
		NestedOtherClass.longDurationSay();
	}

	public static class NestedOtherClass {
		public static void longDurationSay() {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {}
			log.info("Long duration Hello");
		}
	}
}
