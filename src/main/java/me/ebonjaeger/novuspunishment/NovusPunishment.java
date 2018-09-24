package me.ebonjaeger.novuspunishment;

import ch.jalu.injector.Injector;
import ch.jalu.injector.InjectorBuilder;
import me.ebonjaeger.novuspunishment.configuration.SettingsManager;
import me.ebonjaeger.novuspunishment.datasource.MySQL;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;

public class NovusPunishment extends JavaPlugin {

	private MySQL dataSource;

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
	}

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);

		dataSource.close();
	}

	private void registerCommands(Injector injector) {
		// TODO: Register commands
	}
}
