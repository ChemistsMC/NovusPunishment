package me.ebonjaeger.novuspunishment.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import me.ebonjaeger.novuspunishment.Message;
import me.ebonjaeger.novuspunishment.NovusPunishment;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;

public class UnbanCommand extends BaseCommand {

	private NovusPunishment plugin;

	@Inject
	UnbanCommand(NovusPunishment plugin) {
		this.plugin = plugin;
	}

	@CommandAlias("unban|pardon")
	@CommandPermission("newpunish.command.unban")
	public void onCommand(CommandSender sender, String name) {
		if (Bukkit.getBanList(BanList.Type.NAME).isBanned(name)) {
			Bukkit.getBanList(BanList.Type.NAME).pardon(name);

			// Notify players
			plugin.getServer().getOnlinePlayers().stream()
					.filter(onlinePlayer -> hasPermission("newpunish.notify.unban"))
					.forEach(onlinePlayer -> plugin.sendMessage(onlinePlayer, Message.PLAYER_UNBANNED, name));
		} else {
			plugin.sendMessage(sender, Message.PLAYER_NOT_BANNED, name);
		}
	}
}
