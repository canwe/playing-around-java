package my.code.a003.process.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Convenience input stream handler, intended mainly to capture spawned child process output from
 * {@link Process}. Provide tee (Unix command) like functionality, redirecting one input to several
 * outputs.
 * <p>
 * Check {@link StreamHandler} for better designed class.
 * 
 * @author vkanopelko
 */
public class StreamRedirector {

	/**
	 * Redirect input stream to given output streams. Provide tee (Unix command) like functionality,
	 * redirecting one input to several outputs.
	 * 
	 * @param is
	 *            input stream, for example from spawned child process
	 * @param writes
	 *            list of outputs in form of {@link WriterAdapter} to encapsulate different output
	 *            streams like {@link Writer} or {@link PrintWriter}.
	 */
	public void redirect(final InputStream is, final WriterAdapter... writers) {
		// the only way how to achieve 
		new Thread(new Runnable() {
			@Override
			public void run() {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String line;
				try {
					while ((line = reader.readLine()) != null) {
						for (WriterAdapter writer : writers) {
							writer.println(line);
						}
					}
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * Adapter to various output methods - logger, System.out, StringWriter, ..<br>
	 * Encapsulate classes like {@link PrintWriter} or {@link PrintStream}.<br>
	 * Provide uniform println() method.
	 */
	public static interface WriterAdapter {
		public void println(String s);
	}

	/**
	 * Static factory methods providing adapter of {@link WriterAdapter} for {@link PrintWriter}
	 */
	public WriterAdapter writer(PrintWriter pw) {
		return new WriterAdapterForPrintWriter(pw);
	}

	/**
	 * Static factory methods providing adapter of {@link WriterAdapter} for {@link Writer}
	 */
	public WriterAdapter writer(Writer w) {
		return new WriterAdapterForPrintWriter(new PrintWriter(w));
	}

	/**
	 * Static factory methods providing adapter of {@link WriterAdapter} for {@link PrintStream}
	 */
	public WriterAdapter writer(PrintStream ps) {
		return new WriterAdapterForPrintStream(ps);
	}

	/**
	 * Encapsulate classes like {@link PrintWriter} or {@link PrintStream}. Provide uniform
	 * println() method.
	 */
	public static class WriterAdapterForPrintWriter implements WriterAdapter {
		PrintWriter pw;

		public WriterAdapterForPrintWriter(PrintWriter pw) {
			this.pw = pw;
		}

		public void println(String s) {
			pw.println(s);
		}
	}

	/**
	 * Encapsulate classes like {@link PrintWriter} or {@link PrintStream}. Provide uniform
	 * println() method.
	 */
	public static class WriterAdapterForPrintStream implements WriterAdapter {
		PrintStream ps;

		public WriterAdapterForPrintStream(PrintStream ps) {
			this.ps = ps;
		}

		public void println(String s) {
			ps.println(s);
		}
	}
}
