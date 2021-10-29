package com.pluoi.tsbot.event.eventhandler;

import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.command.Command;
import com.pluoi.tsbot.event.Event;

import java.util.List;

public class Commands extends Event {
    private final int mod = TeamSpeakBot.getConfig().getInt("function.event.commands.modgroup");
    private final int admin = TeamSpeakBot.getConfig().getInt("function.event.commands.admingroup");
    private final List<String> modcommands = (List<String>) TeamSpeakBot.getConfig().getList("function.event.commands.modcommands");

    @Override
    public void TextMessage(TextMessageEvent event) {
        int id = event.getInvokerId();
        String msg = event.getMessage();
        String cmd = msg.split(" ")[0];
        if (TeamSpeakBot.api.getClientInfo(id).isInServerGroup(admin)
            || (TeamSpeakBot.api.getClientInfo(id).isInServerGroup(mod) && modcommands.contains(cmd))
        ) {
            runCommand(id, msg, cmd);
        }
    }

    private void runCommand(int id, String msg, String cmd) {
        for (Command command : TeamSpeakBot.getCommandManager().getCommands()) {
            if (command.getName().equalsIgnoreCase(cmd)) {
                msg = msg.replace(cmd + " ", "");
                logger.debug(msg);
                String[] args = msg.split(" ");
                if (args.length < 1) {
                    command.execute(new String[]{}, id);
                } else {
                    command.execute(args, id);
                }
            }
        }
    }
}
