package com.pluoi.tsbot.command.commands;

import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;

import java.util.HashMap;

public class tempToPerm extends com.pluoi.tsbot.command.Command {
    public tempToPerm() {
        super("tempToPerm", "Changes all Temp-Channels to Perm-Channels", "tempToPerm");
    }

    @Override
    public void execute(String[] args, int id) {
        HashMap<ChannelProperty, String> temp = new HashMap<>();
        HashMap<ChannelProperty, String> channels = new HashMap<>();
        temp.put(ChannelProperty.CHANNEL_FLAG_PERMANENT, "1");
        channels.put(ChannelProperty.CHANNEL_FLAG_PERMANENT, "true");
        for (Channel channel : TeamSpeakBot.api.getChannels()) {
            if (channel.isEmpty()) {
                if (channel.isPermanent() == false && channel.isSemiPermanent() == false) {
                    Logger.write("Edited channel " + channel.getName(), id);
                    TeamSpeakBot.api.editChannel(id, temp);
                    TeamSpeakBot.api.editChannel(id, channels);
                }
            }
        }
    }
}
