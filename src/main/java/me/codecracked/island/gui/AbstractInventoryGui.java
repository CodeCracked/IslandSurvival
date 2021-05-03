package me.codecracked.island.gui;

import me.codecracked.island.IslandPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractInventoryGui implements Listener
{
    private final Inventory inventory;
    private BukkitRunnable tickHandler;
    private boolean open;
    private boolean itemsChanged;
    private Set<HumanEntity> visibleToPlayers;

    public AbstractInventoryGui(String name, int rows)
    {
        inventory = Bukkit.createInventory(null, rows * 9, name);
        visibleToPlayers = new HashSet<>();
        initializeItems();
    }
    public void showGui(final HumanEntity player)
    {
        IslandPlugin.registerEvents(this);
        open = true;
        startTickHandler();
        player.openInventory(inventory);
        visibleToPlayers.add(player);
        onOpen();
    }
    public void dropItems(Location location)
    {
        for (int slot : getDroppableSlots())
        {
            ItemStack stack = inventory.getItem(slot);
            if (stack != null && stack.getAmount() != 0) location.getWorld().dropItemNaturally(location, stack);
        }
    }

    //region Abstraction
    protected abstract void initializeItems();
    protected int[] getDroppableSlots() { return new int[0]; }
    /**
     * @return Whether to cancel the click event
     */
    protected abstract boolean onClick(int slot, ItemStack clicked, ClickType clickType, Player player);
    /**
     * @param inventoryOpen Whether the inventory is open
     * @return Whether to cancel the tick handler until the inventory is opened again
     */
    protected boolean tick(boolean inventoryOpen) { return !inventoryOpen; }
    protected void onOpen() { }
    protected void onClose() { }
    //endregion
    //region Events
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        if (event.getInventory() != inventory) return;

        ItemStack clickedStack = event.getCurrentItem();
        if (clickedStack == null || clickedStack.getType().isAir()) return;

        Player player = (Player)event.getWhoClicked();
        if (onClick(event.getRawSlot(), clickedStack, event.getClick(), player)) event.setCancelled(true);
    }
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event)
    {
        if (event.getInventory() == inventory) event.setCancelled(true);
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        if (event.getInventory() == inventory)
        {
            visibleToPlayers.remove(event.getPlayer());
            if (visibleToPlayers.size() == 0)
            {
                IslandPlugin.unregisterEvents(this);
                onClose();
                open = false;
            }
        }
    }
    //endregion
    //region Helpers
    protected Inventory getInventory() { return inventory; }
    protected void stopTicking() { stopTickHandler(); }

    protected final void setItem(int slot, final Material material)
    {
        inventory.setItem(slot, createGuiItem(material));
        itemsChanged = true;
    }
    protected final void setItem(int slot, final Material material, final String name)
    {
        inventory.setItem(slot, createGuiItem(material, name));
        itemsChanged = true;
    }
    protected final void setItem(int slot, final Material material, final String name, final int amount, final String... lore)
    {
        inventory.setItem(slot, createGuiItem(material, name, amount, lore));
        itemsChanged = true;
    }
    protected final void setItem(int slot, ItemStack item)
    {
        inventory.setItem(slot, item);
        itemsChanged = true;
    }

    protected final void setItems(int minSlot, int maxSlot, final Material material)
    {
        for (int slot = minSlot; slot <= maxSlot; slot++) setItem(slot, createGuiItem(material));
    }
    protected final void setItems(int minSlot, int maxSlot, final Material material, final String name)
    {
        for (int slot = minSlot; slot <= maxSlot; slot++) setItem(slot, createGuiItem(material, name));
    }
    protected final void setItems(int minSlot, int maxSlot, final Material material, final String name, final int amount, final String... lore)
    {
        for (int slot = minSlot; slot <= maxSlot; slot++) setItem(slot, createGuiItem(material, name, amount, lore));
    }
    protected final void setItems(int minSlot, int maxSlot, ItemStack item)
    {
        for (int slot = minSlot; slot <= maxSlot; slot++) setItem(slot, item);
    }

    protected final ItemStack createGuiItem(final Material material)
    {
        return createGuiItem(material, " ", 1);
    }
    protected final ItemStack createGuiItem(final Material material, final String name)
    {
        return createGuiItem(material, name, 1);
    }
    protected final ItemStack createGuiItem(final Material material, final String name, final int amount, final String... lore)
    {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
    //endregion
    //region Private Helpers
    private void startTickHandler()
    {
        if (tickHandler != null)
        {
            if (tickHandler.isCancelled()) stopTickHandler();
            else return;
        }

        tickHandler = new BukkitRunnable()
        {
            public void run()
            {
                if (tick(open)) tickHandler.cancel();
                if (itemsChanged)
                {
                    visibleToPlayers.forEach(player -> ((Player)player).updateInventory());
                    itemsChanged = false;
                }
            }
        };
        tickHandler.runTaskTimer(IslandPlugin.instance, 0, 1);
    }
    private void stopTickHandler()
    {
        if (tickHandler != null)
        {
            if (!tickHandler.isCancelled()) tickHandler.cancel();
            tickHandler = null;
            return;
        }
    }
    //endregion
}
