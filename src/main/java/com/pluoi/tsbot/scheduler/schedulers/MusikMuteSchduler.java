package com.pluoi.tsbot.scheduler.schedulers;

import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MusikMuteSchduler extends Scheduler {
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> future;
    private final int normalgroup = TeamSpeakBot.getConfig().getInt("botmuter.channel.normalgroup");
    private final int mutegroup = TeamSpeakBot.getConfig().getInt("botmuter.channel.mutegroup");
    private final int rentbot = TeamSpeakBot.getConfig().getInt("botmuter.server.rentgroup");
    private final int period = TeamSpeakBot.getConfig().getInt("function.scheduler.musicbotmuter.timer");

    public MusikMuteSchduler() {
        super("MusikMute", "Checks if the Musik Channel is empty and then mutes the bot", 0, 60);
        this.setPeriod(period);
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    private void run() {
        logger.debug("MusikMute runned");
        ArrayList<Integer> tempChannels = new ArrayList<>();
        for (Channel channel : TeamSpeakBot.api.getChannels()) {
            if (channel.getTotalClients() == 1) {
                tempChannels.add(channel.getId());
            }
        }
        for (Client client : TeamSpeakBot.api.getClients()) {
            if (client.isServerQueryClient()) {
                continue;
            }
            int channelId = client.getChannelId();
            if (tempChannels.contains(channelId) && client.isInServerGroup(normalgroup) && !client.isInServerGroup(rentbot)) {
                if (client.getChannelGroupId() == mutegroup) {
                    return;
                }
                TeamSpeakBot.api.setClientChannelGroup(mutegroup, channelId, client.getDatabaseId());
            }
        }
    }

    @Override
    public void start(int delay, int period) {
        future = scheduler.scheduleAtFixedRate(this::run, delay, period, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        future.cancel(true);
    }
}
