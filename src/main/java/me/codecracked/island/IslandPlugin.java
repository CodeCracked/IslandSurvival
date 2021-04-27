package me.codecracked.island;

import me.codecracked.island.events.DebugEvents;
import me.codecracked.island.events.EntitySpawn;
import me.codecracked.island.events.InteractWithEntity;
import me.codecracked.island.events.PlayerInteract;
import me.codecracked.island.scent.ScentManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class IslandPlugin extends JavaPlugin
{
    public static final boolean DEBUG_MODE = true;

    @Override
    public void onEnable()
    {
        System.out.println("Enabling Island Survival Plugin");

        ScentManager.init(this);

        PluginManager pm = this.getServer().getPluginManager();
        if (DEBUG_MODE) pm.registerEvents(new DebugEvents(this), this);
        pm.registerEvents(new InteractWithEntity(), this);
        pm.registerEvents(new PlayerInteract(), this);
        pm.registerEvents(new EntitySpawn(), this);
    }

    @Override
    public void onDisable()
    {

    }
}
