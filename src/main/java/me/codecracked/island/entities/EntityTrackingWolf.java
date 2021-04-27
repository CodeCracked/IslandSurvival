package me.codecracked.island.entities;

import me.codecracked.island.pathfinding.PathfinderGoalScentTracking;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EntityWolf;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

import java.util.UUID;

public class EntityTrackingWolf extends EntityWolf
{
    private UUID currentTarget;

    public EntityTrackingWolf(Location location)
    {
        super(EntityTypes.WOLF, ((CraftWorld)location.getWorld()).getHandle());

        this.setPosition(location.getX(), location.getY(), location.getZ());
        this.setCustomName(new ChatComponentText("Tracking Wolf"));
        this.setCustomNameVisible(true);
    }

    @Override
    protected void initPathfinder()
    {
        //super.initPathfinder();
        this.goalSelector.a(2, new PathfinderGoalScentTracking(this));
    }

    public void setCurrentTarget(UUID target)
    {
        currentTarget = target;
    }
    public UUID getCurrentTarget()
    {
        return currentTarget;
    }
}
