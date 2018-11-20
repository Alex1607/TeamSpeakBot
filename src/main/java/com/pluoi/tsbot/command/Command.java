package com.pluoi.tsbot.command;

public abstract class Command {
    private String name;
    private String description;
    private String command;

    public Command(String name, String description, String command) {
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
