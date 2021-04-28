package me.codecracked.island.pathfinding;

import me.codecracked.island.entities.EntityTrackingWolf;
import me.codecracked.island.scent.ScentManager;
import me.codecracked.island.scent.ScentMarker;
import net.minecraft.server.v1_16_R3.PathfinderGoal;

public class PathfinderGoalScentTracking extends PathfinderGoal
{
    public static boolean ENABLED = true;

    private EntityTrackingWolf entity;
    private ScentMarker currentScentTarget;
    private int cooldown;

    public PathfinderGoalScentTracking(EntityTrackingWolf entity)
    {
        this.entity = entity;
    }

    @Override
    public boolean a()
    {
        if (!ENABLED) return false;
        if (!entity.isTamed() || entity.isSitting() || entity.getCurrentTarget() == null) return false;

        if (currentScentTarget != null && currentScentTarget.distanceToSqr(entity.getBukkitEntity().getLocation()) < 25) cooldown = 0;
        if (cooldown > 0)
        {
            cooldown--;
            return false;
        }

        currentScentTarget = ScentManager.getNextScentTarget(entity.getBukkitEntity().getLocation(), entity.getCurrentTarget(), currentScentTarget);
        cooldown = 10;
        return currentScentTarget != null;
    }

    @Override
    public void e()
    {
        entity.getNavigation().a(currentScentTarget.x, currentScentTarget.y, currentScentTarget.z, 1.0D);
    }
}
