package me.ebonjaeger.novuspunishment;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {

	/**
	 * Return a {@link File} to a file in the JAR's resources (main or test).
	 *
	 * @param path The absolute path to the file
	 * @return The project file
	 */
	public static File getJarFile(String path) {
		URI uri = getUriOrThrow(path);
		return new File(uri.getPath());
	}

	/**
	 * Return a {@link Path} to a file in the JAR's resources (main or test).
	 *
	 * @param path The absolute path to the file
	 * @return The Path object to the file
	 */
	public static Path getJarPath(String path) {
		String sqlFilePath = getUriOrThrow(path).getPath();
		// Windows preprends the path with a '/' or '\', which Paths cannot handle
		String appropriatePath = System.getProperty("os.name").contains("indow")
				? sqlFilePath.substring(1)
				: sqlFilePath;
		return Paths.get(appropriatePath);
	}

	private static URI getUriOrThrow(String path) {
		URL url = TestUtils.class.getResource(path);
		if (url == null) {
			throw new IllegalStateException("File '" + path + "' could not be loaded");
		}
		try {
			return new URI(url.toString());
		} catch (URISyntaxException e) {
			throw new IllegalStateException("File '" + path + "' cannot be converted to a URI");
		}
	}
}
