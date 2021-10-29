package com.pluoi.tsbot.scheduler.schedulers;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.scheduler.Scheduler;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class UserGroup extends Scheduler {
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> future;
    private final int period = TeamSpeakBot.getConfig().getInt("function.scheduler.usergroup.timer");
    private final int group = TeamSpeakBot.getConfig().getInt("function.scheduler.usergroup.group");
    private final int time = TeamSpeakBot.getConfig().getInt("function.scheduler.usergroup.time");
    private final List<String> nothisgroup = (List<String>) TeamSpeakBot.getConfig().getList("function.scheduler.usergroup.nothisgroup");

    public UserGroup() {
        super("UserGroupThread", "Gives guests the user group after a time", 0, 30);
        this.setPeriod(period);
        scheduler = Executors.newScheduledThreadPool(1);
    }

    private void run() {
        logger.debug("UserGroup runned");
        for (Client i : TeamSpeakBot.api.getClients()) {
            ClientInfo info = TeamSpeakBot.api.getClientInfo(i.getId());
            if (info == null|| info.isServerQueryClient()) {
                continue;
            }

            if (info.getTimeConnected() > time) {
                for (int g : info.getServerGroups()) {
                    if (nothisgroup.contains(String.valueOf(g))) {
                        return;
                    }
                }
                TeamSpeakBot.api.addClientToServerGroup(group, i.getDatabaseId());
                logger.write(info.getNickname() + " was added to the User group.", -1);
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
