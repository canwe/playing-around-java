package my.code.a003.process;

import java.io.IOException;
import java.util.Set;

import my.code.a003.process.apps.LoiteringApp;

import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

/**
 * Print all running JVMs. Jvmstat way.
 * <p>
 * VisualVM also uses {@link MonitoredHost#activeVms()} to get list of PID for all running JVMs on
 * the host.<br>
 * See
 * {@link com.sun.tools.visualvm.jvmstat.application.JvmstatApplicationProvider#registerJvmstatConnection()}
 * and See
 * {@link com.sun.tools.visualvm.jvmstat.application.JvmstatApplicationProvider#processNewApplicationsByPids()}
 * and also JPS uses this way:
 * http://grepcode.com/file_/repository.grepcode.com/java/root/jdk/openjdk/6-b14/sun/tools/jps/Jps.java/?v=source
 * http://www.docjar.com/html/api/sun/tools/jps/Jps.java.html
 * http://stackoverflow.com/questions/7713177/which-api-does-javas-jps-tool-use-internally
 */
public class A04ListLocalRunningJvmsUsingJvmstatMonitoredHost {

	public static void main(String[] args) throws Exception {
		runTestApp();

		// print the PIDs and other application info
		MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(new HostIdentifier((String) null));
		Set<Integer> activeVmPids = monitoredHost.activeVms();
		for (Integer vmPid : activeVmPids) {
			MonitoredVm mvm = monitoredHost.getMonitoredVm(new VmIdentifier(vmPid.toString()));
			System.out.println("" +
				"pid: " + vmPid.intValue() + ", " +
				"main class: " + MonitoredVmUtil.mainClass(mvm, true) + ", " +
				"command line: " + MonitoredVmUtil.commandLine(mvm) + "\n");
			mvm.detach();
		}
	}

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