package me.codecracked.island.events;

import me.codecracked.island.entities.EntityTrackingWolf;
import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;

public class InteractWithEntity implements Listener
{
    public static final boolean ALLOW_OWNER_TRACKING = true;

    @EventHandler
    public void interactWithEntity(PlayerInteractEntityEvent event)
    {
        if (event.getHand() != EquipmentSlot.HAND) return;

        CraftEntity entity = (CraftEntity) event.getRightClicked();
        if (entity.getHandle() instanceof EntityTrackingWolf)
        {
            EntityTrackingWolf wolf = (EntityTrackingWolf)entity.getHandle();
            if (!wolf.isSitting())
            {
                ItemStack stack = CraftItemStack.asNMSCopy(event.getPlayer().getInventory().getItemInMainHand());
                Material item = event.getPlayer().getInventory().getItemInMainHand().getType();

                if (item != null && !item.isAir() && stack.hasTag())
                {
                    NBTTagCompound tag = stack.getTag();
                    int i = 1;
                    while (tag.hasKey("Scent" + i))
                    {
                        UUID scent = stack.getTag().a("Scent" + i);
                        i++;
                        if ((scent.equals(wolf.getOwnerUUID()) && !ALLOW_OWNER_TRACKING) || scent.equals(wolf.getCurrentTarget())) continue;

                        wolf.setCurrentTarget(scent);
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
}
