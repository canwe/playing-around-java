# When one JVM is not enough

Few exercises in process spawning (fork), JVM process discovery, inter process communication, MBeans & JMXBeans, Jvmstat, Attach API and handling child process output. 

Investigate what methods JDK tools like VisualVM , JPS and JConsole use. How they discover local running applications?
How they manage to get hold of that internal informations about them?

   * Spawn children process, spawn another JVM application with ProcessBuilder
   * Prove that spawned process can outlive parent process. Child process is not stopped when parent process exits.
   * Capture child process full output to a String and using it in a JUnit style test
   * Call method on another JVM process using JMX. It is an ready to use, convenient way how to
     implement remote method invocation, alternative to RMI or hand written socket communication.
     Convenient especially because one does not have to deal with ports, namely handling
     situations when port is taken.
   * get own PID (process ID) for current application
     * using **Jvmstat** way 
     * using **Attach** API way
     * using **RumtimeJMXBean**
   * spawn children process, spawn another JVM application, and get its PID.
   * **list all running JVM applications** running on localhost, print their PIDs (process IDs) and other 
     characteristic, like JPS tool or VisualVM does.
     * using Jvmstat way 
     * using Attach API way 
   * Implement _tee_ (unix command) and _tail_ like functionality for child process output (see StreamHandler.java)
   * Redirecting child process output to current process output - the Java 7 way
   * Load management agent and get access to target JVM process system properties
   * Load extra functionality in form of a Java class, PrintMessage POJO, into target application,
     LoiteringApp, an already running unsuspected simple application, expose as JMXBean as remotely
     callable method, and make it to do naughty things like print "Hello world!" to standard output on
     JMX command, something it was not originally designed to do.

## Unfinished
Get full command line for current and (external) target JVM process the same way VisualVM gets them. I'm nearly there, just unfinished due to lack of time.
Simple sun.java.command system property provides only basic arguments like main class name and following arguments but not JVM attributes, like -D... or tuning -X: and -XX:  

## Lessons learned

There is no generic pure Java way how to get PID of started child process. If it is non JVM process then you are out of luck.  

There is no straightforward way how to get PID of started child JVM process, however if it is a JVM application, one can use
monitoring and attaching API from tools.jar distributed with JDK, but not JRE. tools.jar cannot be obtained from Maven, it
has to come from the Java distribution.  

There is no generic way how to kill non-java process. If it is a child process and you still hold reference to process, you can use Process#destroy()
If it is not and the target process in not JVM, you are out of luck.  
 
There is not easy straightforward way how to kill a process even if it is JVM application. There is no API call for that, be it Jvmstat or Attach or JMXBean.
the only chance is to inject extra behaviour, using Java Agents, like MBean capable of shutting application down and capable of being operated remotely.

There is no easy, straightforward, way how to connect to a JVM application using JMX if you know the PID of the target application. To establish JMX connection PID has to be converted to Local Connector Address, then converted to JMXServiceURL.
To get Local Connector Address from PID is tricky and involves loading management agent to target application (management-agent.jar) using Attach API.
Agent then exposes `com.sun.management.jmxremote.localConnectorAddress`. A bit hackish but it is the official way recommended by Oracle (in Bugzilla).

There was no easy way how to redirect child process output to current stdout prior Java 7.

In Java 6+ applications you don't have to set up JMXBean server, it is capable of discovery and response to client JMXBean calls.
You can get a lot of runtime information this way. To expose more part of running JVM app you have to load Java Agent, like management-agent.jar. 
Yep, you are loading new behaviour to unsuspected application!   

Stopping child process, even when you have Process reference to it, and using standard Process#destroy() can still fail if the spawned child process spawned
process another process itself (that is grand-children process). This may be issues at least on some (Windows) platforms.  
See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4770092

Java Agent carries a great potential. You can instrument application to do things not originally supposed to do, not only using special startup parameters, like 
with AOP LTW (Load Time Weaving), but also for already running application, completely unsuspecting JVM process! What are the security implications?

TODO: clean up code, clean up javadocs
