package me.ebonjaeger.novuspunishment;

import ch.jalu.injector.Injector;
import ch.jalu.injector.InjectorBuilder;
import co.aikar.commands.PaperCommandManager;
import me.ebonjaeger.novuspunishment.command.BanCommand;
import me.ebonjaeger.novuspunishment.command.KickCommand;
import me.ebonjaeger.novuspunishment.command.MuteCommand;
import me.ebonjaeger.novuspunishment.command.WarnCommand;
import me.ebonjaeger.novuspunishment.configuration.SettingsManager;
import me.ebonjaeger.novuspunishment.datasource.MySQL;
import me.ebonjaeger.novuspunishment.listener.PlayerChatListener;
import me.ebonjaeger.novuspunishment.listener.PlayerLoginListener;
import me.ebonjaeger.novuspunishment.listener.PlayerLogoutListener;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;

public class NovusPunishment extends JavaPlugin {

	private MySQL dataSource;
	private StateManager stateManager;

	private boolean isShuttingDown = false;

	@Override
	public void onEnable() {
		// Create default config file if it does not exist
		if (!Files.exists(new File(getDataFolder(), "config.yml").toPath())) {
			saveResource("config.yml", false);
		}

		ConsoleLogger.setLogger(getLogger());

		// Injector
		Injector injector = new InjectorBuilder().addDefaultHandlers("me.ebonjaeger.novuspunishment").create();
		injector.register(NovusPunishment.class, this);
		injector.register(Server.class, getServer());
		injector.register(PluginManager.class, getServer().getPluginManager());

		SettingsManager settingsManager = SettingsManager.create(new File(getDataFolder(), "config.yml"));
		injector.register(SettingsManager.class, settingsManager);

		this.dataSource = injector.getSingleton(MySQL.class);
		this.stateManager = injector.getSingleton(StateManager.class);

		getServer().getPluginManager().registerEvents(injector.getSingleton(PlayerLoginListener.class), this);
		getServer().getPluginManager().registerEvents(injector.getSingleton(PlayerLogoutListener.class), this);
		getServer().getPluginManager().registerEvents(injector.getSingleton(PlayerChatListener.class), this);

		registerCommands(injector);
	}

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
		this.isShuttingDown = true;

		stateManager.flushStates();

		dataSource.close();
	}

	private void registerCommands(Injector injector) {
		PaperCommandManager commandManager = new PaperCommandManager(this);

		commandManager.registerCommand(injector.getSingleton(MuteCommand.class));
		commandManager.registerCommand(injector.getSingleton(WarnCommand.class));
		commandManager.registerCommand(injector.getSingleton(KickCommand.class));
		commandManager.registerCommand(injector.getSingleton(BanCommand.class));
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
}
