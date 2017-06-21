package my.code.a003.process.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

/**
 * Convenience input stream handler, intended mainly to capture spawned child process output from
 * {@link Process}. Provide tee (Unix command) like functionality, redirecting one input to several
 * outputs.
 * <p>
 * Example of usage:
 * 
 * <pre>
 * StringWriter capturedOutput = new StringWriter();
 * StreamHandler.input(childProcess.getInputStream()).outputTo(System.out).outputTo(System.err).outputTo(capturedOutput).start();
 * </pre>
 * 
 * @author vkanopelko
 */
public class StreamHandler {

	final private InputStream inputStream;

	private final List<WriterAdapter> outputs = new LinkedList<>();

	private StreamHandler(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public static StreamHandler input(InputStream inputStream) {
		return new StreamHandler(inputStream);
	}

	public StreamHandler outputTo(PrintWriter pw) {
		outputs.add(new WriterAdapterForPrintWriter(pw));
		return this;
	}

	public StreamHandler outputTo(Writer w) {
		outputs.add(new WriterAdapterForPrintWriter(new PrintWriter(w)));
		return this;
	}

	public StreamHandler outputTo(PrintStream ps) {
		outputs.add(new WriterAdapterForPrintStream(ps));
		return this;
	}

	public void start() {
		redirect(inputStream, outputs.toArray(new WriterAdapter[outputs.size()]));
	}

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
	private void redirect(final InputStream is, final WriterAdapter... writers) {
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
	private static interface WriterAdapter {
		public void println(String s);
	}

	/**
	 * Encapsulate classes like {@link PrintWriter} or {@link PrintStream}. Provide uniform
	 * println() method.
	 */
	private static class WriterAdapterForPrintWriter implements WriterAdapter {
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
	private static class WriterAdapterForPrintStream implements WriterAdapter {
		PrintStream ps;

		public WriterAdapterForPrintStream(PrintStream ps) {
			this.ps = ps;
		}

		public void println(String s) {
			ps.println(s);
		}
	}
}
