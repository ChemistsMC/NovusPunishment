package me.ebonjaeger.novuspunishment.action;

import java.time.Instant;
import java.util.UUID;

/**
 * Class representing when a player has been muted.
 * <p>
 * While muted, the player will not be able to type in chat. Muted users
 * being able to use private messaging systems will be configurable.
 */
public class Mute implements TemporaryAction {

    private UUID playerUUID;
    private String staff;
    private Instant timestamp;
    private Instant expires;
    private String reason;

    /**
     * Constructor.
     *
     * @param playerUUID The UUID of the player being muted
     * @param staff      The UUID of the staff muting the player, or 'console'
     * @param timestamp  The {@link Instant} when the mute occurred
     * @param expires    The {@link Instant} that the mute will expire
     * @param reason     The reason given for the mute. May be an empty String
     */
    public Mute(UUID playerUUID, String staff, Instant timestamp, Instant expires, String reason) {
        this.playerUUID = playerUUID;
        this.staff = staff;
        this.timestamp = timestamp;
        this.expires = expires;
        this.reason = reason;
    }

    @Override
    public Instant getExpires() {
        return expires;
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
