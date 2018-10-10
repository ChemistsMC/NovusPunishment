package me.ebonjaeger.novuspunishment.configuration;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import java.util.List;

import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class ActionSettings implements SettingsHolder {

	@Comment({"Number of warnings a player can have in a session before they are automatically kicked",
			"Set to 0 to disable"})
	public static final Property<Integer> WARNS_UNTIL_KICK = newProperty("actions.warns-until-kick", 0);

	@Comment("Commands that the player cannot perform while muted, e.g. msg, tell")
	public static final Property<List<String>> DISALLOWED_COMMANDS =
			newListProperty("actions.disallowed-commands-while-muted");
}
