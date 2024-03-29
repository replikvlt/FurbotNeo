/*
 * Here comes the text of your license
 * Each line should be prefixed with  *
 */
package com.kuborros.FurBotNeo.commands.AdminCommands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@CommandInfo(
        name = "Eval",
        description = "Runs code using nashorn engine."
)
@Author("Kuborros")
public class EvalCommand extends AdminCommand {

    private final ScriptEngine engine;


    public EvalCommand() {
        this.name = "eval";
        this.help = "Evaluates nashorn code";
        this.ownerCommand = true;
        this.guildOnly = true;
        this.arguments = "<code>";
        this.category = new Category("Moderation");
        this.hidden = true;

        engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            engine.eval("var imports = new JavaImporter(" +
                    "java.io," +
                    "java.lang," +
                    "java.util," +
                    "Packages.net.dv8tion.jda.api," +
                    "Packages.net.dv8tion.jda.api.entities," +
                    "Packages.net.dv8tion.jda.api.entities.impl," +
                    "Packages.net.dv8tion.jda.api.managers," +
                    "Packages.net.dv8tion.jda.api.managers.impl," +
                    "Packages.net.dv8tion.jda.api.utils);");
        } catch (ScriptException e) {
            LOG.error("Eval command error: ", e);
        }
    }

    @Override
    protected void doCommand(CommandEvent event) {
        engine.put("event", event);
        engine.put("jda", event.getJDA());
        engine.put("guild", event.getGuild());
        engine.put("channel", event.getChannel());
        try {
            event.reply(event.getClient().getSuccess() + " Evaluated Successfully:\n```\n" + engine.eval(event.getArgs()) + " ```");
        } catch (ScriptException e) {
            event.reply(event.getClient().getError() + " An exception was thrown:\n```\n" + e + " ```");
        }
    }

}