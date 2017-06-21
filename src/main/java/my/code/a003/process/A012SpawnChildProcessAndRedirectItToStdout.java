package my.code.a003.process;

import my.code.a003.process.apps.PrintCurrentTimeApp;
import my.code.a003.process.util.StreamHandler;

/**
 * Spawn a child process, another JVM, a simple Java application printing current time and keep it running for a short time.
 * Redirect child application stdout to this application stdout so we can see the time being printed (otherwise it is not).
 * Redirecting spawned application output is not that straightforward, no API call, so I created a handy convenience handler 
 * for it, see {@link StreamHandler}. 
 * 
 * @author vkanopelko
 */
public class A012SpawnChildProcessAndRedirectItToStdout {
	
	private static int KEEP_IT_RUNNING_INTERVAL_IN_MS = 30000; // half a minute

	public static void main(String[] args) throws Exception {
		new A012SpawnChildProcessAndRedirectItToStdout().run();
	}

	public void run() throws Exception {
		Process childProcess = null;
		try {
			ProcessBuilder testedAppBuilder = new ProcessBuilder(
				"java",
				"-cp", System.getProperty("java.class.path"), // this is a must, otherwise it will see only JRE classes and not PrintCurrentTimeApp
				PrintCurrentTimeApp.class.getName()
				);
			testedAppBuilder.redirectErrorStream(true);
			childProcess = testedAppBuilder.start();
			StreamHandler.input(childProcess.getInputStream()).outputTo(System.out).outputTo(System.err).start();
			Thread.sleep(KEEP_IT_RUNNING_INTERVAL_IN_MS);
		} finally {
			if (childProcess != null) {
				childProcess.destroy(); // kill child process if it was started
			}
		}
	}
}
