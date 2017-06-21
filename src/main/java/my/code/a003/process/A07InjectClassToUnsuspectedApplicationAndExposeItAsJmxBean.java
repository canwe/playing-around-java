package my.code.a003.process;

import java.io.File;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import my.code.a003.process.agent.PrintMessage;
import my.code.a003.process.apps.LoiteringApp;
import my.code.a003.process.apps.PrintOnRemoteRequest.MessagePrinterMBean;
import my.code.a003.process.util.ActiveJavaVirtualMachineExplorer;
import my.code.a003.process.util.JarFileProvider;
import my.code.a003.process.util.JarFileProviderMavenStandardsImpl;

import com.sun.tools.attach.VirtualMachine;

/**
 * Load extra functionality in form of a Java class, PrintMessage POJO, into target application,
 * LoiteringApp, an already running unsuspected simple application, expose as JMXBean as remotely
 * callable method, and make it to do naughty things like print "Hello world!" to standard output on
 * JMX command, something it was not originally designed to do.
 *
 * Security, like preventing PrintMessage being called from remote nodes is not covered in this example.
 *
 * This example is
 *
 * @see http://www.javaworld.com/community/node/470
 * @see http://dhruba.name/2010/02/07/creation-dynamic-loading-and-instrumentation-with-javaagents/
 * @see https://blogs.oracle.com/CoreJavaTechTips/entry/the_attach_api
 * @see https://blogs.oracle.com/jmxetc/entry/how_to_retrieve_remote_jvm
 * @see http://blog.42.nl/articles/connecting-to-a-jvm-programmatically/
 *
 * @author vkanopelko
 */
public class A07InjectClassToUnsuspectedApplicationAndExposeItAsJmxBean {

    private static final String ADD_MBEAN_AGENT_JAR = "add-mbean-agent.jar";

    public static final Class<?> TESTED_MBEAN_CLASS = PrintMessage.class;

    public static final Class<?> TARGET_APP_MAIN_CLASS = LoiteringApp.class;

    public static final String TESTED_MBEAN_CLASS_NAME = "my.code.a003.process.agent.PrintMessage";

    public static final String TESTED_MBEAN_OBJECT_NAME = "my.code.a003.process.agent:type=PrintMessage";

    private static final JarFileProvider jarFileProvider = new JarFileProviderMavenStandardsImpl();

    public static void main(String[] args) throws Exception {
        new A07InjectClassToUnsuspectedApplicationAndExposeItAsJmxBean().run();
    }

    public void run() throws Exception {
        Process childProcess = null;
        Integer pid = null;
        try {

            // start child process
            String classpath = System.getProperty("java.class.path");
            ProcessBuilder childProcessBuilder = new ProcessBuilder(
                    "java",
                    "-cp", classpath,
                    LoiteringApp.class.getName()
            );
            childProcessBuilder.redirectErrorStream(true);
            childProcessBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            childProcess = childProcessBuilder.start();
            Thread.sleep(1000L);

            // get PID of child application
            pid = ActiveJavaVirtualMachineExplorer.getPid(TARGET_APP_MAIN_CLASS);
            if (pid == null)
                throw new RuntimeException("PID of the child process was not found");

            // inject PrintMessage to target JVM application, unsuspecting LoiteringApp
            // special Java Agent AddMBeanAgent cause this insertion and expose it as JMXBean on target application
            injectMBeanToTargetProcess(pid, TESTED_MBEAN_CLASS_NAME);

            // connect to child process via MBeans
            JMXServiceURL jmxURL = extractJmxServiceUrl(pid);
            JMXConnector connector = JMXConnectorFactory.connect(jmxURL);
            MBeanServerConnection mbsc = connector.getMBeanServerConnection();
            ObjectName mbeanName = new ObjectName(TESTED_MBEAN_OBJECT_NAME);
            MessagePrinterMBean printerProxy = JMX.newMBeanProxy(mbsc, mbeanName,
                    MessagePrinterMBean.class, true);

            // call method on child process via MBean
            printerProxy.printMessage("Hello world!");

            // close connection to child application
            connector.close();
        } finally {
            if (childProcess != null) {
                System.out.println("Destroying child process pid = " + pid);
                childProcess.destroy();
                childProcess.waitFor();
            }
        }
    }

    private void injectMBeanToTargetProcess(int pid, String mbeanClassName) throws Exception {
        com.sun.tools.attach.VirtualMachine vm = VirtualMachine.attach(String.valueOf(pid));
        String agentJar = jarFileProvider.getJarFire(ADD_MBEAN_AGENT_JAR).getAbsolutePath();
        vm.loadAgent(agentJar, mbeanClassName);
        vm.detach();
    }

    private JMXServiceURL extractJmxServiceUrl(int pid) throws Exception {
        com.sun.tools.attach.VirtualMachine vm = com.sun.tools.attach.VirtualMachine.attach(String.valueOf(pid));
        String javaHome = vm.getSystemProperties().getProperty("java.home");
        String agentJar = javaHome + File.separator + "lib" + File.separator + "management-agent.jar";
        vm.loadAgent(agentJar, "com.sun.management.jmxremote");
        String localConnectorAddress = vm.getAgentProperties().getProperty(
                "com.sun.management.jmxremote.localConnectorAddress");
        if (localConnectorAddress == null) {
            // Check system properties, some JVM implementations may use system properties instead of agent properties
            localConnectorAddress = vm.getSystemProperties().getProperty(
                    "com.sun.management.jmxremote.localConnectorAddress");
        }
        vm.detach();
        System.out.println("Local connector address = " + localConnectorAddress);
        return new JMXServiceURL(localConnectorAddress);
    }
}