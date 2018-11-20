package com.pluoi.tsbot.event.events;

import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.pluoi.tsbot.event.Event;

public class TextMessage extends TS3EventAdapter {
    Event event;

    public TextMessage(Event event) {
        this.event = event;
    }

    @Override
    public void onTextMessage(TextMessageEvent e) {
        event.executeTextMessage(e);
    }
}
