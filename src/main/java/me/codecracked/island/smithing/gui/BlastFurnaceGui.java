package me.codecracked.island.smithing.gui;

import me.codecracked.island.gui.AbstractInventoryGui;
import me.codecracked.island.smithing.SmithingManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class BlastFurnaceGui extends AbstractInventoryGui
{
    public static final int TICKS_PER_PROGRESS_NOTCH = 50;
    public static final int TICKS_FOR_OVERCOOK = 5;

    private static final int ORE_SLOT = 0;
    private static final int STEEL_SLOT = 4;

    private long smeltingTime;

    public BlastFurnaceGui()
    {
        super("Blast Furnace", 2);
    }

    @Override
    protected void initializeItems()
    {
        setItems(1, 3, Material.BLACK_STAINED_GLASS_PANE);
        setItems(5, 8, Material.BLACK_STAINED_GLASS_PANE);
        setItems(9, 15, Material.YELLOW_STAINED_GLASS_PANE);
        setItem(16, Material.GREEN_STAINED_GLASS_PANE);
        setItem(17, Material.RED_STAINED_GLASS_PANE);
    }
    @Override
    protected int[] getDroppableSlots()
    {
        return new int[] { ORE_SLOT, STEEL_SLOT };
    }

    @Override
    protected void onOpen()
    {
        refreshState();
    }
    @Override
    protected boolean onClick(int slot, ItemStack clicked, ClickType clickType, Player player)
    {
        if (slot < 18)
        {
            if (slot == ORE_SLOT) return !isValidForOreSlot(player, clicked);
            else if (slot == STEEL_SLOT) return !isSteelIngotTaken(player, clicked);
            else return true;
        }
        else return false;
    }
    @Override
    protected boolean tick(boolean inventoryOpen)
    {
        if (smeltingTime > 0)
        {
            if (smeltingTime == 1) refreshState();

            smeltingTime++;
            for (int i = 0; i < 9; i++) checkProgressUpdate(i);
            return false;
        }
        else
        {
            ItemStack steelStack = getInventory().getItem(STEEL_SLOT);
            ItemStack oreStack = getInventory().getItem(ORE_SLOT);
            boolean steelEmpty = steelStack == null || steelStack.getAmount() == 0;
            boolean oreEmpty = oreStack == null || oreStack.getAmount() == 0;

            if (steelEmpty)
            {
                if (!oreEmpty)
                {
                    startSmelting();
                    return false;
                }
                else return !inventoryOpen;
            }
            else return !inventoryOpen;
        }
    }
    private void checkProgressUpdate(int slotOffset)
    {
        int timeCheck = (slotOffset < 8) ? TICKS_PER_PROGRESS_NOTCH * (slotOffset + 1) : TICKS_PER_PROGRESS_NOTCH * 8 + TICKS_FOR_OVERCOOK;
        if (smeltingTime == timeCheck)
        {
            setItem(9 + slotOffset, Material.ORANGE_STAINED_GLASS_PANE);
            updateSteelState(slotOffset);
        }
    }
    private void refreshState()
    {
        setItems(9, 15, Material.YELLOW_STAINED_GLASS_PANE);
        setItem(16, Material.GREEN_STAINED_GLASS_PANE);
        setItem(17, Material.RED_STAINED_GLASS_PANE);

        int smeltingState = 0;
        for (int i = 0; i < 9; i++)
        {
            int timeCheck = (i < 8) ? TICKS_PER_PROGRESS_NOTCH * (i + 1) : TICKS_PER_PROGRESS_NOTCH * 8 + TICKS_FOR_OVERCOOK;
            if (smeltingTime >= timeCheck)
            {
                setItem(9 + i, Material.ORANGE_STAINED_GLASS_PANE);
                smeltingState = i;
            }
        }
        updateSteelState(smeltingState);
    }
    private void startSmelting()
    {
        getInventory().getItem(ORE_SLOT).setAmount(getInventory().getItem(ORE_SLOT).getAmount() - 1);
        resetSmelting();
    }
    private void completeSmelting()
    {
        smeltingTime = 0;
        setItems(9, 15, Material.YELLOW_STAINED_GLASS_PANE);
        setItem(16, Material.GREEN_STAINED_GLASS_PANE);
        setItem(17, Material.RED_STAINED_GLASS_PANE);
    }
    private void resetSmelting()
    {
        smeltingTime = 1;
        setItems(9, 15, Material.YELLOW_STAINED_GLASS_PANE);
        setItem(16, Material.GREEN_STAINED_GLASS_PANE);
        setItem(17, Material.RED_STAINED_GLASS_PANE);
        getInventory().clear(STEEL_SLOT);
    }

    private boolean isValidForOreSlot(Player player, ItemStack stack)
    {
        if (stack == null || stack.getType() != Material.IRON_ORE) return false;
        else return true;
    }
    private boolean isSteelIngotTaken(Player player, ItemStack stack)
    {
        if (smeltingTime < TICKS_PER_PROGRESS_NOTCH * 4) return false;
        else
        {
            completeSmelting();
            return true;
        }
    }
    private void updateSteelState(int smeltingState)
    {
        if (smeltingState < 4) getInventory().clear(STEEL_SLOT);
        else if (smeltingState < 6) setItem(STEEL_SLOT, SmithingManager.WEAK_STEEL);
        else if (smeltingState == 6) setItem(STEEL_SLOT, SmithingManager.STEEL);
        else if (smeltingState == 7) setItem(STEEL_SLOT, SmithingManager.PERFECT_STEEL);
        else setItem(STEEL_SLOT, SmithingManager.COMPROMISED_STEEL);
    }
}