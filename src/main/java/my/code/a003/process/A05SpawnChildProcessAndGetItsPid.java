package my.code.a003.process;

import my.code.a003.process.apps.LoiteringApp;
import my.code.a003.process.util.ActiveJavaVirtualMachineExplorer;

/**
 * Print child process PID (process ID) Luckily, our child process is another JVM based application
 * and we know main class, so we can use Jvmstat (in form of convenient
 * {@link ActiveJavaVirtualMachineExplorer} and check it against all detected active JVMs. There is
 * no general pure Java way how to get PID of child process. Sad. Only hacks and native libraries.
 * 
 * @see http://stackoverflow.com/questions/4750470 - How to get PID of process I've just started
 *      within java program?
 */
public class A05SpawnChildProcessAndGetItsPid {
	// 
	public static void main(String[] args) throws Exception {
		String classpath = System.getProperty("java.class.path");
		ProcessBuilder childProcessBuilder = new ProcessBuilder(
			"java",
			"-cp", classpath, // they MUST BE separated as two arguments, otherwise you get -cp is not recognised option -
			"-Dsomething=foo",
			"-Djava.util.logging.ConsoleHandler.level=FINE",
			LoiteringApp.class.getName(),
			"foo1"
			);

		Process childProcess = null;
		try {
			childProcess = childProcessBuilder.start();
			Integer pid = ActiveJavaVirtualMachineExplorer.getPid(LoiteringApp.class);
			System.out.println("Child process PID is " + pid);
		} finally {
			if (childProcess != null)
				childProcess.destroy();
		}
	}
}