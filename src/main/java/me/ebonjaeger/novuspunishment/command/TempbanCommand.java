package me.ebonjaeger.novuspunishment.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.contexts.OnlinePlayer;
import me.ebonjaeger.novuspunishment.*;
import me.ebonjaeger.novuspunishment.action.TemporaryBan;
import me.ebonjaeger.novuspunishment.datasource.MySQL;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class TempbanCommand extends BaseCommand {

	private BukkitService bukkitService;
	private Messenger messenger;
	private MySQL dataSource;

	@Inject
	TempbanCommand(BukkitService bukkitService, Messenger messenger, MySQL dataSource) {
		this.bukkitService = bukkitService;
		this.messenger = messenger;
		this.dataSource = dataSource;
	}

	@CommandAlias("tempban|tb")
	@CommandPermission("newpunish.command.tempban")
	@CommandCompletion("@players")
	public void onCommand(CommandSender sender, OnlinePlayer player, String duration, String... reason) {
		Player target = player.getPlayer();
		String _reason = String.join(", ", reason);

		if (sender.getName().equals(target.getName())) {
			messenger.sendMessage(sender, Message.ACTION_AGAINST_SELF);
			return;
		}

		if (Bukkit.getBanList(BanList.Type.NAME).isBanned(target.getName())) {
			messenger.sendMessage(sender, Message.ALREADY_BANNED, target.getName());
			return;
		}

		if (target.hasPermission("newpunish.bypass.tempban")) {
			messenger.sendMessage(sender, Message.BAN_EXEMPT, target.getName());
			return;
		}

		if (!Utils.matchesDurationPattern(duration)) {
			messenger.sendMessage(sender, Message.INVALID_DURATION, duration);
			return;
		}

		String staff = "console";
		if (sender instanceof Player) {
			staff = ((Player) sender).getUniqueId().toString();
		}

		Instant timestamp = Instant.now();
		Instant expires = Utils.addDuration(duration, timestamp);

		// Save the tempban to the database
		TemporaryBan tempban = new TemporaryBan(target.getUniqueId(), staff, timestamp, expires, _reason);
		bukkitService.runTaskAsync(() -> dataSource.saveTempban(tempban));

		// Add the entry to the server's banlist
		Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), tempban.getReason(), Date.from(expires), sender.getName());
		target.kickPlayer(Utils.formatTempbanMessage(tempban.getReason(), Duration.between(timestamp, expires)));

		// Notify players
		messenger.broadcastMessage(Message.TEMPBAN_NOTIFICATION, "newpunish.notify.tempban", target.getName(), tempban.getReason());
	}
}
