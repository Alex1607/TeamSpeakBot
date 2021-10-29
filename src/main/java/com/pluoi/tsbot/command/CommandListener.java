package com.pluoi.tsbot.command;

import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;

import java.util.Scanner;

public class CommandListener {
    private static Thread commandLine;
    private static boolean listening = false;
    private static boolean didCommandExecute = false;
    private Logger logger = new Logger();

    public void start() {
        listening = true;
        commandLine = new Thread(() -> {
            do {
                Scanner s = new Scanner(System.in);

                String msg = s.nextLine();
                String cmd = msg.split(" ")[0];
                didCommandExecute = false;

                for (Command command : TeamSpeakBot.getCommandManager().getCommands()) {
                    if (!command.getName().equalsIgnoreCase(cmd)) {
                        continue;
                    }

                    msg = msg.replace(cmd + " ", "");
                    logger.debug(msg);
                    String[] args = msg.split(" ");
                    if (args.length < 1) {
                        logger.debug("0 args found");
                        command.execute(new String[]{}, -1);
                    } else {
                        logger.debug("found " + args.length + " args");
                        command.execute(args, -1);
                    }
                    didCommandExecute = true;
                }
                if (!didCommandExecute) {
                    logger.write("Command not found, try 'help' to get Help!", -1);
                }
            } while (listening);
        });
        commandLine.start();
    }

    public void stop() {
        listening = false;
        commandLine.interrupt();
        logger.write("Commandlinelistener has been stopped!", -1);
    }

    public void restart() {
        logger.write("Stopping Commandlinelistener...", -1);
        listening = false;
        if (commandLine.isAlive()) {
            commandLine.interrupt();
        }
        commandLine.start();
        listening = true;
        logger.write("Started Commandlinelistener!", -1);
    }
}
