package me.codecracked.island.smithing;

import me.codecracked.island.smithing.gui.BlastFurnaceGui;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
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
                if (type.equals(Material.BLAST_FURNACE)) event.setCancelled(rightClickBlastFurnace(event.getPlayer(), block.getLocation()));
                else if (type.equals(Material.ANVIL)) event.setCancelled(rightClickAnvil(event.getPlayer(), block.getLocation()));
                else if (type.equals(Material.CAULDRON)) event.setCancelled(rightClickCauldron(event.getPlayer(), block.getLocation()));
                else if (type.equals(Material.SMITHING_TABLE)) event.setCancelled(rightClickSmithingTable(event.getPlayer(), block.getLocation()));
                else return;
            }
        }
    }
    @EventHandler
    public void breakBlock(BlockBreakEvent event)
    {
        if (event.isCancelled()) return;

        Block block = event.getBlock();
        if (block != null)
        {
            Material type = block.getType();
            if (type.equals(Material.BLAST_FURNACE)) breakBlastFurnace(event.getPlayer(), block.getLocation());
            else if (type.equals(Material.ANVIL)) breakAnvil(event.getPlayer(), block.getLocation());
            else if (type.equals(Material.CAULDRON)) breakCauldron(event.getPlayer(), block.getLocation());
            else if (type.equals(Material.SMITHING_TABLE)) breakSmithingTable(event.getPlayer(), block.getLocation());
            else return;
        }
    }

    private boolean rightClickBlastFurnace(Player player, Location location)
    {
        SmithingManager.openBlastFurnace(player, location);
        return true;
    }
    private boolean rightClickAnvil(Player player, Location location)
    {
        SmithingManager.useAnvil(player, location);
        return true;
    }
    private boolean rightClickCauldron(Player player, Location location)
    {
        if (player.getInventory().getItemInMainHand().getType().equals(Material.WATER_BUCKET)) return false;
        else if (player.getInventory().getItemInOffHand().getType().equals(Material.WATER_BUCKET)) return false;
        else
        {
            SmithingManager.useCauldron(player, location);
            return true;
        }
    }
    private boolean rightClickSmithingTable(Player player, Location location)
    {
        return true;
    }

    private void breakBlastFurnace(Player player, Location location)
    {
        SmithingManager.breakBlastFurnace(player, location);
    }
    private void breakAnvil(Player player, Location location)
    {
        SmithingManager.breakAnvil(player, location);
    }
    private void breakCauldron(Player player, Location location)
    {
        SmithingManager.breakCauldron(player, location);
    }
    private void breakSmithingTable(Player player, Location location)
    {

    }
}
