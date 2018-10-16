package me.ebonjaeger.novuspunishment;

import org.bukkit.ChatColor;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.regex.Pattern;

public class Utils {

	private final static Pattern DURATION_PATTERN = Pattern.compile("^\\d*[smdhSMDH]$");
	private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
			.withLocale(Locale.getDefault())
			.withZone(ZoneId.systemDefault());

	public static String formatKickMessage(String reason) {
		return String.format(ChatColor.GRAY + "You have been kicked from the server!\n" +
		ChatColor.GRAY + "Reason: " + ChatColor.RED + "%s", reason);
	}

	public static String formatTempbanMessage(String reason, Duration duration) {
		return String.format(ChatColor.GRAY + "You have been temporarily banned from the server!\n" +
				ChatColor.GRAY + "Duration: " + ChatColor.WHITE + "%s\n" +
				ChatColor.GRAY + "Reason: " + ChatColor.RED + "%s", formatDuration(duration), reason);
	}

	public static String formatBanMessage(String reason) {
		return String.format(ChatColor.GRAY + "You have been permanently banned from the server!\n" +
		ChatColor.GRAY + "Reason: " + ChatColor.RED + "%s", reason);
	}

	/**
	 * Parse a user-given String to add a unit of time to a given {@link Instant}.
	 * It is assumed that the argument passed in will be a number followed by one letter.
	 *
	 * @see Utils#matchesDurationPattern(String) for validating input.
	 *
	 * @param arg The string to parse the duration from
	 * @param from The instant in time to add to
	 * @return An instant in the future that is the given distance from the given time
	 */
	public static Instant addDuration(String arg, Instant from) {
		arg = arg.toUpperCase();

		// Parse the number given
		long difference = Long.parseLong(arg.substring(0, arg.length() - 1));
		char unit = arg.charAt(arg.length() - 1);

		switch (unit) {
			case 'S': return from.plus(difference, ChronoUnit.SECONDS);
			case 'M': return from.plus(difference, ChronoUnit.MINUTES);
			case 'H': return from.plus(difference, ChronoUnit.HOURS);
			case 'D': return from.plus(difference, ChronoUnit.DAYS);
			default: throw new IllegalArgumentException("Unknown unit identifier '" + unit + "'");
		}
	}

	/**
	 * Test if a given String matches the pattern for a time duration.
	 *
	 * @see Utils#DURATION_PATTERN for the pattern to match.
	 *
	 * @param arg The String to test
	 * @return True if it is a complete match
	 */
	public static boolean matchesDurationPattern(String arg) {
		return DURATION_PATTERN.matcher(arg).matches();
	}

	/**
	 * Format a given {@link Instant} using the system's
	 * locale and timezone settings.
	 *
	 * @param time The time to format
	 * @return Time formatted as a String
	 */
	public static String formatTime(Instant time) {
		return FORMATTER.format(time);
	}

	/**
	 * Format a duration of time using the default system locale.
	 *
	 * @param duration The {@link Duration} to format
	 * @return The duration formatted as a readable String
	 */
	public static String formatDuration(Duration duration) {
		long s = duration.getSeconds();

		return String.format("%d Days, %d Hours, %02d Minutes and %02d Seconds", s/86400, s/3600, (s%3600)/60, (s%60));
	}
}
