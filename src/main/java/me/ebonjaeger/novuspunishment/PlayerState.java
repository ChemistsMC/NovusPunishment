package me.ebonjaeger.novuspunishment;

import java.time.Instant;
import java.util.UUID;

/**
 * Class to represent some of the state of a player.
 */
public class PlayerState {

	private UUID uniqueID;
	private String userName;
	private boolean isMuted;
	private Instant until;

	/**
	 * Constructor.
	 *
	 * @param uniqueID The {@link UUID} of the player
	 * @param userName The player's current user name
	 * @param isMuted If the player is currently muted
	 */
	public PlayerState(UUID uniqueID, String userName, boolean isMuted) {
		this.uniqueID = uniqueID;
		this.userName = userName;
		this.isMuted = isMuted;
		this.until = null;
	}

	/**
	 * Constructor.
	 *
	 * @param uniqueID The {@link UUID} of the player
	 * @param userName The player's current user name
	 * @param isMuted If the player is currently muted
	 * @param until Time when the player should no longer be muted
	 */
	public PlayerState(UUID uniqueID, String userName, boolean isMuted, Instant until) {
		this.uniqueID = uniqueID;
		this.userName = userName;
		this.isMuted = isMuted;
		this.until = until;
	}

	/**
	 * Get the {@link UUID} of the player.
	 *
	 * @return The player's unique ID
	 */
	public UUID getUniqueID() {
		return uniqueID;
	}

	/**
	 * Get the player's current user name.
	 *
	 * @return The player's current user name
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Set the user name of the player, if they have changed it since
	 * they last joined the server.
	 *
	 * @param userName The player's new user name
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Get if the player is currently muted.
	 *
	 * @return True if the player is muted
	 */
	public boolean isMuted() {
		return isMuted;
	}

	/**
	 * Set if the player is muted.
	 *
	 * @param muted True if the player should now be muted
	 */
	public void setMuted(boolean muted) {
		isMuted = muted;
	}

	/**
	 * Get the time when the player should no longer be muted.
	 * Will be {@code null} if the player is not currently muted.
	 *
	 * @return {@link Instant} when the player is unmuted, or {@code null}
	 */
	public Instant getUntil() {
		return until;
	}

	/**
	 * Set when the player should be unmuted.
	 *
	 * @param until New time when the player should be unmuted
	 */
	public void setUntil(Instant until) {
		this.until = until;
	}
}
