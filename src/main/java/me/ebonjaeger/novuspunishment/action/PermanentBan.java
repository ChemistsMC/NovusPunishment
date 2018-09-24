package me.ebonjaeger.novuspunishment.action;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a permanent ban from the server.
 *
 * The player will not be able to rejoin the server
 * unless they are unbanned.
 */
public class PermanentBan implements Action {

	private UUID playerUUID;
	private UUID staffUUID;
	private Instant timestamp;
	private String reason;

	/**
	 * Constructor.
	 *
	 * @param playerUUID The UUID of the player being banned
	 * @param staffUUID The UUID of the staff banning the player
	 * @param timestamp The {@link Instant} when the ban occurred
	 * @param reason The reason given for the ban
	 */
	public PermanentBan(UUID playerUUID, UUID staffUUID, Instant timestamp, String reason) {
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
