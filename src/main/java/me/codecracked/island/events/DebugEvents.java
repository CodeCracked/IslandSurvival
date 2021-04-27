package me.codecracked.island.events;

import me.codecracked.island.IslandPlugin;
import me.codecracked.island.entities.EntityTrackingWolf;
import me.codecracked.island.pathfinding.PathfinderGoalScentTracking;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class DebugEvents implements Listener
{
    private IslandPlugin plugin;
    public DebugEvents(IslandPlugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event)
    {
        if (event.getBlock().getType().equals(Material.GOLD_BLOCK))
        {
            EntityTrackingWolf trackingWolf = new EntityTrackingWolf(event.getPlayer().getLocation());
            WorldServer world = ((CraftWorld)event.getPlayer().getWorld()).getHandle();
            world.addEntity(trackingWolf);

            event.setCancelled(true);
        }

        if (event.getBlock().getType().equals(Material.LIME_WOOL))
        {
            PathfinderGoalScentTracking.ENABLED = true;
            event.setCancelled(true);
        }
        if (event.getBlock().getType().equals(Material.RED_WOOL))
        {
            PathfinderGoalScentTracking.ENABLED = false;
            event.setCancelled(true);
        }
    }
}
