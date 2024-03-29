
package com.kuborros.FurBotNeo.commands.PicCommands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import com.jagrosh.jdautilities.menu.Slideshow;
import com.kuborros.FurBotNeo.net.apis.DanApi;
import com.kuborros.FurBotNeo.net.apis.NoImgException;
import net.dv8tion.jda.api.Permission;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.kuborros.FurBotNeo.BotMain.db;

@CommandInfo(
        name = "Dan",
        description = "Searches for nsfw/sfw images on danbooru."
)
@Author("Kuborros")
public class DanCmd extends PicCommand {

    private final EventWaiter waiter;

    public DanCmd(EventWaiter waiter) {
        this.name = "dan";
        this.help = "Searches for _pictures_ on DanBooru";
        this.arguments = "<2 Tags>";
        this.guildOnly = true;
        this.ownerCommand = false;
        this.cooldown = 5;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MANAGE_EMOTES};
        this.category = new Category("ImageBoards");
        this.hidden = true;
        this.waiter = waiter;
        db.registerCommand(this.name);
    }

    @Override
    protected void doCommand(CommandEvent event) {
        Slideshow.Builder builder = new Slideshow.Builder();
        DanApi api;
        List<String> result;
        db.updateCommandStats(event.getAuthor().getId(), this.name);

        if (!event.getTextChannel().isNSFW()) {
            event.replyWarning("This command works only on NSFW channels! (For obvious reasons)");
            return;
        }

        builder.allowTextInput(false)
            .setBulkSkipNumber(5)
            .waitOnSinglePage(false)
            .setColor(Color.PINK)
            .setEventWaiter(waiter)
            .setText("")
            .setDescription("Danbooru")
                .setFinalAction(message -> message.clearReactions().queue())
            .setTimeout(5, TimeUnit.MINUTES);


        api = new DanApi("https://danbooru.donmai.us/posts.json?random=true&limit=100");


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
            } catch (IOException e) {
                event.replyError("Something went wrong! ```" + e.getLocalizedMessage() + "```");
            }
        builder.build().display(event.getTextChannel());
    }
}