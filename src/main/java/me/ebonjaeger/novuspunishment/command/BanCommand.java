package me.ebonjaeger.novuspunishment.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import me.ebonjaeger.novuspunishment.BukkitService;
import me.ebonjaeger.novuspunishment.Message;
import me.ebonjaeger.novuspunishment.NovusPunishment;
import me.ebonjaeger.novuspunishment.Utils;
import me.ebonjaeger.novuspunishment.action.PermanentBan;
import me.ebonjaeger.novuspunishment.datasource.MySQL;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.time.Instant;

public class BanCommand extends BaseCommand {

	private NovusPunishment plugin;
	private BukkitService bukkitService;
	private MySQL dataSource;

	@Inject
	BanCommand(NovusPunishment plugin, BukkitService bukkitService, MySQL dataSource) {
		this.plugin = plugin;
		this.bukkitService = bukkitService;
		this.dataSource = dataSource;
	}

	@CommandAlias("ban")
	@CommandPermission("newpunish.command.ban")
	@CommandCompletion("@players")
	public void onCommand(CommandSender sender, String name, String... reason) {
		// Attempt to get the target player
		bukkitService.runTaskAsync(() -> {
			OfflinePlayer target = bukkitService.matchPlayer(name, true);

			bukkitService.runTask(() -> {
				if (target == null) {
					plugin.sendMessage(sender, Message.UNKNOWN_PLAYER, name);
					return;
				}

				if (Bukkit.getBanList(BanList.Type.NAME).isBanned(target.getName())) {
					plugin.sendMessage(sender, Message.ALREADY_BANNED, target.getName());
					return;
				}

				if (sender.getName().equals(target.getName())) {
					plugin.sendMessage(sender, Message.ACTION_AGAINST_SELF);
					return;
				}

				if (bukkitService.hasPermission(target, "newpunish.bypass.ban")) {
					plugin.sendMessage(sender, Message.BAN_EXEMPT, target.getName());
					return;
				}

				String staff = "console";
				if (sender instanceof Player) {
					staff = ((Player) sender).getUniqueId().toString();
				}

				String _reason = String.join(", ", reason);
				Instant timestamp = Instant.now();
				PermanentBan ban = new PermanentBan(target.getUniqueId(), staff, timestamp, _reason);

				bukkitService.runTaskAsync(() -> dataSource.saveBan(ban));

				// Add ban entry and kick from the server if online
				Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), _reason, null, sender.getName());
				if (target.isOnline()) {
					target.getPlayer().kickPlayer(Utils.formatBanMessage(ban.getReason()));
				}

				// Notify players
				plugin.getServer().getOnlinePlayers().stream()
						.filter(onlinePlayer -> hasPermission("newpunish.notify.ban"))
						.forEach(onlinePlayer -> plugin.sendMessage(onlinePlayer, Message.BAN_NOTIFICATION, target.getName(), ban.getReason()));
			});
		});
	}
}
