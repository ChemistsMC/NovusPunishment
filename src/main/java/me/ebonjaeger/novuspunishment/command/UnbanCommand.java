package me.ebonjaeger.novuspunishment.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import me.ebonjaeger.novuspunishment.Message;
import me.ebonjaeger.novuspunishment.Messenger;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;

public class UnbanCommand extends BaseCommand {

	private Messenger messenger;

	@Inject
	UnbanCommand(Messenger messenger) {
		this.messenger = messenger;
	}

	@CommandAlias("unban|pardon")
	@CommandPermission("newpunish.command.unban")
	public void onCommand(CommandSender sender, String name) {
		if (Bukkit.getBanList(BanList.Type.NAME).isBanned(name)) {
			Bukkit.getBanList(BanList.Type.NAME).pardon(name);

			// Notify players
			messenger.broadcastMessage(Message.PLAYER_UNBANNED, "newpunish.notify.unban", name);
		} else {
			messenger.sendMessage(sender, Message.PLAYER_NOT_BANNED, name);
		}
	}
}
