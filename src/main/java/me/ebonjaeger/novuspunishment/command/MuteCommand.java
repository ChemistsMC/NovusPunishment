package me.ebonjaeger.novuspunishment.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.contexts.OnlinePlayer;
import me.ebonjaeger.novuspunishment.*;
import me.ebonjaeger.novuspunishment.action.Mute;
import me.ebonjaeger.novuspunishment.datasource.MySQL;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.time.Instant;

public class MuteCommand extends BaseCommand {

	private NovusPunishment plugin;
	private BukkitService bukkitService;
	private MySQL dataSource;
	private StateManager stateManager;

	@Inject
	MuteCommand(NovusPunishment plugin, BukkitService bukkitService, MySQL dataSource, StateManager stateManager) {
		this.plugin = plugin;
		this.bukkitService = bukkitService;
		this.dataSource = dataSource;
		this.stateManager = stateManager;
	}

	@CommandAlias("mute|unmute|togglemute")
	@CommandPermission("newpunish.command.mute")
	@CommandCompletion("@players")
	public void onCommand(CommandSender sender, OnlinePlayer player, @Optional String duration, @Optional String... reason) {
		Player target = player.getPlayer();
		String _reason = "";
		if (reason != null) {
			_reason = String.join(", ", reason);
		}

		if (sender.getName().equals(target.getName())) {
			plugin.sendMessage(sender, Message.ACTION_AGAINST_SELF);
			return;
		}

		if (target.hasPermission("newpunish.bypass.mute")) {
			plugin.sendMessage(sender, Message.MUTE_EXEMPT, target.getName());
			return;
		}

		// Handle when the target player is already muted
		if (stateManager.getPlayerState(target.getUniqueId()) != null) {
			PlayerState state = stateManager.getPlayerState(target.getUniqueId());
			if (state.isMuted()) {
				state.setMuted(false);
				state.setUntil(null);
				plugin.sendMessage(sender, Message.UNMUTE_SUCCESS, target.getName());
				plugin.sendMessage(target, Message.UNMUTE_PLAYER);
			}
		}

		String staff = "console";
		if (sender instanceof Player) {
			staff = ((Player) sender).getUniqueId().toString();
		}

		Instant timestamp = Instant.now();
		Instant expires = Instant.MAX;

		// Parse and add duration if it exists
		if (duration != null) {
			if (Utils.matchesDurationPattern(duration)) {
				expires = Utils.addDuration(duration, timestamp);
			} else {
				plugin.sendMessage(sender, Message.INVALID_DURATION, duration);
				return;
			}
		}

		Mute mute = new Mute(target.getUniqueId(), staff, timestamp, expires, _reason);
		bukkitService.runTaskAsync(() -> dataSource.saveMute(mute));

		PlayerState state = stateManager.getOrCreateState(target);
		state.setMuted(true);
		state.setUntil(mute.getExpires());
		stateManager.addPlayerState(target.getUniqueId(), state);

		notifyPlayers(target, mute);
	}

	private void notifyPlayers(Player target, Mute mute) {
		String duration;
		if (mute.getExpires().equals(Instant.MAX)) {
			duration = "forever";
		} else {
			duration = Utils.formatTime(mute.getExpires());
		}

		plugin.sendMessage(target, Message.MUTE_PLAYER, duration, mute.getReason());

		plugin.getServer().getOnlinePlayers().stream()
				.filter(player -> !player.equals(target))
				.filter(player -> player.hasPermission("newpunish.notify.mute"))
				.forEach(player -> plugin.sendMessage(player, Message.MUTE_NOTIFICATION, target.getName(), duration, mute.getReason()));
	}
}
