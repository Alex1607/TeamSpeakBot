package com.pluoi.tsbot.command.commands;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.command.Command;

public class PokeAll extends Command {
    public PokeAll() {
        super("PokeAll", "Pokes everyone with a given message", "pokeall");
    }

    @Override
    public void execute(String[] args, int id) {
        StringBuilder msg = new StringBuilder();
        if (args.length > 0) {
            for (String arg : args) {
                msg.append(arg).append(" ");
            }
            for (Client client : TeamSpeakBot.api.getClients()) {
                TeamSpeakBot.api.pokeClient(client.getId(), msg.toString());
                logger.debug("Poked " + client.getNickname() + " with \"" + msg + "\"");
            }
        } else {
            logger.error("No args found! Please us pokeall <message>", id);
        }
    }
}
