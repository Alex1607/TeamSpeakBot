package com.pluoi.tsbot.event.eventhandler;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.event.Event;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SupportBot extends Event {
    public static final List<String> afk = new ArrayList<>();
    public static final List<Integer> rentingMusic = new ArrayList<>();
    private final Map<Integer, String> sup = new HashMap<>();
    private final Timer timer = new Timer();
    private final Map<Integer, TimerTask> timers = new HashMap<>();

    private final String removefromgroup = TeamSpeakBot.getConfig().getString("supportbot.message.removed");
    private final String nosups = TeamSpeakBot.getConfig().getString("supportbot.message.nosups");
    private final String atleastonesup = TeamSpeakBot.getConfig().getString("supportbot.message.atleastonesup");
    private final String supportend = TeamSpeakBot.getConfig().getString("supportbot.message.supportend");
    private final String didntunterstand = TeamSpeakBot.getConfig().getString("supportbot.message.didntunderstand");
    private final String noFreeMusicBots = TeamSpeakBot.getConfig().getString("supportbot.message.nofreemusicbot");
    private final int waitingroom = TeamSpeakBot.getConfig().getInt("supportbot.waitingroom");
    private final int endroom = TeamSpeakBot.getConfig().getInt("supportbot.endroom");
    private final int maxafktime = TeamSpeakBot.getConfig().getInt("supportbot.maxafktime");
    private final int supgroup = TeamSpeakBot.getConfig().getInt("supportbot.group");
    private final int maxwaitingtime = TeamSpeakBot.getConfig().getInt("supportbot.maxwaitingtime");
    private final int supportchannel = TeamSpeakBot.getConfig().getInt("supportbot.supportchannel");
    private final int maxmsglenght = TeamSpeakBot.getConfig().getInt("supportbot.maxmsglenght");
    private final List<String> joinfunctions = (List<String>) TeamSpeakBot.getConfig().getList("supportbot.function.join");
    private final List<String> afkfunctions = (List<String>) TeamSpeakBot.getConfig().getList("supportbot.function.afk");

    @Override
    public void ClientMove(ClientMovedEvent event) {
        int clientid = event.getClientId();
        if (event.getTargetChannelId() == supportchannel) {
            for (String functions : joinfunctions) {
                parseAndExecute(functions, clientid, TeamSpeakBot.api.getClientInfo(clientid));
            }
            startIdleTimer(clientid);
        } else if (timers.containsKey(event.getClientId())) {
            timers.get(event.getClientId()).cancel();
            timers.remove(event.getClientId());
        }
    }

    private void startIdleTimer(int clientid) {
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                if (TeamSpeakBot.api.getClientInfo(clientid) == null
                    || TeamSpeakBot.api.getClientInfo(clientid).getChannelId() != supportchannel
                ) {
                    return;
                }
                for (String functions : afkfunctions) {
                    parseAndExecute(functions, clientid, TeamSpeakBot.api.getClientInfo(clientid));
                }
            }
        };
        timers.put(clientid, t);
        timer.schedule(timers.get(clientid), maxwaitingtime);
    }

    @Override
    public void TextMessage(TextMessageEvent event) {
        // Only react to channel messages not sent by the query itself
        if (event.getTargetMode() != TextMessageTargetMode.CLIENT) {
            return;
        }

        String message = event.getMessage().toLowerCase();
        message = message.replace(" ", "");
        int iid = event.getInvokerId();
        if (TeamSpeakBot.api.getClientInfo(iid).getChannelId() != supportchannel) {
            return; // Nicht im Support Warteraum
        }
        if (message.length() < maxmsglenght) {
            ClientInfo ci = TeamSpeakBot.api.getClientInfo(event.getInvokerId());
            logger.chat(ci.getNickname() + " -> " + message);
            boolean enabled = TeamSpeakBot.getConfig().getBoolean("supportbot.messages." + message + ".enabled");
            if (!enabled) {
                msg(iid, didntunterstand);
                return;
            }
            getFunctionsAndRun("supportbot.messages." + message + ".functions", iid, ci);
        }
        if (timers.containsKey(iid)) {
            timers.get(iid).cancel();
            timers.remove(iid);
        }
    }

    private void getFunctionsAndRun(String path, int invokerID, ClientInfo clientInfo) {
        List<String> functions = (List<String>) TeamSpeakBot.getConfig().getList(path);
        boolean returned = false;
        for (String tempfunc : functions) {
            if (returned) {
                continue;
            }
            returned = parseAndExecute(tempfunc, invokerID, clientInfo);
        }
    }

    private void supportReady(int id, String message) {
        int supCount = 0;
        message = message.replace("%name%", "[URL=" + TeamSpeakBot.api.getClientInfo(id).getClientURI() + "]" + TeamSpeakBot.api.getClientInfo(id).getNickname() + "[/URL]");
        message = message.replace("%category%", sup.getOrDefault(id, "->"));
        logger.debug("SupporterGroupEvent runned");
        for (Client i : TeamSpeakBot.api.getClients()) {
            ClientInfo info = TeamSpeakBot.api.getClientInfo(i.getId());
            int dbId = i.getDatabaseId();
            if (info == null || info.isServerQueryClient()) {
                continue;
            }
            if (info.isInServerGroup(supgroup)) {
                if ((info.getIdleTime() > maxafktime) || (info.isAway())) {
                    TeamSpeakBot.api.removeClientFromServerGroup(supgroup, dbId);
                    afk.add(i.getUniqueIdentifier());
                    msg(i.getId(), removefromgroup);
                } else {
                    msg(i.getId(), message);
                    supCount++;
                }
            }
        }
        if (supCount == 0) {
            msg(id, nosups);
        } else {
            msg(id, atleastonesup.replace("%count%", String.valueOf(supCount)));
        }
        move(id, waitingroom);
    }

    private void endSupport(int id) {
        msg(id, supportend);
        move(id, endroom);
        sup.remove(id);
    }

    private void move(int id, int channel) {
        TeamSpeakBot.api.moveClient(id, channel);
    }

    private void msg(int id, String msg) {
        TeamSpeakBot.api.sendPrivateMessage(id, msg.replace("%br%", "\n"));
    }

    private boolean parseAndExecute(String function, int id, ClientInfo clientInfo) {
        logger.debug(function);
        String functionName = function.split("\\(\\{")[0];
        Matcher m = Pattern.compile("\\(\\{(.*)}\\)").matcher(function);
        while (m.find()) {
            String args[] = m.group(1).split(";");
            logger.debug(functionName);
            logger.debug(String.valueOf(args.length));
            switch (functionName) {
                case "msg":
                    msg(id, args[0]);
                    break;
                case "poke":
                    TeamSpeakBot.api.pokeClient(id, args[0]);
                    break;
                case "kickchannel":
                    TeamSpeakBot.api.kickClientFromChannel(args[0], id);
                    break;
                case "kickserver":
                    TeamSpeakBot.api.kickClientFromServer(args[0], id);
                    break;
                case "endsupport":
                    endSupport(id);
                    break;
                case "addgroup":
                    if (!clientInfo.isInServerGroup(Integer.parseInt(args[0]))) {
                        TeamSpeakBot.api.addClientToServerGroup(Integer.parseInt(args[0]), clientInfo.getDatabaseId());
                    }
                    break;
                case "removegroup":
                    if (clientInfo.isInServerGroup(Integer.parseInt(args[0]))) {
                        TeamSpeakBot.api.removeClientFromServerGroup(Integer.parseInt(args[0]), clientInfo.getDatabaseId());
                    }
                    break;
                case "supportcategory":
                    sup.put(id, args[0]);
                    break;
                case "tohuman":
                    supportReady(id, args[0]);
                    break;
                case "donothing":
                    //TODO: Diese Funktion kann genutzt werden wenn mehrere cartegorymatchs untereinander ausgefürt werden sollen da diese bei zb der zweiten funktion nichts ausführen! ---> artegorymatch({Teamspeak;tohuman[{}];donothing[{}]})
                    //TODO: Darunter kann dann die nächste Abfrage kommen ohne das der Nutzer etwas von der darüber mitbekommen hat.
                    break;
                case "return":
                    return true;
                case "rentbot":
                    String channelName = TeamSpeakBot.api.getChannelInfo(25).getName(); // Schlafen Raum
                    int clientCount = TeamSpeakBot.api.getChannelByNameExact(channelName, false).getTotalClients();
                    if (clientCount > 0) {
                        rentingMusic.add(id);
                    } else {
                        msg(id, noFreeMusicBots);
                    }
                    break;
                case "cartegorymatch":
                    if (sup.get(id).equalsIgnoreCase(args[0]) && sup.containsKey(id)) {
                        executeFunctionTrue(id, clientInfo, args);
                    } else {
                        executeFunctionFalse(id, clientInfo, args);
                    }
                    break;
                case "hasgroup":
                    if (clientInfo.isInServerGroup(Integer.parseInt(args[0]))) {
                        executeFunctionTrue(id, clientInfo, args);
                    } else {
                        executeFunctionFalse(id, clientInfo, args);
                    }
                    break;
                case "function":
                    getFunctionsAndRun("supportbot.functions." + args[0], id, clientInfo);
                    break;
            }
        }
        return false;
    }

    private void executeFunctionTrue(int id, ClientInfo clientInfo, String[] args) {
        if (args[1].contains("|,|")) {
            String[] funcs = args[1].split("\\|,\\|");
            for (String tempfunc : funcs) {
                parseAndExecute(tempfunc.replace("[{", "({").replace("}]", "})"), id, clientInfo);
            }
        } else {
            parseAndExecute(args[1].replace("[{", "({").replace("}]", "})"), id, clientInfo);
        }
    }

    private void executeFunctionFalse(int id, ClientInfo clientInfo, String[] args) {
        if (args[2].contains("|,|")) {
            String[] funcs = args[2].split("\\|,\\|");
            for (String tempfunc : funcs) {
                parseAndExecute(tempfunc.replace("[{", "({").replace("}]", "})"), id, clientInfo);
            }
        } else {
            parseAndExecute(args[2].replace("[{", "({").replace("}]", "})"), id, clientInfo);
        }
    }
}
