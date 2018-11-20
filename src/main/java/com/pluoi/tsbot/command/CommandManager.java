package com.pluoi.tsbot.command;

import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.command.commands.*;

import java.util.ArrayList;

public class CommandManager {
    private static ArrayList<Command> commands = new ArrayList<>();
    private boolean clientlist = TeamSpeakBot.config.getBoolean("function.command.clientlist.enabled");
    private boolean randomchannelcreator = TeamSpeakBot.config.getBoolean("function.command.randomchannelcreator.enabled");
    private boolean debug = TeamSpeakBot.config.getBoolean("function.command.debug.enabled");
    private boolean removetempchannels = TeamSpeakBot.config.getBoolean("function.command.removetempchannels.enabled");
    private boolean help = TeamSpeakBot.config.getBoolean("function.command.help.enabled");
    private boolean messageall = TeamSpeakBot.config.getBoolean("function.command.messageall.enabled");
    private boolean pokeall = TeamSpeakBot.config.getBoolean("function.command.pokeall.enabled");
    private boolean tempchannelstoperm = TeamSpeakBot.config.getBoolean("function.command.tempchannelstoperm.enabled");


    public CommandManager() {
        Logger.write("Starting CommandManager...", -1);
        registerCommands();
        Logger.write("CommandManager started!", -1);
    }

    public static ArrayList<Command> getCommands() {
        return commands;
    }

    private void registerCommands() {
        if (clientlist) {
            commands.add(new clientlist());
        }
        if (randomchannelcreator) {
            commands.add(new createRandomChannels());
        }
        if (debug) {
            commands.add(new Debug());
        }
        if (removetempchannels) {
            commands.add(new DelTempChannels());
        }
        if (help) {
            commands.add(new Help());
        }
        if (messageall) {
            commands.add(new MessageAll());
        }
        if (pokeall) {
            commands.add(new PokeAll());
        }
        if (tempchannelstoperm) {
            commands.add(new tempToPerm());
        }

    }
}
