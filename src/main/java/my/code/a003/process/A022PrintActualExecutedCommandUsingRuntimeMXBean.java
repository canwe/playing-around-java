package my.code.a003.process;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * Is it possible to get the command used to launch the JVM in Java?
 * <p>
 * Use {@link ManagementFactory#getRuntimeMXBean()}.
 * <>
 * See http://stackoverflow.com/questions
 * /13958318/is-it-possible-to-get-the-command-used-to-launch-the-jvm-in-java
 * 
 */
public class A022PrintActualExecutedCommandUsingRuntimeMXBean {
	public static void main(String[] args) {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		System.out.println(runtimeMXBean.getInputArguments());
		System.out.println(runtimeMXBean.getClassPath());
	}
}
