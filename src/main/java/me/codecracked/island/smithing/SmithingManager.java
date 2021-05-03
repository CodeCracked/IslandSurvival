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

    public static ItemStack HARDENED_USELESS_STEEL_PLATE = new ItemStack(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, 1);
    public static ItemStack HARDENED_RUINED_STEEL_PLATE = new ItemStack(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, 1);
    public static ItemStack HARDENED_COMPROMISED_STEEL_PLATE = new ItemStack(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, 1);
    public static ItemStack HARDENED_WEAK_STEEL_PLATE = new ItemStack(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, 1);
    public static ItemStack HARDENED_STEEL_PLATE = new ItemStack(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, 1);
    public static ItemStack HARDENED_PERFECT_STEEL_PLATE = new ItemStack(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, 1);

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

        setItemName(HARDENED_USELESS_STEEL_PLATE, "Hardened Useless Steel Plate");
        setItemName(HARDENED_RUINED_STEEL_PLATE, "Hardened Ruined Steel Plate");
        setItemName(HARDENED_COMPROMISED_STEEL_PLATE, "Hardened Compromised Steel Plate");
        setItemName(HARDENED_WEAK_STEEL_PLATE, "Hardened Weak Steel Plate");
        setItemName(HARDENED_STEEL_PLATE, "Hardened Steel Plate");
        setItemName(HARDENED_PERFECT_STEEL_PLATE, "Hardened Perfect Steel Plate");

        addGlint(PERFECT_STEEL);
        addGlint(PERFECT_STEEL_PLATE);
        addGlint(HARDENED_PERFECT_STEEL_PLATE);
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
    private static Map<Location, CauldronState> cauldrons = new HashMap<>();

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
    public static void breakBlastFurnace(Player player, Location location)
    {
        BlastFurnaceGui gui = blastFurnaceGuis.get(location);
        if (gui != null)
        {
            gui.dropItems(location);
            blastFurnaceGuis.remove(location);
        }
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
    public static void breakAnvil(Player player, Location location)
    {
        AnvilState anvilState = anvils.get(location);
        if (anvilState != null) anvilState.finish();
    }

    public static void useCauldron(Player player, Location location)
    {
        CauldronState cauldronState = cauldrons.get(location);
        if (cauldronState != null)
        {
            if (!cauldronState.tryFinish(player)) player.sendMessage("That cauldron is in use!");
            return;
        }

        cauldronState = CauldronState.createCauldronStateForPlayer(player, location);
        if (cauldronState == null) return;

        cauldrons.put(location, cauldronState);
        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
        cauldronState.startMinigame();
    }
    public static void cauldronFinished(Location location)
    {
        cauldrons.remove(location);
    }
    public static void breakCauldron(Player player, Location location)
    {
        CauldronState cauldronState = cauldrons.get(location);
        if (cauldronState != null) cauldronState.finish();
    }
}
