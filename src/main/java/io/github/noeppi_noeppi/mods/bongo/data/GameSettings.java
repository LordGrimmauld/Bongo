package io.github.noeppi_noeppi.mods.bongo.data;

import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;

public class GameSettings {

    public static final GameSettings DEFAULT = new GameSettings(new ResourceLocation(BongoMod.getInstance().modid, "default"), new CompoundNBT());
    
    public static final Map<ResourceLocation, GameSettings> GAME_SETTINGS = new HashMap<>();
    static {
        GAME_SETTINGS.put(DEFAULT.id, DEFAULT);
    }

    public final ResourceLocation id;
    private final CompoundNBT nbt;
    
    public final WinCondition winCondition;
    public final boolean invulnerable;
    public final boolean pvp;
    public final boolean friendlyFire;
    public final boolean lockTaskOnDeath;
    public final boolean consumeItems;
    public final int teleportsPerTeam;
    private final List<Pair<EquipmentSlotType, ItemStack>> startingInventory;

    public GameSettings(ResourceLocation id, CompoundNBT nbt) {
        this.id = id;
        
        if (nbt.contains("winCondition", Constants.NBT.TAG_STRING)) {
            winCondition = WinCondition.getWinOrDefault(nbt.getString("winCondition"));
        } else {
            winCondition = WinCondition.DEFAULT;
        }

        if (nbt.contains("invulnerable")) {
            invulnerable = nbt.getBoolean("invulnerable");
        } else {
            invulnerable = true;
        }

        if (nbt.contains("pvp")) {
            pvp = nbt.getBoolean("pvp");
        } else {
            pvp = false;
        }

        if (nbt.contains("friendlyFire")) {
            friendlyFire = nbt.getBoolean("friendlyFire");
        } else {
            friendlyFire = false;
        }

        if (nbt.contains("lockTaskOnDeath")) {
            lockTaskOnDeath = nbt.getBoolean("lockTaskOnDeath");
        } else {
            lockTaskOnDeath = false;
        }

        if (nbt.contains("consumeItems")) {
            consumeItems = nbt.getBoolean("consumeItems");
        } else {
            consumeItems = false;
        }

        if (nbt.contains("teleportsPerTeam")) {
            teleportsPerTeam = nbt.getInt("teleportsPerTeam");
        } else {
            teleportsPerTeam = 0;
        }

        startingInventory = new ArrayList<>();
        if (nbt.contains("startingInventory", Constants.NBT.TAG_LIST)) {
            Set<EquipmentSlotType> usedTypes = new HashSet<>();
            int slotsUsedInMainInventory = 0;
            ListNBT list = nbt.getList("startingInventory", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundNBT compound = list.getCompound(i);
                if (!compound.contains("Count")) {
                    compound.putByte("Count", (byte) 1);
                }
                EquipmentSlotType slotType = compound.contains("Slot", Constants.NBT.TAG_STRING) ? EquipmentSlotType.fromString(compound.getString("Slot")) : EquipmentSlotType.MAINHAND;
                if (slotType == EquipmentSlotType.MAINHAND) {
                    if (slotsUsedInMainInventory >= 36) {
                        throw new IllegalStateException("Too many starting items in main inventory. Not more than 36 are allowed.'");
                    } else {
                        slotsUsedInMainInventory += 1;
                    }
                } else {
                    if (usedTypes.contains(slotType)) {
                        throw new IllegalStateException("Slot type that is not 'mainhand' was used multiple times for starting inventory.'");
                    } else {
                        usedTypes.add(slotType);
                    }
                }
                startingInventory.add(Pair.of(slotType, ItemStack.read(compound)));
            }
        }

        this.nbt = new CompoundNBT();
        this.nbt.putString("winCondition", winCondition.id);
        this.nbt.putBoolean("invulnerable", invulnerable);
        this.nbt.putBoolean("pvp", pvp);
        this.nbt.putBoolean("friendlyFire", friendlyFire);
        this.nbt.putBoolean("lockTaskOnDeath", lockTaskOnDeath);
        this.nbt.putBoolean("consumeItems", consumeItems);
        this.nbt.putInt("teleportsPerTeam", teleportsPerTeam);
        ListNBT startingInventoryNBT = new ListNBT();
        startingInventory.forEach(stack -> {
            CompoundNBT compound = stack.getRight().write(new CompoundNBT());
            compound.putString("Slot", stack.getLeft().getName());
            startingInventoryNBT.add(compound);
        });
        this.nbt.put("startingInventory", startingInventoryNBT);
    }

    public CompoundNBT getTag() {
        return nbt;
    }
    
    public void fillStartingInventory(PlayerEntity player) {
        PlayerInventory inventory = player.inventory;
        inventory.clear();
        for (Pair<EquipmentSlotType, ItemStack> entry : startingInventory) {
            if (entry.getLeft() == EquipmentSlotType.MAINHAND) {
                inventory.addItemStackToInventory(entry.getRight().copy());
            } else {
                player.setItemStackToSlot(entry.getLeft(), entry.getRight().copy());
            }
        }
    }
    
    public static void loadGameSettings(IResourceManager rm) throws IOException {
        GameDef.loadData(rm, "bingo_settings", GAME_SETTINGS, GameSettings::new);
        GAME_SETTINGS.put(DEFAULT.id, DEFAULT);
    }
}
