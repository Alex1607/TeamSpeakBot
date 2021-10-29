package com.pluoi.tsbot.command.commands;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.command.Command;

public class MessageAll extends Command {
    public MessageAll() {
        super("MessageAll", "Message everyone with a given message", "messageall");
    }

    @Override
    public void execute(String[] args, int id) {
        StringBuilder msg = new StringBuilder();
        if (args.length > 0) {
            for (String arg : args) {
                msg.append(arg).append(" ");
            }
            for (Client client : TeamSpeakBot.api.getClients()) {
                TeamSpeakBot.api.sendPrivateMessage(client.getId(), msg.toString());
                logger.debug("Messaged " + client.getNickname() + " with \"" + msg + "\"");
            }
        } else {
            logger.error("No args found! Please use messageall <message>", id);
        }
    }
}
