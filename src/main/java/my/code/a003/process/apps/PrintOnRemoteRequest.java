package my.code.a003.process.apps;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

/**
 * Launch MBean {@link MessagePrinterMBean}.
 * http://docs.oracle.com/javase/tutorial/jmx/mbeans/standard.html
 * http://docs.oracle.com/javase/tutorial/jmx/mbeans/mxbeans.html
 */
public class PrintOnRemoteRequest {

	public static void main(String[] args) {
		System.out.println("PrintOnRemoteRequest started");
		new PrintOnRemoteRequest().run();
	}

	public void run() {
		System.out.println("PrintOnRemoteRequest started 2");
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name;
		try {
			name = new ObjectName("my.code.a003.process:type=PrintOnRemoteRequest.MessagePrinter");
			mbs.registerMBean(new MessagePrinter(), name);
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException(e);
		} catch (InstanceAlreadyExistsException e) {
			throw new RuntimeException(e);
		} catch (MBeanRegistrationException e) {
			throw new RuntimeException(e);
		} catch (NotCompliantMBeanException e) {
			throw new RuntimeException(e);
		}

		System.out.println("PrintOnRemoteRequest MBean server successfully started");

		// endless loop
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * https://blogs.oracle.com/jmxetc/entry/javax_management_standardmbean_when_and : 
	 * Standard MBeans must have a management interface that follows strict naming conventions: to be a
	 * compliant MBean, a standard MBean implementation class named [package-name].Y must implement an
	 * interface named [package-name].Y MBean - or must extend a class which does follow these
	 * conventions.
	 */
	public interface MessagePrinterMBean {
		void printMessage(String message);
	}

	public static class MessagePrinter implements MessagePrinterMBean {
		@Override
		public void printMessage(String message) {
			System.out.println(message);
		}
	}
}

