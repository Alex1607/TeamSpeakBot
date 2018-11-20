package com.pluoi.tsbot.event.eventhandler;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.event.Event;

import java.util.HashMap;
import java.util.Map;

public class TooManyIPs extends Event {
    private Map<String, Integer> ips = new HashMap<>();
    private Map<Integer, ClientInfo> clientinfo = new HashMap<>();
    private String message = TeamSpeakBot.getConfig().getString("iplimit.message");
    private int bantime = TeamSpeakBot.getConfig().getInt("iplimit.bantime");
    private int maxclients = TeamSpeakBot.getConfig().getInt("iplimit.maxclients");
    private Logger logger = new Logger();

    @Override
    public void JoinEvent(ClientJoinEvent event) {
        ClientInfo ci = TeamSpeakBot.api.getClientInfo(event.getClientId());

        clientinfo.put(event.getClientId(), ci);
        String ip = ci.getIp();

        if (ips.containsKey(ip)) {
            ips.put(ip, ips.get(ip) + 1);
            if (ips.get(ip) > maxclients) {
                TeamSpeakBot.api.banClient(event.getClientId(), bantime, message);
                logger.write("Too many users on the same IP. Banned: " + ci.getNickname(), -1);
            }
        } else {
            ips.put(ip, 1);
        }
        logger.write(ci.getNickname() + " joined!", -1);
    }

    @Override
    public void ClientLeave(ClientLeaveEvent event) {
        Integer id = event.getClientId();
        ClientInfo ci = clientinfo.get(id);
        String ip = ci.getIp();
        if (ips.containsKey(ip)) {
            if (ips.get(ip) > 1) {
                logger.write(ci.getNickname() + " quit! ( " + ips.get(ip) + " left )", -1);
                ips.put(ip, ips.get(ip) - 1);
            } else {
                logger.write(ci.getNickname() + " quit!", -1);
                ips.remove(ip);
            }
        }
        clientinfo.remove(id);
    }
}
