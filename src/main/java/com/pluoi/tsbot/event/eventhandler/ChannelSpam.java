package com.pluoi.tsbot.event.eventhandler;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.ChannelCreateEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.event.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChannelSpam extends Event {
    private static final Map<Integer, Long> channelCreateTime = new HashMap<>();
    private final String message = TeamSpeakBot.getConfig().getString("antichannelspam.message");
    private final int configtime = TeamSpeakBot.getConfig().getInt("antichannelspam.waittime");

    public static void resetTimer() {
        channelCreateTime.clear();
    }

    @Override
    public void ChannelCreate(ChannelCreateEvent e) {
        int id = e.getInvokerId();
        long time = System.currentTimeMillis();
        ClientInfo clientInfo = TeamSpeakBot.api.getClientInfo(id);
        if (clientInfo == null || clientInfo.isServerQueryClient()) {
            return;
        }
        if (channelCreateTime.containsKey(id)) {
            long lastTime = channelCreateTime.get(id);
            if (time - lastTime < configtime) {
                TeamSpeakBot.api.deleteChannel(e.getChannelId(), true);
                String waittime = String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(configtime - (time - lastTime)),
                        TimeUnit.MILLISECONDS.toSeconds(configtime - (time - lastTime)) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(configtime - (time - lastTime)))
                );
                TeamSpeakBot.api.pokeClient(id, message.replace("%time%", String.valueOf(waittime)));
                return;
            }
        }
        logger.write("Created new Channel: " + TeamSpeakBot.api.getChannelInfo(e.getChannelId()).getName(), -1);
        channelCreateTime.put(id, time);
    }
}
