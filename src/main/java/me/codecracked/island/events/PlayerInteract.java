package me.codecracked.island.events;

import me.codecracked.island.scent.ScentManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteract implements Listener
{
    @EventHandler
    public void playerInteract(PlayerInteractEvent event)
    {
        if (!event.getHand().equals(EquipmentSlot.HAND)) return;

        Player player = event.getPlayer();
        if (player.isSneaking())
        {
            Material block = player.getLocation().getBlock().getType();
            if (block == Material.WATER)
            {
                ItemStack mainHand = player.getInventory().getItemInMainHand();
                ItemStack offHand = player.getInventory().getItemInOffHand();

                if (mainHand.getType() == Material.DIRT && mainHand.getAmount() > 0) applyMud(player, mainHand);
                else if (offHand.getType() == Material.DIRT && mainHand.getAmount() > 0) applyMud(player, offHand);
            }
        }
    }

    private void applyMud(Player player, ItemStack itemStack)
    {
        if (player.getGameMode() != GameMode.CREATIVE) itemStack.setAmount(itemStack.getAmount() - 1);
        player.getWorld().playSound(player.getLocation(), "minecraft:entity.slime.squish", SoundCategory.PLAYERS, 1.0f, 0.0f);
        ScentManager.addMudToPlayer(player);
    }
}
