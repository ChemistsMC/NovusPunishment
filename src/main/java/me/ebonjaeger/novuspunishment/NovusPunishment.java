package me.ebonjaeger.novuspunishment;

import ch.jalu.injector.Injector;
import ch.jalu.injector.InjectorBuilder;
import co.aikar.commands.PaperCommandManager;
import me.ebonjaeger.novuspunishment.command.KickCommand;
import me.ebonjaeger.novuspunishment.configuration.SettingsManager;
import me.ebonjaeger.novuspunishment.datasource.MySQL;
import me.ebonjaeger.novuspunishment.listener.PlayerLoginListener;
import me.ebonjaeger.novuspunishment.listener.PlayerLogoutListener;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NovusPunishment extends JavaPlugin {

	private MySQL dataSource;

	private boolean isShuttingDown = false;

	private Map<UUID, PlayerState> playerStates = new HashMap<>();

	@Override
	public void onEnable() {
		// Create default config file if it does not exist
		if (!Files.exists(new File(getDataFolder(), "config.yml").toPath())) {
			saveResource("config.yml", false);
		}

		// Injector
		Injector injector = new InjectorBuilder().addDefaultHandlers("me.ebonjaeger.novuspunishment").create();
		injector.register(NovusPunishment.class, this);
		injector.register(Server.class, getServer());
		injector.register(PluginManager.class, getServer().getPluginManager());

		SettingsManager settingsManager = SettingsManager.create(new File(getDataFolder(), "config.yml"));
		injector.register(SettingsManager.class, settingsManager);

		this.dataSource = injector.getSingleton(MySQL.class);

		getServer().getPluginManager().registerEvents(injector.getSingleton(PlayerLoginListener.class), this);
		getServer().getPluginManager().registerEvents(injector.getSingleton(PlayerLogoutListener.class), this);
	}

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
		this.isShuttingDown = true;

		// Save all player states in memory to the disk
		for (PlayerState state : playerStates.values()) {
			dataSource.savePlayerState(state);
		}
		playerStates.clear();

		dataSource.close();
	}

	private void registerCommands(Injector injector) {
		PaperCommandManager commandManager = new PaperCommandManager(this);

		commandManager.registerCommand(injector.getSingleton(KickCommand.class));
	}

	public boolean isShuttingDown() {
		return isShuttingDown;
	}

	public void sendMessage(Player player, Message message, String... replacers) {
		String finalMessage = message.getMessage();
		if (replacers.length > 0) {
			for (int i = 0; i < replacers.length; i++) {
				finalMessage = finalMessage.replace("{" + i + "}", replacers[i]);
			}
		}

		player.sendMessage(finalMessage);
	}

	public void sendMessage(CommandSender sender, Message message, String... replacers) {
		String finalMessage = message.getMessage();
		if (replacers.length > 0) {
			for (int i = 0; i < replacers.length; i++) {
				finalMessage = finalMessage.replace("{" + i + "}", replacers[i]);
			}
		}

		sender.sendMessage(finalMessage);
	}

	// TODO: Maybe create some sort of state manager class for the next three methods

	/**
	 * Add a new {@link PlayerState} into memory.
	 *
	 * @param uniqueID The player's unique ID
	 * @param playerState The player's current state
	 */
	public void addPlayerState(UUID uniqueID, PlayerState playerState) {
		playerStates.put(uniqueID, playerState);
	}

	/**
	 * Get a loaded {@link PlayerState} for the given {@link UUID}.
	 * Will return {@code null} if no state for the player is currently
	 * in memory.
	 *
	 * @param uniqueID The player's unique ID
	 * @return The player's state, or null
	 */
	public PlayerState getPlayerState(UUID uniqueID) {
		return playerStates.get(uniqueID);
	}

	/**
	 * Remove a {@link PlayerState} from memory, e.g. when a player
	 * is leaving the server.
	 *
	 * @param uniqueID The unique ID of the player
	 */
	public void removePlayerState(UUID uniqueID) {
		playerStates.remove(uniqueID);
	}
}
