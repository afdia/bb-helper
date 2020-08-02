package bb.helper;

import java.util.Arrays;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

public class LogAdvice {
	public static final Logger log = LogManager.getLogger(LogAdvice.class);

	public static class Storage {
		public Stack<String> lines = new Stack<>();
		public int nestedLevel = 0;
	}

	public static ThreadLocal<Storage> storage = ThreadLocal.withInitial(() -> new Storage());

	@Advice.OnMethodEnter
	public static long enter(@Advice.Origin("#t") String typeName, @Advice.Origin("#m") String methodName, @Advice.AllArguments(typing = Assigner.Typing.DYNAMIC) Object[] args) {
		if (shouldLog(typeName, methodName)) {
			Storage s = storage.get();
			if (s.nestedLevel > 1000) { // deeply nested methods which never return (e.g. running swing applications) should not use up memory. By resetting the storage they are just not logged anymore (the OnMethodExit method ignores them too with the lines.isEmpty check)
				storage.set(new Storage());
			}
			// System.out.println("E " + typeMethod(typeName, methodName) + " " + s.lines.size() + " & " + nestedLevel);
			s.lines.add("<" + typeMethod(typeName, methodName) + ">");
			s.nestedLevel++;
		}
		return System.nanoTime();
	}

	@Advice.OnMethodExit
	public static void exit(@Advice.Enter long startTime, @Advice.Origin("#t") String typeName, @Advice.Origin("#m") String methodName, @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object value, @Advice.AllArguments(typing = Assigner.Typing.DYNAMIC) Object[] args) {
		if (shouldLog(typeName, methodName)) {
			Storage s = storage.get();
			// System.out.println("X " + typeMethod(typeName, methodName) + " " + s.lines.size() + " & " + nestedLevel);
			double durationMs = (System.nanoTime() - startTime) / 1000000.0; // convert from ns to ms

			if (s.lines.isEmpty()) { // safety check for deeply nested methods which are no longer monitored
				return;
			}
			if (durationMs < 5) { // if this method ends faster than 5ms, delete the last logline (such short durations are not logged
				s.lines.pop();
			}
			else { // otherwise add this method
				s.lines.add("</" + typeMethod(typeName, methodName) + " ms=\"" + durationMs + "\" >");
			}
			if (--s.nestedLevel == 0) {
				for (String l : s.lines) {
					log.info(l);
				}
				s.lines.clear();
			}
		}
	}

	public static String typeMethod(String typeName, String methodName) {
		return "call type=\"" + typeName.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("&", "&amp;").replace("'", "&apos;") + "\"" + " method=\"" + methodName.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("&", "&amp;").replace("'", "&apos;") + "\"";
	}

	public static boolean shouldLog(String typeName, String methodName) {
		return !Arrays.asList("<init>", "<clinit>").contains(methodName);// dont log constructor calls (I guess this can also be configured in the LogAgent)
	}

}
