package me.ebonjaeger.novuspunishment.listener;

import me.ebonjaeger.novuspunishment.BukkitService;
import me.ebonjaeger.novuspunishment.PlayerState;
import me.ebonjaeger.novuspunishment.StateManager;
import me.ebonjaeger.novuspunishment.datasource.MySQL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import javax.inject.Inject;

public class PlayerLoginListener implements Listener {

    private BukkitService bukkitService;
    private MySQL dataSource;
    private StateManager stateManager;

    @Inject PlayerLoginListener(BukkitService bukkitService, MySQL dataSource, StateManager stateManager) {
        this.bukkitService = bukkitService;
        this.dataSource = dataSource;
        this.stateManager = stateManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        // Check if the player is banned. The server should handle this if so.
        if (player.isBanned()) {
            return;
        }

        bukkitService.runTaskAsync(() -> {
            PlayerState state = dataSource.getPlayerState(player.getUniqueId());

            if (state != null) {
                bukkitService.runTask(() -> stateManager.addPlayerState(player.getUniqueId(), state));
            }
        });
    }
}
