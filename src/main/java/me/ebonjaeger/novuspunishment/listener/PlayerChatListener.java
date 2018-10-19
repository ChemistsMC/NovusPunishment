package me.ebonjaeger.novuspunishment.listener;

import me.ebonjaeger.novuspunishment.*;
import me.ebonjaeger.novuspunishment.configuration.ActionSettings;
import me.ebonjaeger.novuspunishment.configuration.SettingsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;

public class PlayerChatListener implements Listener {

    private Messenger messenger;
    private StateManager stateManager;

    private List<String> disallowedCommands;

    @Inject PlayerChatListener(Messenger messenger, SettingsManager settings, StateManager stateManager) {
        this.messenger = messenger;
        this.stateManager = stateManager;

        this.disallowedCommands = settings.getProperty(ActionSettings.DISALLOWED_COMMANDS);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAsyncChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        // Check if the player is currently muted
        PlayerState state = stateManager.getPlayerState(player.getUniqueId());
        if (state == null || !state.isMuted()) {
            return;
        }

        if (state.getUntil().isAfter(Instant.now())) {
            event.setCancelled(true);
            messenger.sendMessage(player, Message.CHAT_WHILE_MUTED);
        } else {
            state.setMuted(false);
            state.setUntil(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }

        // Get the actual command; we don't care about any command arguments
        String command = event.getMessage().substring(1).split(" ")[0];

        // If the command is not in the configured list of disallowed commands, exit
        if (!disallowedCommands.contains(command)) {
            return;
        }

        Player player = event.getPlayer();
        PlayerState state = stateManager.getPlayerState(player.getUniqueId());

        if (state == null || !state.isMuted()) {
            return;
        }

        if (state.getUntil().isAfter(Instant.now())) {
            event.setCancelled(true);
            messenger.sendMessage(player, Message.CHAT_WHILE_MUTED);
        } else {
            state.setMuted(false);
            state.setUntil(null);
        }
    }
}
