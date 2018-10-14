package me.ebonjaeger.novuspunishment.configuration;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManagerImpl;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.migration.MigrationService;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.YamlFileResource;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class SettingsManager extends SettingsManagerImpl {

	private static List<Class<? extends SettingsHolder>> PROPERTY_HOLDERS =
			Arrays.asList(DatabaseSettings.class, ActionSettings.class);

	private SettingsManager(YamlFileResource file, ConfigurationData configurationData, MigrationService migrationService) {
		super(file, configurationData, migrationService);
	}

	public static SettingsManager create(File file) {
		YamlFileResource resource = new YamlFileResource(file);
		ConfigurationData configurationData = ConfigurationDataBuilder.createConfiguration(PROPERTY_HOLDERS);
		MigrationService migrater = new PlainMigrationService();

		return new SettingsManager(resource, configurationData, migrater);
	}
}
