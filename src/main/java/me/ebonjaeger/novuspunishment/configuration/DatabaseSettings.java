package me.ebonjaeger.novuspunishment.configuration;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class DatabaseSettings implements SettingsHolder {

	@Comment("Hostname for the MySQL server")
	public static final Property<String> DATABASE_HOSTNAME = newProperty("database.hostname", "localhost");

	@Comment("Port for the MySQL server")
	public static final Property<String> DATABASE_PORT = newProperty("database.port", "3306");

	@Comment("MySQL username")
	public static final Property<String> DATABASE_USER = newProperty("database.username", "admin");

	@Comment("MySQL password")
	public static final Property<String> DATABASE_PASSWORD = newProperty("database.password", "adminadmin");
}
