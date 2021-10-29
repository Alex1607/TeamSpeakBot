package com.pluoi.tsbot.command;

import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.command.commands.*;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private final List<Command> commands = new ArrayList<>();
    private final boolean clientlist = TeamSpeakBot.getConfig().getBoolean("function.command.clientlist.enabled");
    private final boolean randomchannelcreator = TeamSpeakBot.getConfig().getBoolean("function.command.randomchannelcreator.enabled");
    private final boolean debug = TeamSpeakBot.getConfig().getBoolean("function.command.debug.enabled");
    private final boolean removetempchannels = TeamSpeakBot.getConfig().getBoolean("function.command.removetempchannels.enabled");
    private final boolean help = TeamSpeakBot.getConfig().getBoolean("function.command.help.enabled");
    private final boolean messageall = TeamSpeakBot.getConfig().getBoolean("function.command.messageall.enabled");
    private final boolean pokeall = TeamSpeakBot.getConfig().getBoolean("function.command.pokeall.enabled");
    private final boolean tempchannelstoperm = TeamSpeakBot.getConfig().getBoolean("function.command.tempchannelstoperm.enabled");
    private final Logger logger = new Logger();

    public CommandManager() {
        logger.write("Starting CommandManager...", -1);
        registerCommands();
        logger.write("CommandManager started!", -1);
    }

    public List<Command> getCommands() {
        return commands;
    }

    private void registerCommands() {
        if (clientlist) {
            commands.add(new ClientList());
        }
        if (randomchannelcreator) {
            commands.add(new CreateRandomChannels());
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
            commands.add(new TempToPerm());
        }
    }
}
