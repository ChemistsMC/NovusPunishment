package me.ebonjaeger.novuspunishment.listener;

import me.ebonjaeger.novuspunishment.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import javax.inject.Inject;
import java.time.Instant;

public class PlayerChatListener implements Listener {

	private NovusPunishment plugin;
	private BukkitService bukkitService;
	private StateManager stateManager;

	@Inject
	PlayerChatListener(NovusPunishment plugin, BukkitService bukkitService, StateManager stateManager) {
		this.plugin = plugin;
		this.bukkitService = bukkitService;
		this.stateManager = stateManager;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerAsyncChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Player player = event.getPlayer();

		bukkitService.runTask(() -> {
			// Check if the player is currently muted
			PlayerState state = stateManager.getPlayerState(player.getUniqueId());
			if (state == null || !state.isMuted()) {
				return;
			}

			if (state.getUntil().isAfter(Instant.now())) {
				event.setCancelled(true);
				plugin.sendMessage(player, Message.CHAT_WHILE_MUTED);
			} else {
				state.setMuted(false);
				state.setUntil(null);
			}
		});
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Player player = event.getPlayer();
		PlayerState state = stateManager.getPlayerState(player.getUniqueId());

		if (state == null || !state.isMuted()) {
			return;
		}

		if (state.getUntil().isAfter(Instant.now())) {
			event.setCancelled(true);
			plugin.sendMessage(player, Message.CHAT_WHILE_MUTED);
		} else {
			state.setMuted(false);
			state.setUntil(null);
		}
	}
}
