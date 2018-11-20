package com.pluoi.tsbot.event.eventhandler;

import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroupClient;
import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.event.Event;

import java.util.ArrayList;
import java.util.List;

public class MusikBotMover extends Event {
    private int botstorage = TeamSpeakBot.getConfig().getInt("musikbotrent.botstorage");
    private int rentgroup = TeamSpeakBot.getConfig().getInt("musikbotrent.rentgroup");
    private List<String> groups = (List<String>) TeamSpeakBot.getConfig().getList("musikbotrent.channelgroups");
    private Logger logger = new Logger();

    @Override
    public void ClientMove(ClientMovedEvent event) {
        int iid = event.getClientId();
        int channelID = event.getTargetChannelId();
        String channelName = TeamSpeakBot.api.getChannelInfo(botstorage).getName(); // Schlafen Raum
        ArrayList<Integer> channelswithbot = new ArrayList<>();
        int clientCount = TeamSpeakBot.api.getChannelByNameExact(channelName, false).getTotalClients();
        if (groups.contains(String.valueOf(TeamSpeakBot.api.getClientInfo(iid).getChannelGroupId()))) {
            if (SupportBot.rentingMusic.contains(iid)) {
                SupportBot.rentingMusic.remove(Integer.valueOf(iid));
                if (clientCount <= 0) {
                    return;
                }
                for (ServerGroupClient serverGroupClient : TeamSpeakBot.api.getServerGroupClients(rentgroup)) {
                    Client client = TeamSpeakBot.api.getClientByNameExact(serverGroupClient.getNickname(), false);
                    if (client.getChannelId() == botstorage) {
                        logger.debug("MusikBot ausgeliehen.");
                        TeamSpeakBot.api.moveClient(client.getId(), channelID);
                        break;
                    }
                }
                for (ServerGroupClient serverGroupClient1 : TeamSpeakBot.api.getServerGroupClients(rentgroup)) {
                    Client client1 = TeamSpeakBot.api.getClientByNameExact(serverGroupClient1.getNickname(), false);
                    if (client1.getChannelId() == botstorage) {
                        return;
                    }
                    if (channelswithbot.contains(client1.getChannelId())) {
                        TeamSpeakBot.api.moveClient(client1.getId(), botstorage);
                    }
                    channelswithbot.add(client1.getChannelId());
                }
            }
        }
    }
}
