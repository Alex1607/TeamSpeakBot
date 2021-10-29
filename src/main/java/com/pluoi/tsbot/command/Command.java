package com.pluoi.tsbot.command;

import com.pluoi.tsbot.Logger;

public abstract class Command {
    protected static final Logger logger = new Logger();

    private final String name;
    private final String description;
    private final String command;

    protected Command(String name, String description, String command) {
        this.name = name;
        this.description = description;
        this.command = command;
    }

    public abstract void execute(String[] args, int id);

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getCommand() {
        return command;
    }
}
