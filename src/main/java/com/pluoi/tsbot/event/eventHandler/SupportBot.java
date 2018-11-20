package com.pluoi.tsbot.event.eventHandler;

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
    public static ArrayList<String> afk = new ArrayList<>();
    public static ArrayList<Integer> rentingMusic = new ArrayList<>();
    public static HashMap<Integer, String> sup = new HashMap<>();
    Timer timer = new Timer();
    private TS3Api api = TeamSpeakBot.api;
    private HashMap<Integer, TimerTask> timers = new HashMap<>();

    private String removefromgroup = TeamSpeakBot.getConfig().getString("supportbot.message.removed");
    private String nosups = TeamSpeakBot.getConfig().getString("supportbot.message.nosups");
    private String atleastonesup = TeamSpeakBot.getConfig().getString("supportbot.message.atleastonesup");
    private String supportend = TeamSpeakBot.getConfig().getString("supportbot.message.supportend");
    private String didntunterstand = TeamSpeakBot.getConfig().getString("supportbot.message.didntunderstand");
    private String noFreeMusicBots = TeamSpeakBot.getConfig().getString("supportbot.message.nofreemusicbot");
    private int waitingroom = TeamSpeakBot.getConfig().getInt("supportbot.waitingroom");
    private int endroom = TeamSpeakBot.getConfig().getInt("supportbot.endroom");
    private int maxafktime = TeamSpeakBot.getConfig().getInt("supportbot.maxafktime");
    private int supgroup = TeamSpeakBot.getConfig().getInt("supportbot.group");
    private int maxwaitingtime = TeamSpeakBot.getConfig().getInt("supportbot.maxwaitingtime");
    private int supportchannel = TeamSpeakBot.getConfig().getInt("supportbot.supportchannel");
    private int maxmsglenght = TeamSpeakBot.getConfig().getInt("supportbot.maxmsglenght");
    private List<String> joinfunctions = (List<String>) TeamSpeakBot.getConfig().getList("supportbot.function.join");
    private List<String> afkfunctions = (List<String>) TeamSpeakBot.getConfig().getList("supportbot.function.afk");

    @Override
    public void ClientMove(ClientMovedEvent event) {
        int clientid = event.getClientId();
        if (event.getTargetChannelId() == supportchannel) {
            for (String functions : joinfunctions) {
                parseAndExecute(functions, clientid, api.getClientInfo(clientid));
            }
            TimerTask t = new TimerTask() {
                @Override
                public void run() {
                    if (api.getClientInfo(event.getClientId()) != null) {
                        if (api.getClientInfo(event.getClientId()).getChannelId() == supportchannel) {
                            for (String functions : afkfunctions) {
                                parseAndExecute(functions, clientid, api.getClientInfo(clientid));
                            }
                        }
                    }
                }
            };
            timers.put(event.getClientId(), t);
            timer.schedule(timers.get(event.getClientId()), maxwaitingtime);
        } else {
            if (timers.containsKey(event.getClientId())) {
                timers.get(event.getClientId()).cancel();
                timers.remove(event.getClientId());
            }
        }
    }

    @Override
    public void TextMessage(TextMessageEvent event) {
        // Only react to channel messages not sent by the query itself
        if (event.getTargetMode() == TextMessageTargetMode.CLIENT) {
            String message = event.getMessage().toLowerCase();
            message = message.replace(" ", "");
            int iid = event.getInvokerId();
            if (api.getClientInfo(iid).getChannelId() != supportchannel) {
                return; // Nicht im Support Warteraum
            }
            if (message.length() < maxmsglenght) {
                ClientInfo ci = api.getClientInfo(event.getInvokerId());
                Logger.chat(ci.getNickname() + " -> " + message);
                Boolean enabled = TeamSpeakBot.getConfig().getBoolean("supportbot.messages." + message + ".enabled");
                if (enabled == null || enabled == false) {
                    msg(iid, didntunterstand);
                    return;
                }
                List<String> functions = (List<String>) TeamSpeakBot.getConfig().getList("supportbot.messages." + message + ".functions");
                for (String tempfunc : functions) {
                    parseAndExecute(tempfunc, iid, ci);
                }
            }
            if (timers.containsKey(iid)) {
                timers.get(iid).cancel();
                timers.remove(iid);
            }
        }
    }

    private void supportReady(int id, String message) {
        int supCount = 0;
        message = message.replace("%name%", "[URL=" + api.getClientInfo(id).getClientURI() + "]" + api.getClientInfo(id).getNickname() + "[/URL]");
        message = message.replace("%category%", sup.containsKey(id) ? sup.get(id) : "->");
        Logger.debug("SupporterGroupEvent runned");
        for (Client i : api.getClients()) {
            ClientInfo info = api.getClientInfo(i.getId());
            int dbId = i.getDatabaseId();
            if (info == null || info.isServerQueryClient()) {
                continue;
            }
            if (info.isInServerGroup(supgroup)) {
                if ((info.getIdleTime() > maxafktime) || (info.isAway())) {
                    api.removeClientFromServerGroup(supgroup, dbId);
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
        api.moveClient(id, channel);
    }

    private void msg(int id, String msg) {
        api.sendPrivateMessage(id, msg.replace("%br%", "\n"));
    }

    private void parseAndExecute(String function, int id, ClientInfo clientInfo) {
        Logger.debug(function);
        String functionName = function.split("\\(\\{")[0];
        Matcher m = Pattern.compile("\\(\\{(.*)}\\)").matcher(function);
        while (m.find()) {
            //Logger.debug("while1");
            //Logger.debug(String.valueOf(m.group(1)));

            String args[] = m.group(1).split(";");
            Logger.debug(functionName);
            Logger.debug(String.valueOf(args.length));
            switch (functionName) {
                case "msg":
                    msg(id, args[0]);
                    break;
                case "poke":
                    api.pokeClient(id, args[0]);
                    break;
                case "kickchannel":
                    api.kickClientFromChannel(args[0], id);
                    break;
                case "kickserver":
                    api.kickClientFromServer(args[0], id);
                    break;
                case "endsupport":
                    endSupport(id);
                    break;
                case "addgroup":
                    if (!clientInfo.isInServerGroup(Integer.parseInt(args[0]))) {
                        api.addClientToServerGroup(Integer.parseInt(args[0]), clientInfo.getDatabaseId());
                    }
                    break;
                case "removegroup":
                    if (clientInfo.isInServerGroup(Integer.parseInt(args[0]))) {
                        api.removeClientFromServerGroup(Integer.parseInt(args[0]), clientInfo.getDatabaseId());
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
                        if (args[1].contains("|,|")) {
                            String func[] = args[1].split("\\|,\\|");
                            String[] funcs = func;
                            for (String tempfunc : funcs) {
                                parseAndExecute(tempfunc.replace("[{", "({").replace("}]", "})"), id, clientInfo);
                            }
                        } else {
                            parseAndExecute(args[1].replace("[{", "({").replace("}]", "})"), id, clientInfo);
                        }
                    } else {
                        if (args[2].contains("|,|")) {
                            String func[] = args[2].split("\\|,\\|");
                            String[] funcs = func;
                            for (String tempfunc : funcs) {
                                parseAndExecute(tempfunc.replace("[{", "({").replace("}]", "})"), id, clientInfo);
                            }
                        } else {
                            parseAndExecute(args[2].replace("[{", "({").replace("}]", "})"), id, clientInfo);
                        }
                    }
                    break;
                case "hasgroup":
                    if (clientInfo.isInServerGroup(Integer.parseInt(args[0]))) {
                        if (args[1].contains("|,|")) {
                            String func[] = args[1].split("\\|,\\|");
                            String[] funcs = func;
                            for (String tempfunc : funcs) {
                                parseAndExecute(tempfunc.replace("[{", "({").replace("}]", "})"), id, clientInfo);
                            }
                        } else {
                            parseAndExecute(args[1].replace("[{", "({").replace("}]", "})"), id, clientInfo);
                        }
                    } else {
                        if (args[2].contains("|,|")) {
                            String func[] = args[2].split("\\|,\\|");
                            String[] funcs = func;
                            for (String tempfunc : funcs) {
                                parseAndExecute(tempfunc.replace("[{", "({").replace("}]", "})"), id, clientInfo);
                            }
                        } else {
                            parseAndExecute(args[2].replace("[{", "({").replace("}]", "})"), id, clientInfo);
                        }
                    }
                    break;
            }
        }
    }
}
