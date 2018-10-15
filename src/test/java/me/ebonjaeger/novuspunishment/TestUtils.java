package me.ebonjaeger.novuspunishment;

import java.io.File;
import java.lang.reflect.Field;
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

	/**
	 * Sets the field of the given class.
	 *
	 * @param clazz The class on which the field is declared
	 * @param fieldName The field name
	 * @param instance The instance to set the field on (null for static fields)
	 * @param value The value to set
	 * @param <T> The instance's type
	 */
	public static <T> void setField(Class<? super T> clazz, String fieldName, T instance, Object value) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(instance, value);
		} catch (NoSuchFieldException | IllegalAccessException ex) {
			throw new IllegalStateException("Could not set field '" + fieldName + "' on " + instance, ex);
		}
	}
}
