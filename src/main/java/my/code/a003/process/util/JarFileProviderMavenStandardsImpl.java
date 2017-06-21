package my.code.a003.process.util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Convert JAR library file name to {@link File} on local file system. Assume that JAR library file
 * is placed according to Maven setup one level above directory with compiled clasess.
 * 
 * @author vkanopelko
 */
public class JarFileProviderMavenStandardsImpl implements JarFileProvider {

	/**
	 * Convert JAR library file name to {@link File} on local file system.
	 */
	@Override
	public File getJarFire(String jarFileName) {
		URI targetClassDirName = null;
		try {
			targetClassDirName = this.getClass().getResource("/").toURI();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
		File targetDir = new File(targetClassDirName).getParentFile();
		File jarFile = new File(targetDir, jarFileName);
		if (!jarFile.exists()) {
			throw new IllegalArgumentException(jarFile.getAbsolutePath() + " was not found");
		}
		return jarFile;
	}
}
