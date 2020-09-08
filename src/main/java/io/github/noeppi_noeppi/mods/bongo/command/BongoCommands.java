package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.noeppi_noeppi.mods.bongo.command.arg.GameDefArgument;
import io.github.noeppi_noeppi.mods.bongo.command.arg.UppercaseEnumArgument;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.item.DyeColor;

public class BongoCommands {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("bp").executes(new BackPackCommand()));

        dispatcher.register(Commands.literal("bingo").then(
                Commands.literal("backpack").executes(new BackPackCommand())
        ).then(
                Commands.literal("join").then(Commands.argument("team", UppercaseEnumArgument.enumArgument(DyeColor.class)).executes(new JoinCommand()))
        ).then(
                Commands.literal("leave").executes(new LeaveCommand())
        ).then(
                Commands.literal("create").requires(cs -> cs.hasPermissionLevel(2)).then(Commands.argument("pattern", GameDefArgument.gameDef()).executes(new CreateCommand()))
        ).then(
                Commands.literal("start").executes(new StartCommand())
        ).then(
                Commands.literal("stop").executes(new StopCommand())
        ).then(
                Commands.literal("spread").then(Commands.argument("amount", IntegerArgumentType.integer(1, 16)).executes(new SpreadCommand()))
        ).then(
                Commands.literal("teams").executes(new TeamsCommand())
        ));
    }
}
