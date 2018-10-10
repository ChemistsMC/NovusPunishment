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
import me.ebonjaeger.novuspunishment.action.Kick;
import me.ebonjaeger.novuspunishment.datasource.MySQL;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.time.Instant;

public class KickCommand extends BaseCommand {

	private NovusPunishment plugin;
	private BukkitService bukkitService;
	private MySQL dataSource;

	@Inject
	KickCommand(NovusPunishment plugin, BukkitService bukkitService, MySQL dataSource) {
		this.plugin = plugin;
		this.bukkitService = bukkitService;
		this.dataSource = dataSource;
	}

	@CommandAlias("kick")
	@CommandPermission("newpunish.command.kick")
	@CommandCompletion("@players")
	public void onCommand(CommandSender sender, OnlinePlayer player, String[] reason) {
		Player target = player.getPlayer();
		String fullReason = String.join(", ", reason);

		if (sender instanceof Player && target.equals(sender)) {
			plugin.sendMessage(sender, Message.ACTION_AGAINST_SELF);
			return;
		}

		if (target.hasPermission("newpunish.bypass.kick")) {
			plugin.sendMessage(sender, Message.KICK_EXEMPT, target.getName());
			return;
		}

		String staff = "console";
		if (sender instanceof Player) {
			staff = ((Player) sender).getUniqueId().toString();
		}

		Instant timestamp = Instant.now();
		Kick kick = new Kick(target.getUniqueId(), staff, timestamp, fullReason);

		bukkitService.runTaskAsync(() -> dataSource.saveKick(kick));

		target.kickPlayer(Utils.formatKickMessage(kick.getReason()));

		plugin.getServer().getOnlinePlayers().stream()
				.filter(onlinePlayer -> hasPermission("newpunish.notify.kick"))
				.forEach(onlinePlayer -> plugin.sendMessage(onlinePlayer, Message.KICK_NOTIFICATION, target.getName(), kick.getReason()));
	}
}
