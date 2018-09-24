package me.ebonjaeger.novuspunishment.action;

import java.time.Instant;
import java.util.UUID;

/**
 * Class representing a temporary ban from the server.
 *
 * The player will be able to rejoin the server after the ban
 * has expired.
 */
public class TemporaryBan implements TemporaryAction {

	private UUID playerUUID;
	private UUID staffUUID;
	private Instant timestamp;
	private Instant expires;
	private String reason;

	/**
	 * Constructor.
	 *
	 * @param playerUUID The UUID of the player being banned
	 * @param staffUUID The UUID of the staff banning the player
	 * @param timestamp The {@link Instant} when the ban occurred
	 * @param expires The {@link Instant} that the ban will expire
	 * @param reason The reason given for the ban
	 */
	public TemporaryBan(UUID playerUUID, UUID staffUUID, Instant timestamp, Instant expires, String reason) {
		this.playerUUID = playerUUID;
		this.staffUUID = staffUUID;
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
