package me.ebonjaeger.novuspunishment.action;

/**
 * Enum for the different types of {@link Action}s.
 */
public enum ActionType {

    MUTE("mute"),

    WARNING("warning"),

    KICK("kick"),

    TEMPORARY_BAN("tempban"),

    PERMANENT_BAN("permban");

    private String name;

    ActionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
