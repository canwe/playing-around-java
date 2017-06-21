package my.code.a003.process.apps;

/**
 * Print current time in an endless loop.
 * It is used as a prove of still running process.
 */
public class PrintCurrentTimeApp {
	public static void main(String[] args) {
		// endless loop
		while (true) {
			System.out.println(String.format("%tc", System.currentTimeMillis()));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
