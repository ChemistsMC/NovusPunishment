package me.ebonjaeger.novuspunishment.action;

import java.time.Instant;
import java.util.UUID;

/**
 * Class representing a player kick.
 */
public class Kick implements Action {

    private UUID playerUUID;
    private String staff;
    private Instant timestamp;
    private String reason;

    /**
     * Constructor.
     *
     * @param playerUUID The UUID of the player being kicked
     * @param staff      The UUID of the staff kicking the player, or 'console'
     * @param timestamp  The {@link Instant} when the kick occurred
     * @param reason     The reason given for the kick
     */
    public Kick(UUID playerUUID, String staff, Instant timestamp, String reason) {
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
