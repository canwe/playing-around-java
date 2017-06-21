package my.code.a003.process;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import my.code.a003.process.apps.LoiteringApp;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * Print all running JVMs, the Java Attach API way, see {@link VirtualMachine#list()}. Java Attach
 * API resides in tools.jar, same as Jvmstat classes. Honestly, I have no idea which way is better,
 * Jvmstat or Attach, I would recommend Jvmstat for discovering JVM processes since it is what JPS
 * tool uses.
 * 
 * @see http://blog.42.nl/articles/connecting-to-a-jvm-programmatically/
 */
public class A042ListLocalRunningJvmsUsingAttachApi {

	public static void main(String[] args) throws Exception {
		runTestApp();

		// print the PIDs and other application info
		List<VirtualMachineDescriptor> vms = VirtualMachine.list();
		for (VirtualMachineDescriptor virtualMachineDescriptor : vms) {
			System.out.print("" +
				"pid: " + virtualMachineDescriptor.id() + ", " +
				"display name: " + virtualMachineDescriptor.displayName() + ", " +
				"main class: " + extractMainClassNameFromDisplayName(virtualMachineDescriptor.displayName()) + ", " +
				"command line: " + virtualMachineDescriptor.displayName());

			// to get system properties (or to load java agent) we need to attach to VM
			VirtualMachine virtualMachine = VirtualMachine.attach(virtualMachineDescriptor);
			if (virtualMachine != null) {
				Properties targetSystemProperties = virtualMachine.getSystemProperties();
				System.out.print(", ");
				System.out.print("command (from sysprops): " + targetSystemProperties.getProperty("sun.java.command"));
			}
			virtualMachine.detach();

			System.out.println();
		}
	}

	private static String extractMainClassNameFromDisplayName(String displayName) {
		int pos = displayName.indexOf(" ");
		if (pos > 0) {
			return displayName.substring(0, pos);
		} else {
			return displayName;
		}
	}

	/**
	 * Run test app, never ending {@link LoiteringApp} just for the main program having something
	 * more to discover and list.
	 */
	public static void runTestApp() {
		String classpath = System.getProperty("java.class.path");
		ProcessBuilder testedAppBuilder = new ProcessBuilder(
			"java",
			"-cp", classpath, // they MUST BE separated as two arguments, otherwise you get -cp is not recognised option -
			"-Dsomething=foo",
			"-Djava.util.logging.ConsoleHandler.level=FINE",
			LoiteringApp.class.getName(),
			"foo1"
			);
		try {
			testedAppBuilder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}