package me.ebonjaeger.novuspunishment.action;

import java.time.Instant;
import java.util.UUID;

/**
 * Class representing when a player has been muted.
 *
 * While muted, the player will not be able to type in chat. Muted users
 * being able to use private messaging systems will be configurable.
 */
public class Mute implements TemporaryAction {

	private UUID playerUUID;
	private UUID staffUUID;
	private Instant timestamp;
	private Instant expires;
	private String reason;

	/**
	 * Constructor.
	 *
	 * @param playerUUID The UUID of the player being muted
	 * @param staffUUID The UUID of the staff muting the player
	 * @param timestamp The {@link Instant} when the mute occurred
	 * @param expires The {@link Instant} that the mute will expire
	 * @param reason The reason given for the mute. May be an empty String
	 */
	public Mute(UUID playerUUID, UUID staffUUID, Instant timestamp, Instant expires, String reason) {
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
