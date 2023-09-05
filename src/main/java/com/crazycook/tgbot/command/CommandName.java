package com.crazycook.tgbot.command;

public enum CommandName {

    S("/s"),
    M("/m"),
    L("/l"),
    PRICE("/price"),
    CHOOSE_BOX("/choose_box"),
    FLAVOR("/flavor"),
    ORDER("/order"),
    MESSAGE("/message"),
    START("/start"),
    UNKNOWN_COMMAND("/unknown_command"),
    REFRESH("/refresh");

    private final String commandName;

    CommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }
}
