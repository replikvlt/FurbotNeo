/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package com.kuborros.FurBotNeo.commands.AdminCommands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Kuborros
 */

public class StatsCommand extends Command {

    private final OffsetDateTime start = OffsetDateTime.now();
    public StatsCommand()
    {
        this.name = "stats";
        this.help = "Shows some statistics on the bot";
        this.ownerCommand = true;
        this.guildOnly = false;
        this.category = new Category("Moderation");
        this.userPermissions = new Permission[]{Permission.KICK_MEMBERS};
    }
    
    @Override
    protected void execute(CommandEvent event) {
        long totalMb = Runtime.getRuntime().totalMemory()/(1024*1024);
        long usedMb = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1024*1024);
        event.reply("**"+event.getSelfUser().getName()+"** statistics:"
                + "\nLast Startup: "+start.format(DateTimeFormatter.RFC_1123_DATE_TIME)
                + "\nGuilds: "+event.getJDA().getGuilds().size()
                + "\nMemory: "+usedMb+"Mb / "+totalMb+"Mb"
                + "\nCurrent ping: " + event.getJDA().getPing()
                + "\nResponse Total: "+event.getJDA().getResponseTotal());
    }
    
}
