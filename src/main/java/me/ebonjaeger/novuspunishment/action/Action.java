package me.ebonjaeger.novuspunishment.action;

import java.time.Instant;
import java.util.UUID;

/**
 * Generic action that can be taken against a {@link org.bukkit.entity.Player}.
 */
public interface Action {

	/**
	 * Get the unique ID of the player the action is against.
	 *
	 * @return The player's UUID
	 */
	UUID getPlayerUUID();

	/**
	 * Get the unique ID of the player taking the action, or 'console'
	 * if the action was performed from the server console.
	 *
	 * @return The staff member's UUID, or console
	 */
	String getStaff();

	/**
	 * Get the time when the action was taken.
	 *
	 * @return The {@link Instant} the action occurred at
	 */
	Instant getTimestamp();

	/**
	 * Get the reason that the action was taken.
	 *
	 * @return The reason for the action
	 */
	String getReason();
}
