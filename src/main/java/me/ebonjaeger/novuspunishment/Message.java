package me.ebonjaeger.novuspunishment;

import org.bukkit.ChatColor;

public enum Message {

	KICK_EXEMPT(Prefix.ERROR, "{0} cannot be kicked from the server!"),
	KICK_NOTIFICATION(Prefix.INFO, "{0} has been kicked from the server for: " + ChatColor.WHITE + "{1}");

	private Prefix prefix;
	private String message;

	Message(Prefix prefix, String message) {
		this.prefix = prefix;
		this.message = message;
	}

	public String getMessage() {
		return prefix.getPrefix() + message;
	}

	private enum Prefix {
		INFO(ChatColor.BLUE + "» " + ChatColor.GRAY),
		SUCCESS(ChatColor.GREEN + "» " + ChatColor.GRAY),
		ERROR(ChatColor.RED + "» " + ChatColor.GRAY);

		private String prefix;

		Prefix(String prefix) {
			this.prefix = prefix;
		}

		public String getPrefix() {
			return prefix;
		}
	}
}
