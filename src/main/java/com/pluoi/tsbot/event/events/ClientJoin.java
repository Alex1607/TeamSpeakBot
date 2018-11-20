package com.pluoi.tsbot.event.events;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.pluoi.tsbot.event.Event;

public class ClientJoin extends TS3EventAdapter {
    Event event;

    public ClientJoin(Event event) {
        this.event = event;
    }

    @Override
    public void onClientJoin(ClientJoinEvent e) {
        event.executeJoinEvent(e);
    }
}
