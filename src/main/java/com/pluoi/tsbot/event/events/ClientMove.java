package com.pluoi.tsbot.event.events;

import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.pluoi.tsbot.event.Event;

public class ClientMove extends TS3EventAdapter {
    Event event;

    public ClientMove(Event event) {
        this.event = event;
    }

    @Override
    public void onClientMoved(ClientMovedEvent e) {
        event.executeClientMove(e);
    }
}
