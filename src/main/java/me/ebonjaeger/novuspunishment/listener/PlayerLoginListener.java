package me.ebonjaeger.novuspunishment.listener;

import me.ebonjaeger.novuspunishment.BukkitService;
import me.ebonjaeger.novuspunishment.NovusPunishment;
import me.ebonjaeger.novuspunishment.PlayerState;
import me.ebonjaeger.novuspunishment.Utils;
import me.ebonjaeger.novuspunishment.action.TemporaryBan;
import me.ebonjaeger.novuspunishment.datasource.MySQL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;

public class PlayerLoginListener implements Listener {

	private NovusPunishment plugin;
	private BukkitService bukkitService;
	private MySQL dataSource;

	@Inject
	public PlayerLoginListener(NovusPunishment plugin, BukkitService bukkitService, MySQL dataSource) {
		this.plugin = plugin;
		this.bukkitService = bukkitService;
		this.dataSource = dataSource;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();

		// Check if the player is perm-banned. The server should handle this if so.
		if (player.isBanned()) {
			return;
		}

		// Check if the player has an active tempban
		bukkitService.runTaskAsync(() -> {
			TemporaryBan tempban = dataSource.getActiveTempban(player.getUniqueId());

			if (tempban != null) {
				// Player is currently tempbanned from the server
				bukkitService.runTask(() -> {
					Duration duration = Duration.between(Instant.now(), tempban.getExpires());
					event.disallow(PlayerLoginEvent.Result.KICK_BANNED, Utils.formatTempbanMessage(tempban.getReason(), duration));
				});
			} else {
				// No active tempban, so load their state if there is any
				PlayerState state = dataSource.getPlayerState(player.getUniqueId());

				if (state != null) {
					bukkitService.runTask(() -> {
						state.setBanned(false);
						plugin.addPlayerState(player.getUniqueId(), state);

						// TODO: Handle if the player is supposed to be muted
					});
				}
			}
		});
	}
}
