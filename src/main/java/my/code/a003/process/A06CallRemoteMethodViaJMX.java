package my.code.a003.process;

import java.io.File;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import my.code.a003.process.apps.PrintOnRemoteRequest;
import my.code.a003.process.apps.PrintOnRemoteRequest.MessagePrinterMBean;
import my.code.a003.process.util.ActiveJavaVirtualMachineExplorer;

/**
 * Simple example of (mis)using JMXBeans for remote method invocation. Spawn a child process,
 * another JVM, a simple Java application echoing given message on remote call done by MBean
 * (JMXBean) interface. Make it print "Hello world!".
 * <p>
 * Two tricky bits:
 * <ol>
 * <li>get PID (process ID) of spawned child process (covered in previous examples)
 * <li>get MBean connection URL for that PID (main topic for this example)
 * </ol>
 * <p>
 * Note: MBean can be an inner class, see {@link PrintOnRemoteRequest.MessagePrinter} used in this
 * example, however I would not recommend it for real world application.
 * <p>
 * Note: MBean naming is tricky, interface has to have same name as implementation but plus MBean
 * suffix, and implementation name is used in {@link ObjectName}
 * 
 * @see http://docs.oracle.com/javase/tutorial/jmx/remote/custom.html
 * @see https://blogs.oracle.com/jmxetc/entry/how_to_retrieve_remote_jvm
 * @see http://barecode.org/blog.php/establishing-jmx-connections-to-a
 * @see http://www.pongasoft.com/blog/yan/entry/connecting_to_a_local_vm/
 * 
 * @author vkanopelko
 */
public class A06CallRemoteMethodViaJMX {

	public static void main(String[] args) throws Exception {
		new A06CallRemoteMethodViaJMX().run();
	}

	public void run() throws Exception {
		// start child process
		String classpath = System.getProperty("java.class.path");
		ProcessBuilder childProcessBuilder = new ProcessBuilder(
			"java",
			"-cp", classpath,
			PrintOnRemoteRequest.class.getName()
			);
		childProcessBuilder.redirectErrorStream(true);
		childProcessBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		Process childProcess = null;
		try {
			childProcess = childProcessBuilder.start();
			Thread.sleep(1000L);

			// get PID of child application
			Integer pid = ActiveJavaVirtualMachineExplorer.getPid(PrintOnRemoteRequest.class);
			if (pid == null)
				throw new RuntimeException("PID of the child process was not found");

			// connect to child process via MBeans
			JMXServiceURL jmxURL = extractJmxServiceUrl(pid);
			JMXConnector connector = JMXConnectorFactory.connect(jmxURL);
			MBeanServerConnection mbsc = connector.getMBeanServerConnection();
			ObjectName mbeanName = new ObjectName("my.code.a003.process:type=PrintOnRemoteRequest.MessagePrinter");
			MessagePrinterMBean printerProxy = JMX.newMBeanProxy(mbsc, mbeanName,
				MessagePrinterMBean.class, true);

			// call method on child process via MBean
			printerProxy.printMessage("Hello world!");

			// close connection to child application
			connector.close();
		} finally {
			if (childProcess != null) {
				childProcess.destroy();
			}
		}
	}

	private JMXServiceURL extractJmxServiceUrl(int pid) throws Exception {
		// pure madness! just to get local connector address one has to load management-agent.jar to the target application
		// it is the official way! Otherwise localConnectorAddress is always null.
		// See: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6342019 (Bug #6342019 : Provide supported way for JMX clients to obtain local connector address)
		// See: https://blogs.oracle.com/jmxetc/entry/how_to_retrieve_remote_jvm 
		// See: http://barecode.org/blog.php/establishing-jmx-connections-to-a
		com.sun.tools.attach.VirtualMachine vm = com.sun.tools.attach.VirtualMachine.attach(String.valueOf(pid));
		String javaHome = vm.getSystemProperties().getProperty("java.home");
		String agentJar = javaHome + File.separator + "lib" + File.separator + "management-agent.jar";
		vm.loadAgent(agentJar, "com.sun.management.jmxremote");
		String localConnectorAddress = vm.getAgentProperties().getProperty(
			"com.sun.management.jmxremote.localConnectorAddress");
		if (localConnectorAddress == null) {
			// Check system properties, some JVM implementations may use system properties instead of agent properties
			localConnectorAddress = vm.getSystemProperties().getProperty(
				"com.sun.management.jmxremote.localConnectorAddress");
		}
		vm.detach();
		System.out.println("Local connector address = " + localConnectorAddress);
		return new JMXServiceURL(localConnectorAddress);
	}
}
