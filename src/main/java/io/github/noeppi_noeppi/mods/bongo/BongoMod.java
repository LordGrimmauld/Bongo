package io.github.noeppi_noeppi.mods.bongo;

import io.github.noeppi_noeppi.mods.bongo.command.BongoCommands;
import io.github.noeppi_noeppi.mods.bongo.command.arg.GameDefArgument;
import io.github.noeppi_noeppi.mods.bongo.command.arg.UppercaseEnumArgument;
import io.github.noeppi_noeppi.mods.bongo.config.ClientConfig;
import io.github.noeppi_noeppi.mods.bongo.effect.DefaultEffects;
import io.github.noeppi_noeppi.mods.bongo.network.BongoNetwork;
import io.github.noeppi_noeppi.mods.bongo.render.RenderOverlay;
import io.github.noeppi_noeppi.mods.bongo.task.*;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BongoMod.MODID)
public class BongoMod {

    public static final String MODID = "bongo";
    public static final Logger LOGGER = LogManager.getLogger();

    public BongoMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(new EventListener());
        DistExecutor.runWhenOn(Dist.CLIENT, ()->()-> EventListener.clientStart(modEventBus));
    }

    private void setup(FMLCommonSetupEvent event) {
        BongoNetwork.registerPackets();

        TaskTypes.registerType(TaskTypeEmpty.INSTANCE);
        TaskTypes.registerType(TaskTypeItem.INSTANCE);
        TaskTypes.registerType(TaskTypeAdvancement.INSTANCE);
        TaskTypes.registerType(TaskTypeEntity.INSTANCE);

        DefaultEffects.register();

        Util.registerGenericCommandArgument(MODID + "_upperenum", UppercaseEnumArgument.class, new UppercaseEnumArgument.Serializer());
        ArgumentTypes.register(MODID + "_bongogame", GameDefArgument.class, new GameDefArgument.Serializer());
    }

    private void clientSetup(FMLClientSetupEvent event) {
        Keybinds.init();
        MinecraftForge.EVENT_BUS.register(new RenderOverlay());
    }
}
