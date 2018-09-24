package me.ebonjaeger.novuspunishment.action;

import java.time.Instant;
import java.util.UUID;

/**
 * Class representing a player kick.
 */
public class Kick implements Action {

	private UUID playerUUID;
	private UUID staffUUID;
	private Instant timestamp;
	private String reason;

	/**
	 * Constructor.
	 *
	 * @param playerUUID The UUID of the player being kicked
	 * @param staffUUID The UUID of the staff kicking the player
	 * @param timestamp The {@link Instant} when the kick occurred
	 * @param reason The reason given for the kick
	 */
	public Kick(UUID playerUUID, UUID staffUUID, Instant timestamp, String reason) {
		this.playerUUID = playerUUID;
		this.staffUUID = staffUUID;
		this.timestamp = timestamp;
		this.reason = reason;
	}

	@Override
	public UUID getPlayerUUID() {
		return playerUUID;
	}

	@Override
	public UUID getStaffUUID() {
		return staffUUID;
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
