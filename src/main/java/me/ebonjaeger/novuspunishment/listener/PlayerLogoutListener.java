package me.ebonjaeger.novuspunishment.listener;

import me.ebonjaeger.novuspunishment.BukkitService;
import me.ebonjaeger.novuspunishment.NovusPunishment;
import me.ebonjaeger.novuspunishment.PlayerState;
import me.ebonjaeger.novuspunishment.StateManager;
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
    private StateManager stateManager;

    @Inject PlayerLogoutListener(NovusPunishment plugin, BukkitService bukkitService, MySQL dataSource, StateManager stateManager) {
        this.plugin = plugin;
        this.bukkitService = bukkitService;
        this.dataSource = dataSource;
        this.stateManager = stateManager;
    }

    @EventHandler
    public void onplayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerState state = stateManager.getPlayerState(player.getUniqueId());
        if (state != null) {
            if (!plugin.isShuttingDown()) {
                bukkitService.runTaskAsync(() -> dataSource.savePlayerState(state));
                stateManager.removePlayerState(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onplayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        PlayerState state = stateManager.getPlayerState(player.getUniqueId());
        if (state != null) {
            if (!plugin.isShuttingDown()) {
                bukkitService.runTaskAsync(() -> dataSource.savePlayerState(state));
                stateManager.removePlayerState(player.getUniqueId());
            }
        }
    }
}
