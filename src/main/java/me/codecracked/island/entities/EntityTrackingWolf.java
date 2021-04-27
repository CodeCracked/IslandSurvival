package me.codecracked.island.entities;

import me.codecracked.island.pathfinding.PathfinderGoalScentTracking;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EntityWolf;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Entity;

import java.util.UUID;

public class EntityTrackingWolf extends EntityWolf
{
    private UUID currentTarget;

    public EntityTrackingWolf(Location location)
    {
        this(location, 0, 0);
    }
    public EntityTrackingWolf(Entity bukkitWolf, net.minecraft.server.v1_16_R3.Entity nmsWolf)
    {
        this(bukkitWolf.getLocation(), nmsWolf.yaw, nmsWolf.pitch);
    }
    public EntityTrackingWolf(Location location, float yaw, float pitch)
    {
        super(EntityTypes.WOLF, ((CraftWorld)location.getWorld()).getHandle());
        this.setPositionRotation(location.getX(), location.getY(), location.getZ(), yaw, pitch);
    }

    @Override
    protected void initPathfinder()
    {
        super.initPathfinder();
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
