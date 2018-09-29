package me.ebonjaeger.novuspunishment.listener;

import me.ebonjaeger.novuspunishment.BukkitService;
import me.ebonjaeger.novuspunishment.NovusPunishment;
import me.ebonjaeger.novuspunishment.PlayerState;
import me.ebonjaeger.novuspunishment.datasource.MySQL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.inject.Inject;

public class PlayerLogoutListener implements Listener {

	private NovusPunishment plugin;
	private BukkitService bukkitService;
	private MySQL dataSource;

	@Inject
	public PlayerLogoutListener(NovusPunishment plugin, BukkitService bukkitService, MySQL dataSource) {
		this.plugin = plugin;
		this.bukkitService = bukkitService;
		this.dataSource = dataSource;
	}

	@EventHandler
	public void onplayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PlayerState state = plugin.getPlayerState(player.getUniqueId());
		if (state != null) {
			if (!plugin.isShuttingDown()) {
				bukkitService.runTaskAsync(() -> dataSource.savePlayerState(state));
			}
		}
	}

	@EventHandler
	public void onplayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		PlayerState state = plugin.getPlayerState(player.getUniqueId());
		if (state != null) {
			if (!plugin.isShuttingDown()) {
				bukkitService.runTaskAsync(() -> dataSource.savePlayerState(state));
			}
		}
	}
}
