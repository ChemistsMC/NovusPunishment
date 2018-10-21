package me.ebonjaeger.novuspunishment;

import me.ebonjaeger.novuspunishment.datasource.MySQL;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StateManager {

    private Map<UUID, PlayerState> playerStates = new HashMap<>();
    private Map<UUID, Integer> playerWarnings = new HashMap<>();

    private NovusPunishment plugin;
    private MySQL dataSource;

    @Inject StateManager(NovusPunishment plugin, MySQL dataSource) {
        this.plugin = plugin;
        this.dataSource = dataSource;
    }

    /**
     * Add a new {@link PlayerState} into memory.
     *
     * @param uniqueID    The player's unique ID
     * @param playerState The player's current state
     */
    public synchronized void addPlayerState(UUID uniqueID, PlayerState playerState) {
        playerStates.put(uniqueID, playerState);
    }

    /**
     * Get a loaded {@link PlayerState} for the given {@link UUID}.
     * Will return {@code null} if no state for the player is currently
     * in memory.
     *
     * @param uniqueID The player's unique ID
     * @return The player's state, or null
     */
    public synchronized PlayerState getPlayerState(UUID uniqueID) {
        return playerStates.get(uniqueID);
    }

    /**
     * Get an already created {@link PlayerState} for
     * a player if one exists, or create a new one.
     * The newly created state will not be added to the cache.
     *
     * @param player The {@link Player} to get the state for
     * @return The current state data for the player
     */
    public synchronized PlayerState getOrCreateState(Player player) {
        return playerStates.getOrDefault(
            player.getUniqueId(),
            new PlayerState(player.getUniqueId(), player.getName(), false)
        );
    }

    /**
     * Remove a {@link PlayerState} from memory, e.g. when a player
     * is leaving the server.
     *
     * @param uniqueID The unique ID of the player
     */
    public synchronized void removePlayerState(UUID uniqueID) {
        playerStates.remove(uniqueID);
    }

    /**
     * Increment the number of warnings a player has had this session.
     *
     * @param uuid The player's unique id
     */
    public void incrementWarnings(UUID uuid) {
        int count = getWarnings(uuid);

        playerWarnings.put(uuid, ++count);
    }

    /**
     * Get how many warnings a player has had this session.
     *
     * @param uuid The player's unique id
     * @return Their number of warnings
     */
    public int getWarnings(UUID uuid) {
        return playerWarnings.getOrDefault(uuid, 0);
    }

    /**
     * Flush all active player states in memory to the disk.
     * This method can only be called when the plugin is being disabled.
     *
     * @throws IllegalStateException When the plugin is not being disabled when the method is called
     */
    void flushStates() {
        if (!plugin.isShuttingDown()) {
            throw new IllegalStateException("StateManager::flushStates() can only be called when the plugin is shutting down");
        }

        // Save all player states in memory to the disk
        for (PlayerState state : playerStates.values()) {
            dataSource.savePlayerState(state);
        }

        playerStates.clear();
        playerWarnings.clear();
    }
}
