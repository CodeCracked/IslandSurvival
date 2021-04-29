package me.codecracked.island.smithing;

import me.codecracked.island.IslandPlugin;
import net.minecraft.server.v1_16_R3.EntityItem;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class AnvilState
{
    private static Set<ItemStack> hammerableItems = new HashSet<>();
    static
    {
        hammerableItems.add(SmithingManager.WEAK_STEEL);
        hammerableItems.add(SmithingManager.STEEL);
        hammerableItems.add(SmithingManager.PERFECT_STEEL);
        hammerableItems.add(SmithingManager.COMPROMISED_STEEL);
    }

    private Player player;
    private Location anvil;
    private ItemStack itemOnAnvil;
    private EntityItem itemPreview;
    private int hammersLeft;
    private float hammerWindow;

    private BukkitRunnable tickHandler;
    private BossBar bossBar;
    private float minigameTimer;
    private int successfulHammers;

    private AnvilState (Player player, Location anvil)
    {
        this.player = player;
        this.anvil = anvil;
        this.tickHandler = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                minigameTick();
            }
        };
    }
    public static AnvilState createAnvilStateFromPlayer(Player player, Location anvil)
    {
        AnvilState state = new AnvilState(player, anvil);

        ItemStack playerHand = player.getInventory().getItemInMainHand();
        ItemStack singleItem = playerHand.clone();
        singleItem.setAmount(1);

        if (playerHand == null || playerHand.getAmount() == 0) return null;

        state.itemOnAnvil = singleItem;
        if (singleItem.equals(SmithingManager.WEAK_STEEL))
        {
            state.hammersLeft = 3;
            state.hammerWindow = 0.9f;
            return state;
        }
        else if (singleItem.equals(SmithingManager.STEEL))
        {
            state.hammersLeft = 4;
            state.hammerWindow = 0.85f;
            return state;
        }
        else if (singleItem.equals(SmithingManager.PERFECT_STEEL))
        {
            state.hammersLeft = 5;
            state.hammerWindow = 0.8f;
            return state;
        }
        else if (singleItem.equals(SmithingManager.COMPROMISED_STEEL))
        {
            state.hammersLeft = 2;
            state.hammerWindow = 0.85f;
            return state;
        }
        else return null;
    }

    public void startMinigame()
    {
        WorldServer worldServer = ((CraftWorld)anvil.getWorld()).getHandle();
        net.minecraft.server.v1_16_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemOnAnvil);
        itemPreview = new EntityItem(worldServer, anvil.getBlockX() + 0.5, anvil.getBlockY() + 1, anvil.getBlockZ() + 0.5, nmsStack);
        itemPreview.setPickupDelay(32768);
        itemPreview.setInvulnerable(true);
        itemPreview.age = -32768;
        itemPreview.setMot(0, 0, 0);
        worldServer.addEntity(itemPreview);

        this.minigameTimer = 0;
        this.bossBar = Bukkit.createBossBar("Anvil", BarColor.WHITE, BarStyle.SEGMENTED_20);
        this.bossBar.addPlayer(this.player);
        this.bossBar.setVisible(true);
        this.tickHandler.runTaskTimer(IslandPlugin.instance, 0, 1);
    }
    public boolean hammer(Player player)
    {
        if (!this.player.equals(player)) return false;

        float percent = (this.minigameTimer <= 1) ? this.minigameTimer : (1 - (this.minigameTimer - 1));
        if (percent >= this.hammerWindow)
        {
             player.getWorld().playSound(player.getLocation(), "minecraft:block.anvil.use", SoundCategory.PLAYERS, 1.0f, 1.0f);
             successfulHammers++;
        }
        else player.getWorld().playSound(player.getLocation(), "minecraft:block.anvil.destroy", SoundCategory.PLAYERS, 1.0f, 1.0f);

        hammersLeft--;
        if (hammersLeft == 0)
        {
            WorldServer worldServer = ((CraftWorld)anvil.getWorld()).getHandle();
            worldServer.removeEntity(itemPreview);

            net.minecraft.server.v1_16_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(getResult());
            EntityItem result = new EntityItem(worldServer, anvil.getBlockX() + 0.5, anvil.getBlockY() + 1, anvil.getBlockZ() + 0.5, nmsStack);
            result.setMot(0, 0, 0);
            result.setPickupDelay(0);
            worldServer.addEntity(result);

            this.bossBar.setVisible(false);
            this.bossBar.removeAll();

            SmithingManager.anvilFinished(this.anvil);
        }

        return true;
    }

    private ItemStack getResult()
    {
        switch(successfulHammers)
        {
            case 1: return SmithingManager.RUINED_STEEL_PLATE;
            case 2: return SmithingManager.COMPROMISED_STEEL_PLATE;
            case 3: return SmithingManager.WEAK_STEEL_PLATE;
            case 4: return SmithingManager.STEEL_PLATE;
            case 5: return SmithingManager.PERFECT_STEEL_PLATE;
            default: return SmithingManager.USELESS_STEEL_PLATE;
        }
    }
    private void minigameTick()
    {
        this.minigameTimer += 0.05f;
        while (this.minigameTimer >= 2.0f) this.minigameTimer -= 2.0f;

        float percent = (this.minigameTimer <= 1) ? this.minigameTimer : (1 - (this.minigameTimer - 1));
        this.bossBar.setProgress(percent);
    }
}