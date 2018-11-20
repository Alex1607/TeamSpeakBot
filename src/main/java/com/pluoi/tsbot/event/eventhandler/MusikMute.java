package com.pluoi.tsbot.event.eventhandler;

import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.event.Event;

public class MusikMute extends Event {

    private int normalgroup = TeamSpeakBot.getConfig().getInt("botmuter.channel.normalgroup");
    private int mutegroup = TeamSpeakBot.getConfig().getInt("botmuter.channel.mutegroup");
    private int botgroup = TeamSpeakBot.getConfig().getInt("botmuter.server.botgroup");
    private Logger logger = new Logger();

    @Override
    public void ClientMove(ClientMovedEvent event) {
        int channelID = event.getTargetChannelId();
        String channelName = TeamSpeakBot.api.getChannelInfo(channelID).getName();
        int clientCount = TeamSpeakBot.api.getChannelByNameExact(channelName, false).getTotalClients();
        logger.debug("ClientCount -> " + clientCount);
        if (clientCount == 2) {
            for (Client client : TeamSpeakBot.api.getClients()) {
                if (client.getChannelId() != channelID) {
                    return;
                }
                ClientInfo clientInfo = TeamSpeakBot.api.getClientInfo(client.getId());
                if (client.getChannelGroupId() == mutegroup && clientInfo.isInServerGroup(botgroup)) {
                    TeamSpeakBot.api.setClientChannelGroup(normalgroup, channelID, client.getDatabaseId());
                }
            }
        }
    }
}
