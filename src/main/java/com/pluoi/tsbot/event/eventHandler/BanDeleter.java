package com.pluoi.tsbot.event.eventHandler;

import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Ban;
import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.event.Event;

public class BanDeleter extends Event {

    @Override
    public void ClientLeave(ClientLeaveEvent event) {
        for (Ban bans : TeamSpeakBot.api.getBans()) {
            if (bans.getBannedIp().length() > 3) {
                TeamSpeakBot.api.deleteBan(bans.getId());
                Logger.debug("Removed IP Ban for ID " + bans.getId() + "!");
            }
        }
    }

}
