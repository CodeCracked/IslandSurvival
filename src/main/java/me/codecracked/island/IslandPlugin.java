package me.codecracked.island;

import me.codecracked.island.events.*;
import me.codecracked.island.scent.ScentManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class IslandPlugin extends JavaPlugin
{
    public static final boolean DEBUG_MODE = true;

    @Override
    public void onEnable()
    {
        ScentManager.init(this);

        PluginManager pm = this.getServer().getPluginManager();
        if (DEBUG_MODE) pm.registerEvents(new DebugEvents(), this);
        pm.registerEvents(new WolfEvents(), this);
        pm.registerEvents(new MudEvents(), this);
        pm.registerEvents(new PitfallEvents(), this);
    }

    @Override
    public void onDisable()
    {

    }
}
