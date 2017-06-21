package my.code.a003.process;

import java.lang.management.ManagementFactory;

/**
 * Print process ID. How can a Java program get it's own process ID?
 *  
 * MBean way; using
 * {@link java.lang.management.ManagementFactory#getRuntimeMXBean()} and
 * {@link java.lang.management.RuntimeMXBean#getName()}
 * 
 * This is the suggested way, VisualVM is supposed to use this approach.
 */
public class A05GetOwnPidUsingRuntimeMXBean {

	public static void main(String[] args) {
		new A05GetOwnPidUsingRuntimeMXBean().run();
	}

	public void run() {
		System.out.println(getPid());
	}

	/**
	 * Get self PID.<br>
	 * This version is from VisualvmVM sources:<br>
	 * com.sun.tools.visualvm.application.jvm.Jvm#ApplicationSupport#createCurrentApplication()
	 * <p>
	 * But similar version is here:
	 * See http://stackoverflow.com/a/7690178/1185845 See
	 * http://stackoverflow.com/questions/35842/how-can-a-java-program-get-its-own-process-id
	 */
	public Integer getPid() {
		String selfName = ManagementFactory.getRuntimeMXBean().getName();
		return Integer.valueOf(selfName.substring(0, selfName.indexOf('@')));
	}
}