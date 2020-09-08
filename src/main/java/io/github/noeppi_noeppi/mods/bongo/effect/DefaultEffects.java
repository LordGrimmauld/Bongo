package io.github.noeppi_noeppi.mods.bongo.effect;

import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.command.impl.AdvancementCommand;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;

public class DefaultEffects {

    public static void register() {

        StartingEffects.registerPlayerEffect((bongo, player) -> player.inventory.clear());
        StartingEffects.registerPlayerEffect((bongo, player) -> {
            //noinspection ConstantConditions
            AdvancementCommand.Action.REVOKE.applyToAdvancements(player, player.getServer().getAdvancementManager().getAllAdvancements());
        });

        TaskEffects.registerPlayerEffect((bongo, thePlayer, task) -> {
            Team team = bongo.getTeam(thePlayer);
            if (team != null) {
                ITextComponent tc = team.getName().appendSibling(new TranslationTextComponent("bongo.task.complete")).appendSibling(task.getContentName(thePlayer.getServerWorld().getServer()));
                thePlayer.getServerWorld().getServer().getPlayerList().getPlayers().forEach(player -> {
                    player.sendMessage(tc);
                    if (team.hasPlayer(player)) {
                        player.connection.sendPacket(new SPlaySoundEffectPacket(SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.MASTER, player.getPosX(), player.getPosY(), player.getPosZ(), 0.5f, 1));
                    }
                });
            }
        });

        WinEffects.registerWorldEffect((bongo, world, team) -> {
            ITextComponent tc = team.getName().appendSibling(new TranslationTextComponent("bongo.win"));
            ITextComponent tcc = team.getName().appendSibling(new TranslationTextComponent("bongo.winplayers"));

            world.getServer().getPlayerList().getPlayers().forEach(player -> {
                if (team.hasPlayer(player)) {
                    tcc.appendSibling(new StringTextComponent(" "));
                    ITextComponent pname = player.getDisplayName().deepCopy();
                    pname.applyTextStyle(TextFormatting.RESET).applyTextStyle(TextFormatting.UNDERLINE);
                    pname.setStyle(pname.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp @p " + player.getPosX() + " " + player.getPosY() + " " + player.getPosZ())));
                    tcc.appendSibling(pname);
                }
            });

            world.getServer().getPlayerList().getPlayers().forEach(player -> {
                player.sendMessage(tcc);
                player.connection.sendPacket(new STitlePacket(STitlePacket.Type.TITLE, tc, 10, 60, 10));
                player.connection.sendPacket(new SPlaySoundEffectPacket(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, player.getPosX(), player.getPosY(), player.getPosZ(), 1.2f, 1));
            });
        });
    }
}
