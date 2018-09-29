package me.ebonjaeger.novuspunishment;

import org.bukkit.ChatColor;

import java.time.Duration;

public class Utils {

	public static String formatTempbanMessage(String reason, Duration duration) {
		return String.format(ChatColor.GRAY + "You have been temporarily banned!\n " +
				ChatColor.GRAY + "For another: " + ChatColor.WHITE + "%s\n " +
				ChatColor.GRAY + "Reason: " + ChatColor.RED + "%s", formatDuration(duration), reason);
	}

	private static String formatDuration(Duration duration) {
		long s = duration.getSeconds();

		return String.format("%d Days, %d Hours, %02d Minutes and %02d Seconds", s/86400, s/3600, (s%3600)/60, (s%60));
	}
}
