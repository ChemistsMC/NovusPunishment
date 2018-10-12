package me.ebonjaeger.novuspunishment.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.contexts.OnlinePlayer;
import me.ebonjaeger.novuspunishment.BukkitService;
import me.ebonjaeger.novuspunishment.Message;
import me.ebonjaeger.novuspunishment.NovusPunishment;
import me.ebonjaeger.novuspunishment.Utils;
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

	private NovusPunishment plugin;
	private BukkitService bukkitService;
	private MySQL dataSource;

	@Inject
	TempbanCommand(NovusPunishment plugin, BukkitService bukkitService, MySQL dataSource) {
		this.plugin = plugin;
		this.bukkitService = bukkitService;
		this.dataSource = dataSource;
	}

	@CommandAlias("tempban|tb")
	@CommandPermission("newpunish.command.tempban")
	@CommandCompletion("@players")
	public void onCommand(CommandSender sender, OnlinePlayer player, String duration, String... reason) {
		Player target = player.getPlayer();
		String _reason = String.join(", ", reason);

		if (sender.getName().equals(target.getName())) {
			plugin.sendMessage(sender, Message.ACTION_AGAINST_SELF);
			return;
		}

		if (Bukkit.getBanList(BanList.Type.NAME).isBanned(target.getName())) {
			plugin.sendMessage(sender, Message.ALREADY_BANNED, target.getName());
			return;
		}

		if (target.hasPermission("newpunish.bypass.tempban")) {
			plugin.sendMessage(sender, Message.BAN_EXEMPT, target.getName());
			return;
		}

		if (!Utils.matchesDurationPattern(duration)) {
			plugin.sendMessage(sender, Message.INVALID_DURATION, duration);
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
		plugin.getServer().getOnlinePlayers().stream()
				.filter(onlinePlayer -> onlinePlayer.hasPermission("newpunish.notify.tempban"))
				.forEach(onlinePlayer -> plugin.sendMessage(onlinePlayer, Message.TEMPBAN_NOTIFICATION, target.getName(), tempban.getReason()));
	}
}
