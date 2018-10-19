package me.ebonjaeger.novuspunishment.action;

import java.time.Instant;
import java.util.UUID;

/**
 * Class representing a warning to the player.
 * <p>
 * A warning does not limit the player's ability to play the game in any way.
 * The reason is shown to the player.
 */
public class Warning implements Action {

    private UUID playerUUID;
    private String staff;
    private Instant timestamp;
    private String reason;

    /**
     * Constructor.
     *
     * @param playerUUID The UUID of the player being warned
     * @param staff      The UUID of the staff warning the player
     * @param timestamp  The {@link Instant} when the warning occurred
     * @param reason     The reason given for the warning
     */
    public Warning(UUID playerUUID, String staff, Instant timestamp, String reason) {
        this.playerUUID = playerUUID;
        this.staff = staff;
        this.timestamp = timestamp;
        this.reason = reason;
    }

    @Override
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    @Override
    public String getStaff() {
        return staff;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String getReason() {
        return reason;
    }
}
