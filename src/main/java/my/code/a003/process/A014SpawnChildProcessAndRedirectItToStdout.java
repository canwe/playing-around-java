package my.code.a003.process;

import my.code.a003.process.apps.PrintCurrentTimeApp;

/**
 * Spawn a child process, another JVM, a simple Java application printing current time and keep it
 * running for a short time. Redirect child application stdout to this application stdout and stderr
 * so we can see the time being printed (otherwise it is not). In this variant uses new
 * functionality in Java 7:
 * {@link ProcessBuilder#redirectOutput(java.lang.ProcessBuilder.Redirect)} and
 * {@link java.lang.ProcessBuilder.Redirect#INHERIT}.
 * 
 * @author vkanopelko
 */
public class A014SpawnChildProcessAndRedirectItToStdout {

	private static int KEEP_IT_RUNNING_INTERVAL_IN_MS = 30000; // half a minute

	public static void main(String[] args) throws Exception {
		new A014SpawnChildProcessAndRedirectItToStdout().run();
	}

	public void run() throws Exception {
		Process childProcess = null;
		try {
			ProcessBuilder childProcessBuilder = new ProcessBuilder(
				"java",
				"-cp", System.getProperty("java.class.path"), // this is a must, otherwise it will see only JRE classes and not PrintCurrentTimeApp
				PrintCurrentTimeApp.class.getName()
				);
			childProcessBuilder.redirectErrorStream(true);
			childProcessBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT); // since Java 7
			childProcessBuilder.redirectErrorStream(true);
			childProcess = childProcessBuilder.start();
			Thread.sleep(KEEP_IT_RUNNING_INTERVAL_IN_MS);
		} finally {
			if (childProcess != null) {
				childProcess.destroy(); // kill child process if it was started
			}
		}
	}
}
