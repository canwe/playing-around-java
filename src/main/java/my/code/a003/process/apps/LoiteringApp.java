package my.code.a003.process.apps;

/**
 * Do nothing but keep running endlessly.
 */
public class LoiteringApp {
	public static void main(String[] args) {
		// endless loop
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
