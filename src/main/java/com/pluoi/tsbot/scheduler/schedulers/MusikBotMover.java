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

public class MusikBotMover extends Scheduler {
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> future;
    private final int storage = TeamSpeakBot.getConfig().getInt("musicbotrent.botstorage");
    private final int rentgroup = TeamSpeakBot.getConfig().getInt("musicbotrent.rentgroup");
    private final int kickchannel = TeamSpeakBot.getConfig().getInt("musicbotrent.kickchannel");
    private final int period = TeamSpeakBot.getConfig().getInt("function.scheduler.musicbotmover.timer");

    public MusikBotMover() {
        super("MusikMute", "Checks if the MusikBot is alone and moves him back.", 0, 60);
        this.setPeriod(period);
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    private void run() {
        logger.debug("MusikBotMover runned");
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
            if (tempChannels.contains(channelId)) {
                if (client.isInServerGroup(rentgroup)) {
                    TeamSpeakBot.api.moveClient(client.getId(), storage);
                }
            } else if (channelId == kickchannel && client.isInServerGroup(rentgroup)) {
                TeamSpeakBot.api.moveClient(client.getId(), storage);
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
