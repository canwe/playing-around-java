package my.code.a003.process;

import java.util.Set;

import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

/**
 * Print process ID. How can a Java program get it's own process ID? Way using
 * {@link sun.jvmstat.monitor.MonitoredHost#activeVms()}
 * <p>
 * Limitation: This way fails when multiple class have the same class, when this application was
 * launched several times.
 * <p>
 * Limitation: I am not sure how this will react when application is launched as executable JAR
 * (java -jar foo.jar)
 */
public class A05GetOwnPidUsingJvmstat {

	public static void main(String[] args) {
		new A05GetOwnPidUsingJvmstat().run();
	}

	public void run() {
		System.out.println(getPid(this.getClass()));
	}

	/**
	 * Get PID for given main class
	 * 
	 * @param mainClass
	 * @return
	 */
	public Integer getPid(Class<?> mainClass) {
		MonitoredHost monitoredHost;
		Set<Integer> activeVmPids;
		try {
			monitoredHost = MonitoredHost.getMonitoredHost(new HostIdentifier((String) null));
			activeVmPids = monitoredHost.activeVms();
			MonitoredVm mvm = null;
			for (Integer vmPid : activeVmPids) {
				try {
					mvm = monitoredHost.getMonitoredVm(new VmIdentifier(vmPid.toString()));
					String mvmMainClass = MonitoredVmUtil.mainClass(mvm, true);
					if (mainClass.getName().equals(mvmMainClass)) {
						return vmPid;
					}
				} finally {
					if (mvm != null) {
						mvm.detach();
					}
				}
			}
		} catch (java.net.URISyntaxException e) {
			throw new InternalError(e.getMessage());
		} catch (MonitorException e) {
			throw new InternalError(e.getMessage());
		}
		return null;
	}
}