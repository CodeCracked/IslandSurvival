package me.codecracked.island.events;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class PitfallEvents implements Listener
{
    private static final Set<Material> pitfallBlocks = new HashSet<>();
    private static final Set<Material> pitfallSupportBlocks = new HashSet<>();
    private static final Map<Material, Float> pitfallDamageMultipliers = new HashMap<>();
    static
    {
        pitfallBlocks.add(Material.ACACIA_LEAVES);
        pitfallBlocks.add(Material.BIRCH_LEAVES);
        pitfallBlocks.add(Material.DARK_OAK_LEAVES);
        pitfallBlocks.add(Material.JUNGLE_LEAVES);
        pitfallBlocks.add(Material.OAK_LEAVES);
        pitfallBlocks.add(Material.SPRUCE_LEAVES);

        pitfallSupportBlocks.add(Material.TRIPWIRE);

        pitfallDamageMultipliers.put(Material.ACACIA_FENCE, 2.0f);
        pitfallDamageMultipliers.put(Material.BIRCH_FENCE, 2.0f);
        pitfallDamageMultipliers.put(Material.DARK_OAK_FENCE, 2.0f);
        pitfallDamageMultipliers.put(Material.JUNGLE_FENCE, 2.0f);
        pitfallDamageMultipliers.put(Material.OAK_FENCE, 2.0f);
        pitfallDamageMultipliers.put(Material.SPRUCE_FENCE, 2.0f);
        pitfallDamageMultipliers.put(Material.IRON_BARS, 3.0f);
    }

    @EventHandler
    public void entityDamage(EntityDamageEvent event)
    {
        if (event.getEntityType().equals(EntityType.PLAYER) && event.getCause().equals(EntityDamageEvent.DamageCause.FALL))
        {
            Material fallOn = event.getEntity().getLocation().add(0, -1, 0).getBlock().getType();
            Float multiplier = pitfallDamageMultipliers.get(fallOn);
            if (multiplier == null) return;
            else event.setDamage(event.getDamage(EntityDamageEvent.DamageModifier.BASE) * multiplier);
        }
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent event)
    {
        if (event.getPlayer().getGameMode().equals(GameMode.SPECTATOR) || event.getPlayer().isSneaking()) return;

        Location location = event.getPlayer().getLocation();
        location.add(0, -1, 0);

        Material standingOn = location.getBlock().getType();
        if (pitfallBlocks.contains(standingOn))
        {
            Material supportingBlock = location.clone().add(0, -1, 0).getBlock().getType();
            if (pitfallSupportBlocks.contains(supportingBlock))
            {
                breakPitfall(location);
            }
        }
    }

    private void breakPitfall(Location pitfallLocation)
    {
        Queue<Location> frontier = new ArrayDeque<>();
        Set<Location> explored = new HashSet<>();
        frontier.add(pitfallLocation.clone());

        WorldServer world = ((CraftWorld)pitfallLocation.getWorld()).getHandle();

        while (!frontier.isEmpty())
        {
            Location next = frontier.remove();
            explored.add(next);

            if (pitfallBlocks.contains(next.getBlock().getType()))
            {
                Location support = next.clone().add(0, -1, 0);
                if (pitfallSupportBlocks.contains(support.getBlock().getType()))
                {
                    world.b(new BlockPosition(next.getX(), next.getY(), next.getZ()), true);
                    world.b(new BlockPosition(support.getX(), support.getY(), support.getZ()), true);

                    Location nx = next.clone().add(-1, 0, 0);
                    Location px = next.clone().add(1, 0, 0);
                    Location nz = next.clone().add(0, 0, -1);
                    Location pz = next.clone().add(0, 0, 1);

                    if (!explored.contains(nx)) frontier.add(nx);
                    if (!explored.contains(px)) frontier.add(px);
                    if (!explored.contains(nz)) frontier.add(nz);
                    if (!explored.contains(pz)) frontier.add(pz);
                }
            }
        }
    }
}
