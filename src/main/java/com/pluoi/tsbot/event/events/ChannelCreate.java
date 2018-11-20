package com.pluoi.tsbot.event.events;

import com.github.theholywaffle.teamspeak3.api.event.ChannelCreateEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.pluoi.tsbot.event.Event;

public class ChannelCreate extends TS3EventAdapter {
    Event event;

    public ChannelCreate(Event event) {
        this.event = event;
    }

    @Override
    public void onChannelCreate(ChannelCreateEvent e) {
        event.executeChannelCreate(e);
    }
}
