package com.pluoi.tsbot.event.events;

import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.pluoi.tsbot.event.Event;

public class ClientLeave extends TS3EventAdapter {
    Event event;

    public ClientLeave(Event event) {
        this.event = event;
    }

    @Override
    public void onClientLeave(ClientLeaveEvent e) {
        event.executeClientLeave(e);
    }
}
