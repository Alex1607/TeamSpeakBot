package com.pluoi.tsbot.event;

import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.event.eventhandler.*;
import com.pluoi.tsbot.event.events.*;

import java.util.ArrayList;

public class EventManager {
    private static ArrayList<Event> events = new ArrayList<>();
    private Event event = new Event();
    private boolean ipbandeleter = TeamSpeakBot.getConfig().getBoolean("function.event.ipbandeleter.enabled");
    private boolean channelspam = TeamSpeakBot.getConfig().getBoolean("function.event.channelspam.enabled");
    private boolean commands = TeamSpeakBot.getConfig().getBoolean("function.event.commands.enabled");
    private boolean botmuter = TeamSpeakBot.getConfig().getBoolean("function.event.botmuter.enabled");
    private boolean supportbot = TeamSpeakBot.getConfig().getBoolean("function.event.supportbot.enabled");
    private boolean iplimit = TeamSpeakBot.getConfig().getBoolean("function.event.iplimit.enabled");
    private boolean musicbotrent = TeamSpeakBot.getConfig().getBoolean("function.event.musicbotrent.enabled");
    private Logger logger = new Logger();

    public EventManager() {
        logger.write("Starting EventManager...", -1);
        TeamSpeakBot.api.addTS3Listeners(new ClientJoin(event));
        TeamSpeakBot.api.addTS3Listeners(new ClientLeave(event));
        TeamSpeakBot.api.addTS3Listeners(new TextMessage(event));
        TeamSpeakBot.api.addTS3Listeners(new ClientMove(event));
        TeamSpeakBot.api.addTS3Listeners(new ChannelCreate(event));
        registerEvents();
        logger.write("EventManager started!", -1);
    }

    public static ArrayList<Event> getEvents() {
        return events;
    }

    private void registerEvents() {
        if (ipbandeleter) {
            events.add(new BanDeleter());
        }
        if (channelspam) {
            events.add(new ChannelSpam());
        }
        if (commands) {
            events.add(new Commands());
        }
        if (botmuter) {
            events.add(new MusikMute());
        }
        if (iplimit) {
            events.add(new TooManyIPs());
        }
        if (supportbot) {
            events.add(new SupportBot());
        }
        if (musicbotrent) {
            events.add(new MusikBotMover());
        }
    }
}
