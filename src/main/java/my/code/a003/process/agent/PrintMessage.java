package my.code.a003.process.agent;

/**
 * This ordinary POJO will be turned into MBean and forcefully loaded via Java Agent into
 * unsuspected application.
 * 
 * @author vkanopelko
 */
public class PrintMessage implements PrintMessageMBean {
	@Override
	public void printMessage(String message) {
		System.out.println(message);
	}
}