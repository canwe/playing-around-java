package my.code.a003.process.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Test {@link JarFileProviderMavenStandardsImpl}
 * 
 * @author vkanopelko
 */
public class JarFileProviderTest {

	@Test
	@Ignore("There is an issues with Maven calling test first and only then assembly:assembly goal. Until this is solved ignoring")
	public void existingJarFileShouldReturnCorrectFile() {
		JarFileProvider jarFileProvider = new JarFileProviderMavenStandardsImpl();
		File jarFile = jarFileProvider.getJarFire("add-mbean-agent.jar");
		assertNotNull(jarFile);
		assertTrue(jarFile.exists());
	}

	@Test(expected = IllegalArgumentException.class)
	public void nonExistingJarFileShouldThrowException() {
		JarFileProvider jarFileProvider = new JarFileProviderMavenStandardsImpl();
		jarFileProvider.getJarFire("foofoofoo.jar");
	}
}
