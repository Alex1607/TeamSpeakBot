package com.pluoi.tsbot.event;

import com.github.theholywaffle.teamspeak3.api.event.*;

public class Event {
    public void executeJoinEvent(ClientJoinEvent event) {
        for (Event e : EventManager.getEvents()) {
            e.JoinEvent(event);
        }
    }

    public void executeClientLeave(ClientLeaveEvent event) {
        for (Event e : EventManager.getEvents()) {
            e.ClientLeave(event);
        }
    }

    public void executeTextMessage(TextMessageEvent event) {
        for (Event e : EventManager.getEvents()) {
            e.TextMessage(event);
        }
    }

    public void executeClientMove(ClientMovedEvent event) {
        for (Event e : EventManager.getEvents()) {
            e.ClientMove(event);
        }
    }

    public void executeChannelCreate(ChannelCreateEvent event) {
        for (Event e : EventManager.getEvents()) {
            e.ChannelCreate(event);
        }
    }

    public void JoinEvent(ClientJoinEvent event) {

    }

    public void ClientLeave(ClientLeaveEvent event) {

    }

    public void TextMessage(TextMessageEvent event) {

    }

    public void ClientMove(ClientMovedEvent event) {

    }

    public void ChannelCreate(ChannelCreateEvent event) {

    }
}
