package me.codecracked.island;

import me.codecracked.island.events.BlockPlace;
import me.codecracked.island.events.InteractWithEntity;
import me.codecracked.island.events.PlayerInteract;
import me.codecracked.island.scent.ScentManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class IslandPlugin extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        System.out.println("Enabling Island Survival Plugin");

        ScentManager.init(this);

        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new BlockPlace(this), this);
        pm.registerEvents(new InteractWithEntity(), this);
        pm.registerEvents(new PlayerInteract(), this);
    }

    @Override
    public void onDisable()
    {

    }
}
