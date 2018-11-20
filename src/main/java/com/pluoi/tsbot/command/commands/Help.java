package com.pluoi.tsbot.command.commands;

import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.command.Command;

public class Help extends Command {
    private Logger logger = new Logger();

    public Help() {
        super("Help", "Shows the user the Help menu", "help");
    }

    @Override
    public void execute(String[] args, int id) {
        for (Command command : TeamSpeakBot.getCommandManager().getCommands()) {
            logger.write(command.getCommand() + " | " + command.getDescription(), id);
        }
    }
}
