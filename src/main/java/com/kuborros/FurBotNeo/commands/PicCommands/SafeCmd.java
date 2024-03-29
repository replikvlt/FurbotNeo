
package com.kuborros.FurBotNeo.commands.PicCommands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import com.jagrosh.jdautilities.menu.Slideshow;
import com.kuborros.FurBotNeo.net.apis.GelEngine;
import com.kuborros.FurBotNeo.net.apis.NoImgException;
import net.dv8tion.jda.api.Permission;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.kuborros.FurBotNeo.BotMain.db;

@CommandInfo(
        name = "Safe",
        description = "Searches for sfw images on safebooru."
)
@Author("Kuborros")
public class SafeCmd extends PicCommand {

    private final EventWaiter waiter;
    
    public SafeCmd(EventWaiter waiter){
        this.name = "safe";
        this.help = "Searches for pics on SafeBooru";
        this.arguments = "<Tags>";
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = 5;
        this.waiter = waiter;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.category = new Category("ImageBoards");  
        db.registerCommand(this.name);
    }
    
    @Override
    protected void doCommand(CommandEvent event) {
        GelEngine api;
        List<String> result;
        Slideshow.Builder builder = new Slideshow.Builder();
        db.updateCommandStats(event.getAuthor().getId(), this.name);


        builder.allowTextInput(false)
                .setBulkSkipNumber(5)
                .waitOnSinglePage(false)
                .setColor(Color.PINK)
                .setEventWaiter(waiter)
                .setText("")
                .setDescription("Safebooru")
                .setFinalAction(message -> message.clearReactions().queue())
                .setTimeout(5, TimeUnit.MINUTES);


        api = new GelEngine("https://safebooru.org/index.php?page=dapi&s=post&q=index&limit=100");

        try {
            if (!event.getArgs().isEmpty()) {
                result = api.getImageSetTags(event.getArgs());
                } else {
                result = api.getImageSetRandom();
                }
                    builder.setUrls(result.toArray(new String[0]));
                } catch (NoImgException e) {
                    event.reply("No results found!");                    
                    return;
                } catch (ParserConfigurationException | IOException | SAXException e) {
                    event.replyError("Something went wrong! ```\\n\" " + e.getLocalizedMessage() + "\"\\n```\"");
                    return;
                }
                builder.build().display(event.getTextChannel());
    }
    
}
