
package com.kuborros.FurBotNeo.utils.msg;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;


public class ChannelFinder {

    private final Guild guild;
    
    public ChannelFinder(Guild guild) {
        this.guild = guild;
    }
    
    public TextChannel FindBotChat() {
        List<TextChannel> channels = guild.getTextChannels();
        for(TextChannel channel : channels){
            if (channel.getName().contains("bot"))
                return channel;
        }
        return guild.getDefaultChannel();
    }
}
