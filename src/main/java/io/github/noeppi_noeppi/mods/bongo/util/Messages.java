package io.github.noeppi_noeppi.mods.bongo.util;

import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class Messages {

    public static void onJoin(World world, PlayerEntity player, Team team) {
        player.sendMessage(new TranslationTextComponent("bongo.cmd.team.joined").appendSibling(team.getName()));
        Util.broadcastExcept(world, player, (player.getDisplayName()).appendSibling(new TranslationTextComponent("bongo.cmd.team.joinedother").appendSibling(team.getName())));
    }

    public static void onLeave(World world, PlayerEntity player, Team team) {
        player.sendMessage(new TranslationTextComponent("bongo.cmd.team.left").appendSibling(team.getName()));
        Util.broadcastExcept(world, player, (player.getDisplayName()).appendSibling(new TranslationTextComponent("bongo.cmd.team.leftother").appendSibling(team.getName())));
    }
}
