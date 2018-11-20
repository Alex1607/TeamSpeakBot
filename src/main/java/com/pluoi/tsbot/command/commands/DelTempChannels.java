package com.pluoi.tsbot.command.commands;

import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.pluoi.tsbot.Logger;
import com.pluoi.tsbot.TeamSpeakBot;
import com.pluoi.tsbot.command.Command;

public class DelTempChannels extends Command {
    private Logger logger = new Logger();
    public DelTempChannels() {
        super("DelTempChannels", "Removes all temp-channels which are not in use", "deltempchannels");
    }

    @Override
    public void execute(String[] args, int id) {
        for (Channel channel : TeamSpeakBot.api.getChannels()) {
            if (!channel.isEmpty() || channel.isPermanent() || channel.isSemiPermanent()) {
                continue;
            }
            logger.write("Removed channel " + channel.getName(), id);
            TeamSpeakBot.api.deleteChannel(channel.getId());
        }
    }
}
