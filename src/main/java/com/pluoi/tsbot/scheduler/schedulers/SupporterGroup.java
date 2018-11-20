package com.pluoi.tsbot.scheduler.schedulers;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.event.eventHandler.SupportBot;
import com.pluoi.tsbot.scheduler.Scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SupporterGroup extends Scheduler {
    private final ScheduledExecutorService scheduler;
    ScheduledFuture<?> future;
    private TS3Api api = TeamSpeakBot.api;
    private String message = TeamSpeakBot.getConfig().getString("supportbot.message.added");
    private int maxtime = TeamSpeakBot.getConfig().getInt("supportbot.maxafktime");
    private int group = TeamSpeakBot.getConfig().getInt("supportbot.group");
    private int period = TeamSpeakBot.getConfig().getInt("function.scheduler.supportergroup.timer");

    public SupporterGroup() {
        super("SupporterGroup", "Aktualiesiert die Servergruppe", 0, 10);
        this.setPeriod(period);
        scheduler = Executors.newScheduledThreadPool(1);
    }

    private void run() {
        Logger.debug("SupporterGroupScheduler runned");
        for (Client i : api.getClients()) {
            ClientInfo info = api.getClientInfo(i.getId());
            int dbId = i.getDatabaseId();
            if (info == null || info.isServerQueryClient()) {
                continue;
            }
            if (SupportBot.afk.contains(i.getUniqueIdentifier())) {
                if ((info.getIdleTime() < maxtime) && !info.isAway()) {
                    SupportBot.afk.remove(i.getUniqueIdentifier());
                    api.addClientToServerGroup(group, dbId);
                    api.sendPrivateMessage(i.getId(), message);
                }
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
