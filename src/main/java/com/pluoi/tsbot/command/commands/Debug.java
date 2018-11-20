package com.pluoi.tsbot.command.commands;

import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.command.Command;

public class Debug extends Command {
    public Debug() {
        super("Debug", "Disabled / Enables the debug messages", "debug");
    }

    @Override
    public void execute(String[] args, int id) {
        if (id != -1) {
            Logger.write("Console only!", id);
            return;
        }
        if (Logger.debugMode) {
            Logger.debugMode = false;
            Logger.write("Debug mode disabled!", id);
        } else {
            Logger.debugMode = true;
            Logger.write("Debug mode enabled!", id);
        }
    }
}
