package com.pluoi.tsbot.command.commands;

import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.command.Command;

public class Debug extends Command {
    private Logger logger = new Logger();
    public Debug() {
        super("Debug", "Disabled / Enables the debug messages", "debug");
    }

    @Override
    public void execute(String[] args, int id) {
        if (id != -1) {
            logger.write("Console only!", id);
            return;
        }
        if (logger.isDebugMode()) {
            logger.setDebugMode(false);
            logger.write("Debug mode disabled!", id);
        } else {
            logger.setDebugMode(true);
            logger.write("Debug mode enabled!", id);
        }
    }
}
