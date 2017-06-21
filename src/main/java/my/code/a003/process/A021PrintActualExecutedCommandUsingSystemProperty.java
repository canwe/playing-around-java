package my.code.a003.process;

/**
 * Is it possible to get the command used to launch the JVM in Java?
 * http://stackoverflow.com/questions
 * /13958318/is-it-possible-to-get-the-command-used-to-launch-the-jvm-in-java
 * 
 */
public class A021PrintActualExecutedCommandUsingSystemProperty {
	public static void main(String[] args) {
		System.out.println(System.getProperty("sun.java.command"));
		// output: 
		// my.code.a003.process.A02PrintActualExecutedCommand
		// ..this prints only main class name and arguments, no classpat of JVM parameters! This is not sufficient result.
	}
}
