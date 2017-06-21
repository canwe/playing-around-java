package my.code.a003.process;

import java.io.IOException;
import java.io.StringWriter;

import my.code.a003.process.apps.PrintMessageApp;
import my.code.a003.process.util.StreamRedirector;

import org.junit.Assert;

public class A011SpawnAnotherJvmRun {

	public static String MESSAGE = "Hello world!";

	private final StreamRedirector poh = new StreamRedirector();

	public static void main(String[] args) {
		new A011SpawnAnotherJvmRun().run();
	}

	public void run() {
		String classpath = System.getProperty("java.class.path");
		ProcessBuilder testedAppBuilder = new ProcessBuilder(
			"java",
			"-cp", classpath, // they MUST BE separated as two arguments, otherwise you get -cp is not recognised option -
			//"-Dline.separator=\\n",
			"-Djava.util.logging.ConsoleHandler.level=FINE",
			PrintMessageApp.class.getName(),
			MESSAGE
			);
		testedAppBuilder.redirectErrorStream(true);
		Process childProcess = null;
		StringWriter capturedOutput = new StringWriter();
		
		try {
			childProcess = testedAppBuilder.start();
			poh.redirect(childProcess.getInputStream(), poh.writer(System.out), poh.writer(capturedOutput));

			// this (main) thread is blocked until child application ends
			int exitValue = childProcess.waitFor();

			// check the captured result
			Assert.assertEquals(0, exitValue);
			Assert.assertEquals(MESSAGE, capturedOutput.toString().trim());

		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			if (childProcess != null)
				childProcess.destroy();
			// Killing child process may not be so easy when the child process starts another process itself.
			// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4770092
			// 4770092 : (process) Process.destroy does not kill multiple child processes
		}
	}
}
