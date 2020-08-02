package bb.helper;

import java.lang.instrument.Instrumentation;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer.ForAdvice;
import net.bytebuddy.matcher.ElementMatchers;

public class LogAgent {
	public static void premain(Instrumentation inst) {
		try {
			new AgentBuilder.Default()
					.type(ElementMatchers.nameStartsWith("")) // put here packagepath if only methods within this package should be intercepted
					.transform(new ForAdvice()
							.include(LogAgent.class.getClassLoader())
							.advice(ElementMatchers.any(), LogAdvice.class.getName()))
					.installOn(inst);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
