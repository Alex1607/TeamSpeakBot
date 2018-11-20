package com.pluoi.tsbot.command.commands;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.command.Command;

public class MessageAll extends Command {
    public MessageAll() {
        super("MessageAll", "Message everyone with a given message", "messageall");
    }

    @Override
    public void execute(String[] args, int id) {
        String msg = "";
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                msg = msg + args[i] + " ";
            }
            for (Client client : TeamSpeakBot.api.getClients()) {
                TeamSpeakBot.api.sendPrivateMessage(client.getId(), msg);
                Logger.debug("Messaged " + client.getNickname() + " with \"" + msg + "\"");
            }
        } else {
            Logger.error("No args found! Please us Messageall <message>", id);
        }
    }
}
