package my.code.a003.process.agent;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test {@link AddMBeanAgent}
 *
 * @author vkanopelko
 */
public class AddMBeanAgentTest {
	
	@Test
	public void getInterfaceNameFromMBeanClassName() {
		assertEquals("my.code.a003.process.agent.PrintMessageMBean", AddMBeanAgent.getInterfaceNameFromMBeanClassName("my.code.a003.process.agent.PrintMessage"));
	}
	
	@Test
	public void getObjectNameFromMBeanClassName() {
		assertEquals("my.code.a003.process.agent:type=PrintMessage", AddMBeanAgent.getObjectNameFromMBeanClassName("my.code.a003.process.agent.PrintMessage"));
	}
	
	@Test
	public void testAgentMainMethod() throws Exception {
		AddMBeanAgent.agentmain("my.code.a003.process.agent.PrintMessage", null);
	}
}
