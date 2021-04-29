package me.codecracked.island.smithing;

import me.codecracked.island.smithing.gui.BlastFurnaceGui;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class SmithingManager
{
    public static ItemStack COMPROMISED_STEEL = new ItemStack(Material.IRON_INGOT, 1);
    public static ItemStack WEAK_STEEL = new ItemStack(Material.IRON_INGOT, 1);
    public static ItemStack STEEL = new ItemStack(Material.IRON_INGOT, 1);
    public static ItemStack PERFECT_STEEL = new ItemStack(Material.IRON_INGOT, 1);

    public static ItemStack USELESS_STEEL_PLATE = new ItemStack(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, 1);
    public static ItemStack RUINED_STEEL_PLATE = new ItemStack(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, 1);
    public static ItemStack COMPROMISED_STEEL_PLATE = new ItemStack(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, 1);
    public static ItemStack WEAK_STEEL_PLATE = new ItemStack(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, 1);
    public static ItemStack STEEL_PLATE = new ItemStack(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, 1);
    public static ItemStack PERFECT_STEEL_PLATE = new ItemStack(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, 1);

    static
    {
        setItemName(COMPROMISED_STEEL, "Compromised Steel Ingot");
        setItemName(WEAK_STEEL, "Weak Steel Ingot");
        setItemName(STEEL, "Steel Ingot");
        setItemName(PERFECT_STEEL, "Perfect Steel Ingot");

        setItemName(USELESS_STEEL_PLATE, "Useless Steel Plate");
        setItemName(RUINED_STEEL_PLATE, "Ruined Steel Plate");
        setItemName(COMPROMISED_STEEL_PLATE, "Compromised Steel Plate");
        setItemName(WEAK_STEEL_PLATE, "Weak Steel Plate");
        setItemName(STEEL_PLATE, "Steel Plate");
        setItemName(PERFECT_STEEL_PLATE, "Perfect Steel Plate");

        addGlint(PERFECT_STEEL);
        addGlint(PERFECT_STEEL_PLATE);
    }
    private static void setItemName(ItemStack stack, String name)
    {
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        stack.setItemMeta(meta);
    }
    private static void addGlint(ItemStack stack)
    {
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 0, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
    }

    private static Map<Location, BlastFurnaceGui> blastFurnaceGuis = new HashMap<>();
    private static Map<Location, AnvilState> anvils = new HashMap<>();

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
    public static void useAnvil(Player player, Location location)
    {
        AnvilState anvilState = anvils.get(location);
        if (anvilState != null)
        {
            if (!anvilState.hammer(player)) player.sendMessage("That anvil is in use!");
            return;
        }

        anvilState = AnvilState.createAnvilStateFromPlayer(player, location);
        if (anvilState == null)
        {
            player.sendMessage("Hold an item that can be hammered!");
            return;
        }

        anvils.put(location, anvilState);
        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
        anvilState.startMinigame();
    }
    public static void anvilFinished(Location location)
    {
        anvils.remove(location);
    }
}
