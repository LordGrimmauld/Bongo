package io.github.noeppi_noeppi.mods.bongo.network;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;

public class BongoNetwork {

    private BongoNetwork() {

    }

    private static final String PROTOCOL_VERSION = "1";
    private static int discriminator = 0;
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BongoMod.MODID, "netchannel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerPackets() {
        register(new BongoUpdateHandler(), NetworkDirection.PLAY_TO_CLIENT);
        register(new AdvancementInfoUpdateHandler(), NetworkDirection.PLAY_TO_CLIENT);
    }

    private static <T> void register(PacketHandler<T> handler, NetworkDirection direction) {
        INSTANCE.registerMessage(discriminator++, handler.messageClass(), handler::encode, handler::decode, handler::handle, Optional.of(direction));
    }

    public static void updateBongo(World world) {
        if (!world.isRemote) {
            INSTANCE.send(PacketDistributor.DIMENSION.with(() -> world.getDimension().getType()), new BongoUpdateHandler.BongoUpdateMessage(Bongo.get(world)));
        }
    }

    public static void updateBongo(PlayerEntity player) {
        if (!player.getEntityWorld().isRemote) {
            INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new BongoUpdateHandler.BongoUpdateMessage(Bongo.get(player.getEntityWorld())));
        }
    }

    public static void updateBongo(World world, BongoMessageType messageType) {
        if (!world.isRemote) {
            INSTANCE.send(PacketDistributor.DIMENSION.with(() -> world.getDimension().getType()), new BongoUpdateHandler.BongoUpdateMessage(Bongo.get(world), messageType));
        }
    }

    public static void updateBongo(PlayerEntity player, BongoMessageType messageType) {
        if (!player.getEntityWorld().isRemote) {
            INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new BongoUpdateHandler.BongoUpdateMessage(Bongo.get(player.getEntityWorld()), messageType));
        }
    }

    public static void syncAdvancement(Advancement advancement) {
        if (advancement.getDisplay() != null) {
            INSTANCE.send(PacketDistributor.ALL.noArg(), new AdvancementInfoUpdateHandler.AdvancementInfoUpdateMessage(advancement.getId(), advancement.getDisplay().icon, advancement.getDisplay().getTitle()));
        }
    }

    public static void syncAdvancementTo(Advancement advancement, ServerPlayerEntity playerEntity) {
        if (advancement.getDisplay() != null) {
            INSTANCE.send(PacketDistributor.PLAYER.with(() -> playerEntity), new AdvancementInfoUpdateHandler.AdvancementInfoUpdateMessage(advancement.getId(), advancement.getDisplay().icon, advancement.getDisplay().getTitle()));
        }
    }
}
