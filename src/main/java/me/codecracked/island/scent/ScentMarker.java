package me.codecracked.island.scent;

import org.bukkit.Location;

public class ScentMarker
{
    public final float x;
    public final float y;
    public final float z;
    public final int timeCreated;

    public ScentMarker(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.timeCreated = ScentManager.getCurrentTime();
    }

    public float distanceToSqr(Location location)
    {
        float dx = x - (float)location.getX();
        float dy = y - (float)location.getY();
        float dz = z - (float)location.getZ();
        return dx * dx + dy * dy + dz * dz;
    }
}
