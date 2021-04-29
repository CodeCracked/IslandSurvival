package me.codecracked.island.smithing;

import me.codecracked.island.smithing.gui.BlastFurnaceGui;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SmithingManager
{
    private static Map<Location, BlastFurnaceGui> blastFurnaceGuis = new HashMap<>();

    public static void openBlastFurnace(Player player, Location location)
    {
        BlastFurnaceGui gui = blastFurnaceGuis.get(location);
        if (gui == null)
        {
            gui = new BlastFurnaceGui();
            blastFurnaceGuis.put(location, gui);
        }
        gui.showGui(player);
    }
}
