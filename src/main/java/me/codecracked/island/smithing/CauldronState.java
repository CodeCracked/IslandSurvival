package me.codecracked.island.smithing;

import me.codecracked.island.IslandPlugin;
import me.codecracked.island.scent.ScentManager;
import net.minecraft.server.v1_16_R3.EntityItem;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CauldronState
{
    private static Set<ItemStack> quenchableItems = new HashSet<>();
    static
    {
        quenchableItems.add(SmithingManager.USELESS_STEEL_PLATE);
        quenchableItems.add(SmithingManager.RUINED_STEEL_PLATE);
        quenchableItems.add(SmithingManager.COMPROMISED_STEEL_PLATE);
        quenchableItems.add(SmithingManager.WEAK_STEEL_PLATE);
        quenchableItems.add(SmithingManager.STEEL_PLATE);
        quenchableItems.add(SmithingManager.PERFECT_STEEL_PLATE);
    }

    private Player player;
    private Location cauldron;
    private ItemStack itemInCauldron;
    private int ticksLeft;
    private Levelled waterLevel;
    private BukkitRunnable tickHandler;

    private CauldronState(Player player, Location cauldron)
    {
        this.player = player;
        this.cauldron = cauldron;
        this.tickHandler = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                minigameTick();
            }
        };
    }
    public static CauldronState createCauldronStateForPlayer(Player player, Location cauldron)
    {
        CauldronState state = new CauldronState(player, cauldron);

        ItemStack playerHand = player.getInventory().getItemInMainHand();
        ItemStack singleItem = ScentManager.stripScentsFromItem(playerHand.clone());
        singleItem.setAmount(1);

        if (playerHand == null || playerHand.getAmount() == 0)
        {
            player.sendMessage("Hold an item that can be quenched!");
            return null;
        }

        Block block = cauldron.getBlock();
        if (!block.getType().equals(Material.CAULDRON))
        {
            player.sendMessage("INTERNAL ERROR!");
            return null;
        }
        else
        {
            state.waterLevel = (Levelled)block.getBlockData();
            if (state.waterLevel.getLevel() == 0)
            {
                player.sendMessage("Cauldron must have water!");
                return null;
            }
        }

        state.itemInCauldron = singleItem;
        if (quenchableItems.contains(singleItem))
        {
            state.ticksLeft = 60 + IslandPlugin.RANDOM.nextInt(40);
            return state;
        }
        else
        {
            player.sendMessage("Hold an item that can be quenched!");
            return null;
        }
    }

    public void startMinigame()
    {
        this.tickHandler.runTaskTimer(IslandPlugin.instance, 0, 1);
        cauldron.getWorld().playSound(cauldron, "minecraft:block.fire.extinguish", SoundCategory.PLAYERS, 1.0f, 1.0f);
    }
    public boolean tryFinish(Player player)
    {
        if (!this.player.equals(player)) return false;
        else finish();
        return true;
    }
    public void finish()
    {
        WorldServer worldServer = ((CraftWorld)cauldron.getWorld()).getHandle();
        net.minecraft.server.v1_16_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(getResult());
        EntityItem result = new EntityItem(worldServer, cauldron.getBlockX() + 0.5, cauldron.getBlockY() + 0.5, cauldron.getBlockZ() + 0.5, nmsStack);
        result.setMot(0, 0, 0);
        result.setPickupDelay(2);
        worldServer.addEntity(result);

        waterLevel.setLevel(waterLevel.getLevel() - 1);
        cauldron.getBlock().setBlockData(waterLevel);
        SmithingManager.cauldronFinished(this.cauldron);
    }

    private ItemStack getResult()
    {
        if (ticksLeft <= 0)
        {
            if (itemInCauldron.equals(SmithingManager.USELESS_STEEL_PLATE)) return SmithingManager.HARDENED_USELESS_STEEL_PLATE;
            else if (itemInCauldron.equals(SmithingManager.RUINED_STEEL_PLATE)) return SmithingManager.HARDENED_RUINED_STEEL_PLATE;
            else if (itemInCauldron.equals(SmithingManager.COMPROMISED_STEEL_PLATE)) return SmithingManager.HARDENED_COMPROMISED_STEEL_PLATE;
            else if (itemInCauldron.equals(SmithingManager.WEAK_STEEL_PLATE)) return SmithingManager.HARDENED_WEAK_STEEL_PLATE;
            else if (itemInCauldron.equals(SmithingManager.STEEL_PLATE)) return SmithingManager.HARDENED_STEEL_PLATE;
            else if (itemInCauldron.equals(SmithingManager.PERFECT_STEEL_PLATE)) return SmithingManager.HARDENED_PERFECT_STEEL_PLATE;
        }

        return itemInCauldron;
    }
    private void minigameTick()
    {
        this.ticksLeft--;
        if (ticksLeft > 0) cauldron.getWorld().spawnParticle(Particle.CLOUD, cauldron.clone().add(0.5, 1, 0.5), 1, 0.1, 0, 0.1, 0, null, true);
    }
}