package my.code.a003.process.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

/**
 * Get list of all local active JVMs using tools from sun.jvmstat.monitor package.
 */
public class ActiveJavaVirtualMachineExplorer {

	/**
	 * Represents successfully captured active java virtual machine
	 */
	public static class ActiveVm {
		private int pid;
		private String mainClassName;
		private String commandLine;

		public ActiveVm(int pid, String mainClassName, String commandLine) {
			super();
			this.pid = pid;
			this.mainClassName = mainClassName;
			this.commandLine = commandLine;
		}

		public int getPid() {
			return pid;
		}

		public String getMainClassName() {
			return mainClassName;
		}

		public String getCommandLine() {
			return commandLine;
		}

		@Override
		public String toString() {
			return "ActiveVm [pid=" + pid + ", mainClassName=" + mainClassName + ", commandLine=" + commandLine + "]";
		}
	}

	/**
	 * Represents unsuccessfully captured active java virtual machine, a failure. Keep cause
	 * exception.
	 */
	public static class FailedActiveVm extends ActiveVm {
		private Exception cause;

		public FailedActiveVm(int pid, Exception e) {
			super(pid, null, null);
			cause = e;
		}

		@Override
		public String toString() {
			if (cause != null)
				return "ActiveVm [pid=" + getPid() + ", exception=" + cause.toString() + "]";
			else
				return "ActiveVm [pid=" + getPid() + ", exception=???]";
		}
	}

	/**
	 * Get list of all local active JVMs.
	 * <p>
	 * Returns something like: ActiveVm [pid=7992, name=my.code.z025.util.launch.RunHttpServer]
	 * ActiveVm [pid=6972, name=] ActiveVm [pid=8188, name=my.code.z025.util.launch.RunCodeServer]
	 * ActiveVm [pid=4532, name=org.eclipse.jdt.internal.junit.runner.RemoteTestRunner] The pid=6972
	 * must be Eclipse. So this approach is not water tight.
	 */
	public static List<ActiveVm> getActiveLocalVms() {
		List<ActiveVm> result = new LinkedList<ActiveVm>();
		MonitoredHost monitoredHost;
		Set<Integer> activeVmPids;
		try {
			monitoredHost = MonitoredHost.getMonitoredHost(new HostIdentifier((String) null));
			activeVmPids = monitoredHost.activeVms();
			for (Integer vmPid : activeVmPids) {
				try {
					MonitoredVm mvm = monitoredHost.getMonitoredVm(new VmIdentifier(vmPid.toString()));
					result.add(new ActiveVm(vmPid.intValue(), MonitoredVmUtil.mainClass(mvm, true), MonitoredVmUtil.commandLine(mvm)));
					mvm.detach();
				} catch (Exception e) {
					result.add(new FailedActiveVm(vmPid.intValue(), e));
				}
			}
			return result;
		} catch (java.net.URISyntaxException e) {
			throw new InternalError(e.getMessage());
		} catch (MonitorException e) {
			throw new InternalError(e.getMessage());
		}
	}

	/**
	 * Check if JVM application represented by given Main Class is active and running. Intended to
	 * check if support servers were launched.
	 */
	public static boolean checkActiveJvmForMainClass(Class<?> mainClass) {
		String mainClassName = mainClass.getName();
		List<ActiveVm> activeVms = getActiveLocalVms();
		for (ActiveVm activeVm : activeVms) {
			if (activeVm.getMainClassName().equals(mainClassName)) {
				return true;
			}
		}
		return false;
	}
	
	public static Integer getPid(Class<?> mainClass) {
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
