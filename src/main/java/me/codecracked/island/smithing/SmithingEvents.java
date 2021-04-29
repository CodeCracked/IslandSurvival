package me.codecracked.island.smithing;

import me.codecracked.island.smithing.gui.BlastFurnaceGui;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class SmithingEvents implements Listener
{
    @EventHandler
    public void interactWithBlock(PlayerInteractEvent event)
    {
        if (event.isCancelled()) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND)
        {
            Block block = event.getClickedBlock();
            if (block != null)
            {
                Material type = block.getType();
                if (type.equals(Material.BLAST_FURNACE)) rightClickBlastFurnace(event.getPlayer(), block.getLocation());
                else if (type.equals(Material.ANVIL)) rightClickAnvil(event.getPlayer(), block.getLocation());
                else if (type.equals(Material.CAULDRON)) rightClickCauldron(event.getPlayer(), block.getLocation());
                else if (type.equals(Material.SMITHING_TABLE)) rightClickSmithingTable(event.getPlayer(), block.getLocation());
                else return;

                event.setCancelled(true);
            }
        }
    }

    private void rightClickBlastFurnace(Player player, Location location)
    {
        SmithingManager.openBlastFurnace(player, location);
    }
    private void rightClickAnvil(Player player, Location location)
    {

    }
    private void rightClickCauldron(Player player, Location location)
    {

    }
    private void rightClickSmithingTable(Player player, Location location)
    {

    }
}
