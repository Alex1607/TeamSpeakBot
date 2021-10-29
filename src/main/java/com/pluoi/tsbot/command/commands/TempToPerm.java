package com.pluoi.tsbot.command.commands;

import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.pluoi.tsbot.TeamSpeakBot;

import java.util.EnumMap;
import java.util.Map;

public class TempToPerm extends com.pluoi.tsbot.command.Command {
    public TempToPerm() {
        super("tempToPerm", "Changes all Temp-Channels to Perm-Channels", "tempToPerm");
    }

    @Override
    public void execute(String[] args, int id) {
        Map<ChannelProperty, String> temp = new EnumMap<>(ChannelProperty.class);
        Map<ChannelProperty, String> channels = new EnumMap<>(ChannelProperty.class);
        temp.put(ChannelProperty.CHANNEL_FLAG_PERMANENT, "1");
        channels.put(ChannelProperty.CHANNEL_FLAG_PERMANENT, "true");
        for (Channel channel : TeamSpeakBot.api.getChannels()) {
            if (channel.isEmpty() && !channel.isPermanent() && !channel.isSemiPermanent()) {
                logger.write("Edited channel " + channel.getName(), id);
                TeamSpeakBot.api.editChannel(id, temp);
                TeamSpeakBot.api.editChannel(id, channels);
            }
        }
    }
}
