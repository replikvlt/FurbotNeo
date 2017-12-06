/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package com.kuborros.FurBotNeo.commands.GeneralCommands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Kuborros
 */
public class VoteCommand extends Command{
    
//private Logger LOG = LoggerFactory.getLogger("VoteCommand");    

        
        public VoteCommand()         
    {
        this.name = "vote";
        this.help = "Creates a vote";
        this.arguments = "<time> <topic>";
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = 60;
        this.cooldownScope = CooldownScope.GUILD;
        this.category = new Command.Category("Basic");
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args;
        if (event.getArgs().isEmpty()) {
            event.replyWarning("Votes need to contain <time> and <topic>!");
            event.getMessage().delete().queue();   
            return;
        }
        args = event.getArgs().split(" ");
        String val = args[0].toUpperCase().trim();
                        boolean min = false;
                        if(val.endsWith("M"))
                        {
                            min=true;
                            val=val.substring(0,val.length()-1).trim();
                        }
                        else if(val.endsWith("S"))
                        {
                            val=val.substring(0,val.length()-1).trim();
                        }
                        int seconds;
                        try {
                            seconds = (min?60:1)*Integer.parseInt(val);
                            if(seconds<10 || seconds>60*60*24){
                                event.replyWarning("Sorry! Votes need to be at least 10 seconds long, and can't be _too_ long.");
                                event.getMessage().delete().queue();                                
                            }
                            else {
                                String topic = event.getArgs().replaceFirst(args[0], "");
                                if(topic.length()>500){
                                    event.replyWarning("That topic is too long. Can you shorten it a bit?");
                                } else {
                                    Instant now = Instant.now();
                                    if(startVote(event.getTextChannel(), now, seconds, topic)){
                                        event.getMessage().delete().queue();
                                    }
                                    else {
                                        event.replyError("Uh oh. Something went wrong and I wasn't able to start the vote.");
                                        event.getMessage().delete().queue();
                                    }    
                                }
                            }
                        } catch (NumberFormatException ex) {
                            event.replyWarning("Hm. I can't seem to get a number from that.");
                        }
               
        
    }


    @SuppressWarnings("SameReturnValue")
private boolean startVote(TextChannel channel, Instant now, int seconds, String prize){
        MessageEmbed msg = new EmbedBuilder().setTitle("**Vote**").setDescription(prize).setTimestamp(now).setColor(Color.BLUE).addField("", "Vote will end in: " + secondsToTime(seconds) + "!", false).build();
        channel.sendMessage(msg).queue(m -> {
            m.addReaction("\u2705").queue();
            m.addReaction("\u274E").queue();
            Timer timer = new Timer("VoteTimer");
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    String mID = m.getId();
                    List<MessageReaction> ayy = channel.getMessageById(mID).complete().getReactions();
                    int check = ayy.get(0).getCount() - 1;
                    int cross = ayy.get(1).getCount() - 1;
                    Color col = (check >= cross ? Color.GREEN : Color.RED);
                    MessageEmbed msg = new EmbedBuilder().setTitle("**Vote results!**").setDescription(
                            "The results are: \n"
                                    + "\u2705 :  **" + check + "**\n"
                                    + "\u274E :  **" + cross + "**\n"
                    ).addField("", "Vote's topic was: \"" + prize + "\" !", false).setColor(col).build();
                    channel.sendMessage(msg).complete();
                    channel.getMessageById(mID).complete().delete().complete();
                    timer.cancel();
                }
            };

            timer.schedule(timerTask, TimeUnit.SECONDS.toMillis(seconds));
        });
        return true;    
}

    private static String secondsToTime(long timeseconds) {
        StringBuilder builder = new StringBuilder();
        int years = (int)(timeseconds / (60*60*24*365));
        if(years>0){
            builder.append("**").append(years).append("** years, ");
            timeseconds %= (60*60*24*365);
        }
        int weeks = (int)(timeseconds / (60*60*24*365));
        if(weeks>0){
            builder.append("**").append(weeks).append("** weeks, ");
            timeseconds %= (60*60*24*7);
        }
        int days = (int)(timeseconds / (60*60*24));
        if(days>0){
            builder.append("**").append(days).append("** days, ");
            timeseconds %= (60*60*24);
        }
        int hours = (int)(timeseconds / (60*60));
        if(hours>0){
            builder.append("**").append(hours).append("** hours, ");
            timeseconds %= (60*60);
        }
        int minutes = (int)(timeseconds / (60));
        if(minutes>0){
            builder.append("**").append(minutes).append("** minutes, ");
            timeseconds %= (60);
        }
        if(timeseconds>0)
            builder.append("**").append(timeseconds).append("** seconds");
        String str = builder.toString();
        if(str.endsWith(", "))
            str = str.substring(0,str.length()-2);
        if(str.isEmpty())
            str="**No time**";
        return str;
}        
}
