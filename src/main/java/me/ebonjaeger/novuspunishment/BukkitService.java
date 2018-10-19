package me.ebonjaeger.novuspunishment;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitTask;

import javax.inject.Inject;
import java.util.List;

/**
 * Service for scheduling things with the Bukkit API.
 */
public class BukkitService {

    private final NovusPunishment plugin;

    private Permission permission;

    @Inject BukkitService(NovusPunishment plugin) {
        this.plugin = plugin;

        // Hook into Vault if it's present
        if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
            ConsoleLogger.info("Vault found! Hooking into it...");
            RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
            if (rsp != null) {
                this.permission = rsp.getProvider();
                ConsoleLogger.info("Hooked into Vault!");
            } else {
                ConsoleLogger.warning("Unable to hook into Vault!");
            }
        }
    }

    /**
     * Runs a task on the next server tick and returns the task.
     *
     * @param task The task to run.
     * @return A BukkitTask with the ID number.
     */
    public BukkitTask runTask(Runnable task) {
        return Bukkit.getScheduler().runTask(plugin, task);
    }

    /**
     * Runs a task to be run asynchronously.
     *
     * @param task The task to run.
     * @return A BukkitTask with the ID number.
     */
    public BukkitTask runTaskAsync(Runnable task) {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    /**
     * Attempt to get a player using a (possibly partial) name.
     * Optionally, we can check offline players as well, though
     * be aware that this might involve a blocking web request.
     * This method will return null of no matching player is found.
     * <p>
     * {@see Bukkit#getOfflinePlayer(String)}
     *
     * @param name       The (partial) name of the player to match
     * @param getOffline True if we should look at offline players
     * @return The matched {@link OfflinePlayer} if found, or null
     */
    @SuppressWarnings("deprecation")
    public OfflinePlayer matchPlayer(String name, boolean getOffline) {
        // Check the online players first
        List<Player> matches = Bukkit.getServer().matchPlayer(name);

        if (matches.size() == 1) {
            // Only one match for this name, so return it
            return matches.get(0);
        }

        // None of the online players matched, so lets try offline players
        if (getOffline) {
            if (Bukkit.getOfflinePlayer(name) != null) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(name);
                if (player.hasPlayedBefore()) {
                    return player;
                }
            }
        }

        return null;
    }

    /**
     * Check if Vault is hooked into.
     *
     * @return True if Vault is present
     */
    public boolean isVaultPresent() {
        return permission != null;
    }

    /**
     * Check if an {@link OfflinePlayer} has a certain permission.
     *
     * @param player The player to get permissions for
     * @param perm   The permission node to check
     * @return True if the player has the permission node set
     */
    public boolean hasPermission(OfflinePlayer player, String perm) {
        if (!player.isOnline() && !isVaultPresent()) {
            return false;
        }

        if (!player.isOnline()) {
            return permission.playerHas(Bukkit.getWorlds().get(0).getName(), player, perm);
        } else {
            return permission.playerHas(player.getPlayer(), perm);
        }
    }
}
