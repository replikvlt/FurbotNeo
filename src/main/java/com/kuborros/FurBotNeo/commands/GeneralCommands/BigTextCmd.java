package com.kuborros.FurBotNeo.commands.GeneralCommands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;

import java.util.Arrays;
import java.util.List;

@CommandInfo(
        name = "BigText",
        description = "Writes entered text using big letter emojis."
)
@Author("Kuborros")

public class BigTextCmd extends GeneralCommand {
    private final StringBuilder result = new StringBuilder();
    private static final List<String> alphabet = Arrays.asList("abcdefghijklmnopqrstuvwxyz".split(""));

    public BigTextCmd() {
        this.name = "bigtext";
        this.help = "Makes your words huge!";
        this.arguments = "<Words>";
        this.guildOnly = true;
        this.category = new Command.Category("Basic");
    }

    @Override
    protected void doCommand(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.replyError("Put something in to biggyfy!");
            return;
        }
        String[] text = event.getArgs().toLowerCase().split("");
        for (String letter : text) {
            if (letter.equalsIgnoreCase(" ")) result.append(":black_large_square:");
            else if (letter.equalsIgnoreCase(".")) result.append(":black_small_square:");
            else if (letter.equalsIgnoreCase("!")) result.append(":exclamation:");
            else if (letter.equalsIgnoreCase("?")) result.append(":question:");
            else if (letter.equalsIgnoreCase("-") || letter.equalsIgnoreCase("_")) result.append(":heavy_minus_sign:");
            else if (alphabet.contains(letter)) {
                result.append(":regional_indicator_").append(letter).append(":");
            }
        }
        event.reply(result.toString());
        result.delete(0, result.length());
    }
}