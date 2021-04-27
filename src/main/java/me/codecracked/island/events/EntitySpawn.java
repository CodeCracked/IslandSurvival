package me.codecracked.island.events;

import me.codecracked.island.entities.EntityTrackingWolf;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityWolf;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class EntitySpawn implements Listener
{
    @EventHandler
    public void entitySpawn(EntitySpawnEvent event)
    {
        Entity nmsEntity = ((CraftEntity)event.getEntity()).getHandle();
        if (nmsEntity instanceof EntityTrackingWolf) return;
        else if (nmsEntity instanceof EntityWolf)
        {
            EntityTrackingWolf trackingWolf = new EntityTrackingWolf(event.getEntity(), nmsEntity);
            WorldServer world = ((CraftWorld)event.getLocation().getWorld()).getHandle();
            world.addEntity(trackingWolf);
            event.setCancelled(true);
        }
    }
}
