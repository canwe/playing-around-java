package my.code.a003.process;

import java.io.StringWriter;

import my.code.a003.process.apps.PrintCurrentTimeApp;
import my.code.a003.process.util.StreamRedirector;

/**
 * Check what happens when parent process terminates before child process. Will child process be
 * running? Will it survive parents destruction? Test on simple application printing current time.
 */
public class A03SpawnChildProcessAndStopMain {

	public static String MESSAGE = "Hello world!";

	private final StreamRedirector poh = new StreamRedirector();

	public static void main(String[] args) throws Exception {
		new A03SpawnChildProcessAndStopMain().run();
	}

	public void run() throws Exception {
		String classpath = System.getProperty("java.class.path");
		ProcessBuilder testedAppBuilder = new ProcessBuilder(
			"java",
			"-cp", classpath,
			PrintCurrentTimeApp.class.getName()
			);
		testedAppBuilder.redirectErrorStream(true);
		Process childProcess = null;
		StringWriter capturedOutput = new StringWriter();
		childProcess = testedAppBuilder.start();
		poh.redirect(childProcess.getInputStream(), poh.writer(System.out), poh.writer(capturedOutput));
	}
}
