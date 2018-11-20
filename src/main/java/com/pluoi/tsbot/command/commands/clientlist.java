package com.pluoi.tsbot.command.commands;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.command.Command;

import java.util.concurrent.TimeUnit;

public class clientlist extends Command {
    private Logger logger = new Logger();

    public clientlist() {
        super("ClientList", "Lists all current users on the Teamspeak", "clientlist");
    }

    @Override
    public void execute(String[] args, int id) {
        int allUsers = 0;
        int talking = 0;
        int afk = 0;
        int online = 0;
        for (Client client : TeamSpeakBot.api.getClients()) {
            String message = "%name% ";
            long idleTime = client.getIdleTime();

            message += "• Channel: " + TeamSpeakBot.api.getChannelInfo(client.getChannelId()).getName() + " ";

            if (client.isAway() || client.isInputMuted() || client.isOutputMuted() || idleTime > 900000) {
                String afkTime = String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toHours(idleTime),
                        TimeUnit.MILLISECONDS.toMinutes(idleTime) - TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(idleTime))
                );
                message += "• [color=red]AFK[/color] (" + afkTime + " Hours) ";
                afk++;
            } else if (client.isTalking()) {
                message += "• [color=blue]talking[/color] ";
                talking++;
            } else {
                String afkTime = String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(idleTime),
                        TimeUnit.MILLISECONDS.toSeconds(idleTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(idleTime))
                );
                message += "• [color=green]Present[/color] (Inactive since: " + afkTime + " minutes) ";
                online++;
            }
            logger.write(message.replace("%name%", "[URL=" + TeamSpeakBot.api.getClientInfo(client.getId()).getClientURI() + "]" + TeamSpeakBot.api.getClientInfo(client.getId()).getNickname() + "[/URL]"), id);
            allUsers++;
        }
        logger.write(allUsers + " Users online • [b]Talking:[/b] " + talking + " • [b]Present:[/b] " + online + " • [b]AFK:[/b] " + afk, id);
    }
}
