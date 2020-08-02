package bb.helper;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

public class LogAdvice {
	public static final Logger log = LogManager.getLogger(LogAdvice.class);

	@Advice.OnMethodEnter
	public static long enter(@Advice.Origin("#t") String typeName, @Advice.Origin("#m") String methodName, @Advice.AllArguments(typing = Assigner.Typing.DYNAMIC) Object[] args) {
		if (shouldLog(typeName, methodName)) {
			log.info("<" + typeMethod(typeName, methodName) + ">");
		}
		return System.nanoTime();
	}

	@Advice.OnMethodExit
	public static void exit(@Advice.Enter long startTime, @Advice.Origin("#t") String typeName, @Advice.Origin("#m") String methodName, @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object value, @Advice.AllArguments(typing = Assigner.Typing.DYNAMIC) Object[] args) {
		double durationMs = (System.nanoTime() - startTime) / 1000000.0; // convert from ns to ms
		if (shouldLog(typeName, methodName)) {
			log.info("</" + typeMethod(typeName, methodName) + " ms=\"" + durationMs + "\" >");
		}
	}

	public static String typeMethod(String typeName, String methodName) {
		return "call type=\"" + typeName.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("&", "&amp;").replace("'", "&apos;") + "\""
				+ " method=\"" + methodName.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("&", "&amp;").replace("'", "&apos;") + "\"";
	}

	public static boolean shouldLog(String typeName, String methodName) {
		return !Arrays.asList("<init>", "<clinit>").contains(methodName);// dont log constructor calls (I guess this can also be configured in the LogAgent)
	}

}
