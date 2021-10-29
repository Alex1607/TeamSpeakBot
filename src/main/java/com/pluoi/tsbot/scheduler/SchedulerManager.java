package com.pluoi.tsbot.scheduler;

import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.scheduler.schedulers.*;

import java.util.ArrayList;
import java.util.List;

public class SchedulerManager {
    private static final List<Scheduler> schedulers = new ArrayList<>();
    private final boolean musicbotmover = TeamSpeakBot.getConfig().getBoolean("function.scheduler.musicbotmover.enabled");
    private final boolean musicbotmuter = TeamSpeakBot.getConfig().getBoolean("function.scheduler.musicbotmuter.enabled");
    private final boolean randomcatgif = TeamSpeakBot.getConfig().getBoolean("function.scheduler.randomcatgif.enabled");
    private final boolean supportergroup = TeamSpeakBot.getConfig().getBoolean("function.scheduler.supportergroup.enabled");
    private final boolean usergroup = TeamSpeakBot.getConfig().getBoolean("function.scheduler.usergroup.enabled");
    private final Logger logger = new Logger();

    public SchedulerManager() {
        logger.write("Starting ScheduleManager...", -1);
        registerThreads();
        startSchdulers();
        logger.write("ScheduleManager started!", -1);
    }

    public static List<Scheduler> getActiveThreads() {
        return schedulers;
    }

    private void registerThreads() {
        if (musicbotmover) {
            schedulers.add(new MusikBotMover());
        }
        if (musicbotmuter) {
            schedulers.add(new MusikMuteSchduler());
        }
        if (randomcatgif) {
            schedulers.add(new RandomGif());
        }
        if (supportergroup) {
            schedulers.add(new SupporterGroup());
        }
        if (usergroup) {
            schedulers.add(new UserGroup());
        }
    }

    private void startSchdulers() {
        for (Scheduler scheduler : schedulers) {
            scheduler.start(scheduler.getDelay(), scheduler.getPeriod());
        }
    }
}
