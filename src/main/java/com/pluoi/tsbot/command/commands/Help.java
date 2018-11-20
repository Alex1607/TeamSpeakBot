package com.pluoi.tsbot.command.commands;

import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.command.Command;
import com.pluoi.tsbot.command.CommandManager;

public class Help extends Command {
    public Help() {
        super("Help", "Shows the user the Help menu", "help");
    }

    @Override
    public void execute(String[] args, int id) {
        for (Command command : CommandManager.getCommands()) {
            Logger.write(command.getCommand() + " | " + command.getDescription(), id);
        }
    }
}
