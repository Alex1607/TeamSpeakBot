package com.pluoi.tsbot.command.commands;

import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.command.Command;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class createRandomChannels extends Command {
    private int defaultchannel = TeamSpeakBot.getConfig().getInt("settings.defaultchannel");
    private int time = TeamSpeakBot.getConfig().getInt("randomchannels.temptime");

    public createRandomChannels() {
        super("createRandomChannels", "Creates some random-channels", "createrandomchannels");
    }

    @Override
    public void execute(String[] args, int id) {
        if (args.length == 1) {
            try {
                if (Integer.parseInt(args[0]) > 0) {
                    Logger.write("Creating " + args[0] + " random-channels", id);
                    for (int i = 0; i < Integer.parseInt(args[0]); i++) {
                        final Map<ChannelProperty, String> properties = new HashMap<>();
                        properties.put(ChannelProperty.CHANNEL_FLAG_TEMPORARY, String.valueOf(time));
                        TeamSpeakBot.api.createChannel(String.valueOf(new Random().nextDouble()), properties);
                    }
                    TeamSpeakBot.api.moveQuery(defaultchannel);
                } else {
                    Logger.write("Please use createrandomchannels [count]", id);
                }
            } catch (Exception e) {
            }
        }
    }
}
