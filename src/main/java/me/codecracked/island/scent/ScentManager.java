package me.codecracked.island.scent;

import me.codecracked.island.IslandPlugin;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScentManager
{
    public static final int SCENT_TRAIL_SIZE = 1024;
    public static final int SCENT_TRAIL_PERIOD = 10;
    public static final double SCENT_ITEM_DISTANCE = 16;
    public static final float MAX_SCENT_DISTANCE_SQR = 32 * 32;
    public static final int MUD_TICKS_PER_DIRT = 200;
    public static final int MAX_MUD = 2400;

    private class ScentTrail
    {
        private ScentMarker[] scentMarkers;
        private int nextMarkerIndex;

        public ScentTrail(int size)
        {
            scentMarkers = new ScentMarker[size];
            nextMarkerIndex = 0;
        }
        public void addMarker(Location location)
        {
            ScentMarker marker = new ScentMarker((float)location.getX(), (float)location.getY(), (float)location.getZ());
            scentMarkers[nextMarkerIndex] = marker;
            nextMarkerIndex++;
        }

        public ScentMarker getNextTarget(Location location, ScentMarker lastScentTarget)
        {
            ScentMarker bestMarker = null;
            for (ScentMarker marker : scentMarkers)
            {
                if (marker != null && marker.distanceToSqr(location) <= MAX_SCENT_DISTANCE_SQR)
                {
                    if (bestMarker == null || bestMarker.timeCreated < marker.timeCreated) bestMarker = marker;
                }
            }
            return bestMarker;
        }
    }
    private class ScentState
    {
        public int mudExpireTime;
    }

    private static ScentManager instance;

    private IslandPlugin plugin;
    private int timer;
    private BukkitRunnable ticker;
    private Map<UUID, ScentTrail> scentTrails;
    private Map<UUID, ScentState> playerScentStates;

    private ScentManager(IslandPlugin plugin)
    {
        this.plugin = plugin;

        scentTrails = new HashMap<>();
        playerScentStates = new HashMap<>();

        ticker = new BukkitRunnable() { @Override public void run() { tick(); } };
        ticker.runTaskTimer(plugin, 0, 1);
    }

    public static void init(IslandPlugin plugin)
    {
        if (instance == null) instance = new ScentManager(plugin);
    }
    private void tick()
    {
        timer++;
        if (timer % SCENT_TRAIL_PERIOD == 0)
        {
            for (Player player : plugin.getServer().getOnlinePlayers())
            {
                ScentState scentState = playerScentStates.get(player.getUniqueId());
                if (scentState == null)
                {
                    scentState = new ScentState();
                    playerScentStates.put(player.getUniqueId(), scentState);
                }

                if (scentState.mudExpireTime > 0)
                {
                    scentState.mudExpireTime--;
                    continue;
                }

                tryPlaceScentMarker_Singleton(player);
                for (Entity entity : player.getNearbyEntities(SCENT_ITEM_DISTANCE, SCENT_ITEM_DISTANCE, SCENT_ITEM_DISTANCE))
                {
                    if (entity instanceof Item)
                    {
                        Item item = (Item)entity;
                        item.setItemStack(addScentToItem(item.getItemStack(), player));
                    }
                }
            }
        }
    }

    public static ScentManager getInstance()
    {
        return instance;
    }
    public static int getCurrentTime()
    {
        return instance.timer;
    }
    public static ScentMarker getNextScentTarget(Location location, UUID target, ScentMarker lastFoundTarget)
    {
        return instance.getNextScentTarget_Singleton(location, target, lastFoundTarget);
    }
    public static void addMudToPlayer(Player player)
    {
        instance.addMudToPlayer_Singleton(player);
    }
    public static ItemStack addScentToItem(ItemStack item, Player player)
    {
        net.minecraft.server.v1_16_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nbt = nmsStack.hasTag() ? nmsStack.getTag() : new NBTTagCompound();

        int i = 1;
        while (nbt.hasKey("Scent" + i))
        {
            UUID scent = nbt.a("Scent" + i);
            i++;
            if (scent.equals(player.getUniqueId())) return item;
        }
        nbt.a("Scent" + i, player.getUniqueId());

        nmsStack.setTag(nbt);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }
    public static ItemStack stripScentsFromItem(ItemStack item)
    {
        net.minecraft.server.v1_16_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nbt = nmsStack.hasTag() ? nmsStack.getTag() : new NBTTagCompound();

        int i = 1;
        while (nbt.hasKey("Scent" + i)) nbt.remove("Scent" + i);
        nmsStack.setTag(nbt);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    private void tryPlaceScentMarker_Singleton(Player player)
    {
        ScentTrail scentTrail = scentTrails.get(player.getUniqueId());
        if (scentTrail == null) scentTrail = new ScentTrail(SCENT_TRAIL_SIZE);
        scentTrail.addMarker(player.getLocation());
        scentTrails.put(player.getUniqueId(), scentTrail);
    }
    private ScentMarker getNextScentTarget_Singleton(Location location, UUID target, ScentMarker lastFoundTarget)
    {
        ScentTrail scentTrail = scentTrails.get(target);
        if (scentTrail == null) return null;
        else return scentTrail.getNextTarget(location, lastFoundTarget);
    }
    private void addMudToPlayer_Singleton(Player player)
    {
        ScentState scentState = playerScentStates.get(player.getUniqueId());
        if (scentState == null) scentState = new ScentState();

        scentState.mudExpireTime = Math.min(MAX_MUD, scentState.mudExpireTime + MUD_TICKS_PER_DIRT);
        playerScentStates.put(player.getUniqueId(), scentState);
    }
}
