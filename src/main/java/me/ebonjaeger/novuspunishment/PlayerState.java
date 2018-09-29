package me.ebonjaeger.novuspunishment;

import java.util.UUID;

/**
 * Class to represent some of the state of a player.
 */
public class PlayerState {

	private UUID uniqueID;
	private String userName;
	private boolean isMuted;
	private boolean isBanned;

	/**
	 * Constructor.
	 *
	 * @param uniqueID The {@link UUID} of the player
	 * @param userName The player's current user name
	 * @param isMuted If the player is currently muted
	 * @param isBanned If the player is currently banned
	 */
	public PlayerState(UUID uniqueID, String userName, boolean isMuted, boolean isBanned) {
		this.uniqueID = uniqueID;
		this.userName = userName;
		this.isMuted = isMuted;
		this.isBanned = isBanned;
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
	 * Get if the player is currently banned, and thus unable to
	 * join the server.
	 *
	 * @return True if the player is banned
	 */
	public boolean isBanned() {
		return isBanned;
	}

	/**
	 * Set if the player is banned.
	 *
	 * @param banned True if the player should now be banned
	 */
	public void setBanned(boolean banned) {
		isBanned = banned;
	}
}
