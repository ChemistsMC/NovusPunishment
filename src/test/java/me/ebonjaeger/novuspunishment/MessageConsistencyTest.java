package me.ebonjaeger.novuspunishment;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.fail;

/**
 * Tests to make sure placeholders are consistant and well-formed in all {@link Message}s.
 */
public class MessageConsistencyTest {

	@Test
	public void validatePlaceholders() {
		// given
		Pattern placeholderRegex = Pattern.compile("\\{\\d}");

		// when/then
		for (Message message : Message.values()) {
			String text = message.getMessage();
			List<String> placeholders = new ArrayList<>();
			String[] words = text.split(" ");

			for (String word : words) {
				// Poor man's regex
				if (word.contains("{") && word.contains("}")) {
					// Get what appears to be a placeholder
					String placeholder = word.substring(word.indexOf("{"), word.indexOf("}") + 1);

					// Check if the placeholder matches the format {#}
					if (!placeholderRegex.matcher(placeholder).matches()) {
						fail(String.format("Malformed placeholder in %s: '%s', near %d",
								message, placeholder, text.indexOf(placeholder) - getOffset(message)));
					}

					// Check if a placeholder with the same index already exists
					if (placeholders.contains(placeholder)) {
						fail(String.format("Duplicate placeholder found in %s: '%s', near %d",
								message, placeholder, text.lastIndexOf(placeholder) - getOffset(message)));
					}

					placeholders.add(placeholder);
				}
			}
		}
	}

	private int getOffset(Message message) {
		int offset = 0;
		switch (message.getPrefix()) {
			case NONE:
				break;
			case INFO:
			case ERROR:
			case SUCCESS:
				offset = 7;
				break;
		}

		return offset;
	}
}
