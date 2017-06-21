package my.code.a003.process.agent;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Java Agent main class. Instantiate and set up given class as MBean. This MBean then will be
 * exposed via JMX for remote method call.
 * <p>
 * This agent does not do any instrumentation. Just instantiate MBean and register it with
 * {@link MBeanServer}.
 * 
 * @author vkanopelko
 */
public class AddMBeanAgent {

	/**
	 * Main agent method. This agent does not do any instrumentation. Just instantiate MBean and
	 * register it with {@link MBeanServer}.
	 * 
	 * @param args
	 *            Required one argument which is the MBean class. This class and its interface must
	 *            exist in the classpath or be part of agent JAR.<br>
	 *            Example: my.code.a003.mbean.PrintMessage, then my.code.a003.mbean.PrintMessage
	 *            class and my.code.a003.mbean.PrintMessageMBean interface are expected to be in
	 *            classpath. my.code.a003.mbean.PrintMessage will be exposed via JMX as
	 *            "my.code.a003.mbean:Type=PrintMessage".
	 */
	public static void agentmain(String args, Instrumentation inst) throws Exception {
		try {
//			if (args.length < 1)
//				throw new IllegalArgumentException(
//					"One argument is expected, fully qualified mbean class name is required");
//			String className = args[0];
			String className = args;
			if (className == null)
				throw new IllegalArgumentException(
					"Fully qualified mbean class name cannot b null");
			System.out.println(className);
			Class<?> mbeanClass = Class.forName(className);
			Object mbeanInstance = mbeanClass.newInstance();
			Class<?> mbeanInterfaceClass = Class.forName(getInterfaceNameFromMBeanClassName(className));

			// check that PrintMessage is implementing PrintMessageMBean
			if (!mbeanInterfaceClass.isAssignableFrom(mbeanClass))
				throw new IllegalArgumentException(
					mbeanClass.getName() + " does not implement interface " + mbeanInterfaceClass.getName() + ". It is required by JMX standard.");

			// all OK, let's register the bean as MBean on local paltform  MBeanServer
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName mbeanObjectName = new ObjectName(getObjectNameFromMBeanClassName(className));
			mbs.registerMBean(mbeanInstance, mbeanObjectName);

		} catch (Exception e) {
			System.out.println("Failed to set up AddMBeanAgent");
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Example, get "my.code.a003.mbean:type=PrintMessage" from "my.code.a003.mbean.PrintMessage"
	 */
	static String getObjectNameFromMBeanClassName(String className) {
		int lastDotPosition = className.lastIndexOf('.');
		if (lastDotPosition < 0)
			throw new IllegalArgumentException(
				"Supplied MBean class is not fully qualified, package part is requied - " + className);
		return className.substring(0, lastDotPosition) + ":type=" + className.substring(lastDotPosition + 1,
			className.length());
	}

	/**
	 * Example, get "my.code.a003.mbean.PrintMessageMBean" from "my.code.a003.mbean.PrintMessage"
	 */
	static String getInterfaceNameFromMBeanClassName(String className) {
		return className + "MBean";
	}
}
