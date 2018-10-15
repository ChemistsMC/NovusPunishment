package me.ebonjaeger.novuspunishment;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

/**
 * Class for sending messages to players (and the console).
 */
public class Messenger {

	private NovusPunishment plugin;

	@Inject
	Messenger(NovusPunishment plugin) {
		this.plugin = plugin;
	}

	/**
	 * Broadcast a message to all players on the server, including the console, if
	 * the player has the given permission node set.
	 *
	 * @param message The {@link Message} to send
	 * @param permission The permission node to filter by
	 * @param replacers Strings to swap in place of any placeholders
	 */
	public void broadcastMessage(Message message, String permission, String... replacers) {
		plugin.getServer().getOnlinePlayers().stream()
				.filter(player -> player.hasPermission(permission))
				.forEach(player -> sendMessage(player, message, replacers));

		sendMessage(plugin.getServer().getConsoleSender(), message, replacers);
	}

	/**
	 * Send a message only to players with a certain permission node set, unless they are the given player.
	 * Useful for commands where the player is already notified by a different message (e.g. warnings and mutes).
	 *
	 * @param message The {@link Message} to send
	 * @param except The {@link Player} who should not receive the message
	 * @param permission The permission node to filter by
	 * @param replacers Strings to swap in place of any placeholders
	 */
	public void broadcastMessageExcept(Message message, Player except, String permission, String... replacers) {
		plugin.getServer().getOnlinePlayers().stream()
				.filter(player -> !player.equals(except))
				.filter(player -> player.hasPermission(permission))
				.forEach(player -> sendMessage(player, message, replacers));

		sendMessage(plugin.getServer().getConsoleSender(), message, replacers);
	}

	/**
	 * Send a message. The recipient may be anything that extends {@link CommandSender}, such as a player,
	 * or the console.
	 *
	 * Strings passed in will replace placeholders in the order they are given, so check
	 * the message to make sure the order makes sense.
	 *
	 * @param receiver The recipient of the message
	 * @param message The {@link Message} to send
	 * @param replacers Strings to swap in place of any placeholders
	 */
	public void sendMessage(CommandSender receiver, Message message, String... replacers) {
		String finalMessage = message.getMessage();
		if (replacers.length > 0) {
			for (int i = 0; i < replacers.length; i++) {
				finalMessage = finalMessage.replace("{" + i + "}", replacers[i]);
			}
		}

		receiver.sendMessage(finalMessage);
	}
}
