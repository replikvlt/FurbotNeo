
package com.kuborros.FurBotNeo.commands.MusicCommands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

@CommandInfo(
        name = "MusicInfo",
        description = "Returns information on currently played track."
)
@Author("Kuborros")
public class MusicInfoCmd extends MusicCommand{
    
    public MusicInfoCmd()
    {
        this.name = "music";
        this.aliases = new String[]{"current","musicinfo"};
        this.help = "Shows info about current song";
        this.guildOnly = true;        
        this.category = new Category("Music"); 
}
    @Override
    public void doCommand(CommandEvent event){

        if (!hasPlayer(guild) || getPlayer(guild).getPlayingTrack() == null) {
            event.getTextChannel().sendMessage(NOTE + "No music currently playing!").queue(); 
        } else {
            AudioTrack track = getPlayer(guild).getPlayingTrack();
            AudioTrackInfo info = track.getInfo();
            EmbedBuilder eb = new EmbedBuilder();
                            eb
                                    .setColor(Color.CYAN)
                                    .setDescription(":musical_note:   **Current Track Info**")
                                    .addField(":cd:  Title", info.title, false)
                                    .addField(":stopwatch:  Duration", "`[ " + getTimestamp(track.getPosition()) + " / " + getTimestamp(track.getInfo().length) + " ]`", false)
                                    .addField(":microphone:  Channel / Author", info.author, false);
            event.reply(eb.build());
            }
    }
        
}
