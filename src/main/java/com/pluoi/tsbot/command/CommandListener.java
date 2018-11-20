package com.pluoi.tsbot.command;

import com.pluoi.tsbot.Logger;

import java.util.Scanner;

public class CommandListener {
    static Thread commandLine;
    private static boolean listening = false;
    private static boolean didCommandExecute = false;

    public static void start() {
        listening = true;
        commandLine = new Thread(() ->
        {
            do {
                Scanner s = new Scanner(System.in);

                String msg = s.nextLine();
                String cmd = msg.split(" ")[0];
                didCommandExecute = false;

                for (Command command : CommandManager.getCommands()) {
                    if (command.getName().equalsIgnoreCase(cmd)) {
                        msg = msg.replace(cmd + " ", "");
                        Logger.debug(msg);
                        String[] args = msg.split(" ");
                        if (args.length < 1) {
                            Logger.debug("0 args found");
                            command.execute(new String[]{}, -1);
                        } else {
                            Logger.debug("found " + args.length + " args");
                            command.execute(args, -1);
                        }
                        didCommandExecute = true;
                    }
                }
                if (didCommandExecute == false) {
                    Logger.write("Command not found, try 'help' to get Help!", -1);
                }
            } while (listening);
        });
        commandLine.start();
    }

    public static void stop() {
        listening = false;
        commandLine.interrupt();
        Logger.write("Commandlinelistener has been stopped!", -1);
    }

    public static void restart() {
        Logger.write("Stopping Commandlinelistener...", -1);
        listening = false;
        if (commandLine.isAlive()) {
            commandLine.stop(); //TODO: Bessere Methode dafür finden
        }
        commandLine.start();
        listening = true;
        Logger.write("Started Commandlinelistener!", -1);
    }

}
