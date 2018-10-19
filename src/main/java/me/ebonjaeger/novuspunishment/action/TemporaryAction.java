package me.ebonjaeger.novuspunishment.action;

import java.time.Instant;

/**
 * Classes implementing this interface are temporary actions
 * against a player, e.g. a temporary ban.
 * <p>
 * As such, these classes have an expiration time.
 */
public interface TemporaryAction extends Action {

    /**
     * Get the time when the action expires.
     *
     * @return The {@link Instant} when the action expires.
     */
    Instant getExpires();
}
