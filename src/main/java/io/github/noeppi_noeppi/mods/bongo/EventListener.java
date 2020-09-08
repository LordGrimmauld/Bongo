package io.github.noeppi_noeppi.mods.bongo;

import io.github.noeppi_noeppi.mods.bongo.command.BongoCommands;
import io.github.noeppi_noeppi.mods.bongo.data.GameDef;
import io.github.noeppi_noeppi.mods.bongo.network.BongoNetwork;
import io.github.noeppi_noeppi.mods.bongo.task.Task;
import io.github.noeppi_noeppi.mods.bongo.task.TaskTypeAdvancement;
import io.github.noeppi_noeppi.mods.bongo.task.TaskTypeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import javax.annotation.Nonnull;
import java.io.IOException;

public class EventListener {

    private static Minecraft mc = null;

    @SubscribeEvent
    public void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        BongoNetwork.updateBongo(event.getPlayer());
        World world = event.getPlayer().getEntityWorld();
        if (!world.isRemote && world instanceof ServerWorld && event.getPlayer() instanceof ServerPlayerEntity) {
            Bongo bongo = Bongo.get(world);
            for (Task task : bongo.tasks()) {
                if (task != null)
                    task.syncToClient(world.getServer(), (ServerPlayerEntity) event.getPlayer());
            }
        }
    }

    @SubscribeEvent
    public void playerChangeDim(PlayerEvent.PlayerChangedDimensionEvent event) {
        BongoNetwork.updateBongo(event.getPlayer());
    }

    @SubscribeEvent
    public void advancementGrant(AdvancementEvent event) {
        World world = event.getPlayer().getEntityWorld();
        if (!world.isRemote) {
            Bongo.get(world).checkCompleted(TaskTypeAdvancement.INSTANCE, event.getPlayer(), event.getAdvancement().getId());
        }
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.getEntityWorld().isRemote && event.player.ticksExisted % 20 == 0) {
            Bongo bongo = Bongo.get(event.player.world);
            for (ItemStack stack : event.player.inventory.mainInventory) {
                if (!stack.isEmpty()) {
                    bongo.checkCompleted(TaskTypeItem.INSTANCE, event.player, stack);
                }
            }
            if (bongo.getTeam(event.player) != null) {
                event.player.getFoodStats().setFoodLevel(20);
            }
        }
    }

    public static void clientStart(IEventBus modEventBus) {
        mc = Minecraft.getInstance();
        IResourceManager resourceManager = mc.getResourceManager();
        if (resourceManager instanceof IReloadableResourceManager) {
            IReloadableResourceManager reloadableResourceManager = (IReloadableResourceManager) resourceManager;
            reloadableResourceManager.addReloadListener(new ReloadListener<Object>() {
                @Nonnull
                @Override
                protected Object prepare(@Nonnull IResourceManager resourceManager, @Nonnull IProfiler profiler) {
                    return new Object();
                }

                @Override
                protected void apply(@Nonnull Object unused, @Nonnull IResourceManager resourceManager, @Nonnull IProfiler profiler) {
                    try {
                        GameDef.loadGameDefs(resourceManager);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public void damage(LivingHurtEvent event) {
        if (!event.getEntityLiving().getEntityWorld().isRemote && event.getEntityLiving() instanceof PlayerEntity && !event.getSource().canHarmInCreative()) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            Bongo bongo = Bongo.get(player.getEntityWorld());
            if (bongo.running() && bongo.getTeam(player) != null)
                event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void addTooltip(ItemTooltipEvent event) {
        final ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || event.getPlayer() == null)
            return;
        Bongo bongo = Bongo.get(event.getPlayer().world);
        if (bongo.active() && bongo.tasks().stream().anyMatch(task -> {
            ItemStack test = task.getElement(TaskTypeItem.INSTANCE);
            return test != null && stack.isItemEqual(test);
        }))
            event.getToolTip().add(new StringTextComponent(TextFormatting.GOLD + I18n.format("bongo.tooltip.required")));
    }

    @SubscribeEvent
    public void serverLoad(FMLServerStartingEvent event) {
        BongoCommands.register(event.getCommandDispatcher());
    }
}
