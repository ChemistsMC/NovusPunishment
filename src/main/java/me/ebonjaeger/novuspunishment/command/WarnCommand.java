package me.ebonjaeger.novuspunishment.command;

import ch.jalu.configme.SettingsManager;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.contexts.OnlinePlayer;
import me.ebonjaeger.novuspunishment.BukkitService;
import me.ebonjaeger.novuspunishment.Message;
import me.ebonjaeger.novuspunishment.NovusPunishment;
import me.ebonjaeger.novuspunishment.Utils;
import me.ebonjaeger.novuspunishment.action.Warning;
import me.ebonjaeger.novuspunishment.configuration.ActionSettings;
import me.ebonjaeger.novuspunishment.datasource.MySQL;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.time.Instant;

public class WarnCommand extends BaseCommand {

	private NovusPunishment plugin;
	private BukkitService bukkitService;
	private MySQL dataSource;
	private SettingsManager settings;

	@Inject
	public WarnCommand(NovusPunishment plugin, BukkitService bukkitService, MySQL dataSource, SettingsManager settings) {
		this.plugin = plugin;
		this.bukkitService = bukkitService;
		this.dataSource = dataSource;
		this.settings = settings;
	}

	@CommandAlias("warn")
	@CommandPermission("newpunish.command.warn")
	@CommandCompletion("@players")
	public void onCommand(CommandSender sender, OnlinePlayer player, String[] reason) {
		Player target = player.getPlayer();
		String fullReason = String.join(", ", reason);

		if (sender instanceof Player && target.equals(sender)) {
			plugin.sendMessage(sender, Message.ACTION_AGAINST_SELF);
			return;
		}

		if (target.hasPermission("newpunish.bypass.warn")) {
			plugin.sendMessage(sender, Message.WARN_EXEMPT, target.getName());
			return;
		}

		String staff = "console";
		if (sender instanceof Player) {
			staff = ((Player) sender).getUniqueId().toString();
		}

		Instant timestamp = Instant.now();
		Warning warning = new Warning(target.getUniqueId(), staff, timestamp, fullReason);

		bukkitService.runTaskAsync(() -> dataSource.saveWarning(warning));

		plugin.incrementWarnings(target.getUniqueId());
		int sessionCount = plugin.getWarnings(target.getUniqueId());
		int warnLimit = settings.getProperty(ActionSettings.WARNS_UNTIL_KICK);

		if (warnLimit > 0 && sessionCount % warnLimit == 0) {
			// TODO: Make it clear they were warned too many times
			target.kickPlayer(Utils.formatKickMessage(warning.getReason()));
		} else {
			plugin.sendMessage(target, Message.WARN_PLAYER, warning.getReason());
		}

		for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
			if (!onlinePlayer.equals(target) && onlinePlayer.hasPermission("newpunish.notify.warn")) {
				plugin.sendMessage(onlinePlayer, Message.WARN_NOTIFICATION, target.getName(), warning.getReason());
			}
		}
	}
}
