package me.codecracked.island;

import me.codecracked.island.events.*;
import me.codecracked.island.scent.ScentManager;
import me.codecracked.island.smithing.SmithingEvents;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class IslandPlugin extends JavaPlugin
{
    public static final boolean DEBUG_MODE = true;

    public static IslandPlugin instance;
    public static PluginManager pluginManager;

    @Override
    public void onEnable()
    {
        instance = this;
        pluginManager = this.getServer().getPluginManager();

        ScentManager.init(this);

        if (DEBUG_MODE) pluginManager.registerEvents(new DebugEvents(), this);
        pluginManager.registerEvents(new WolfEvents(), this);
        pluginManager.registerEvents(new MudEvents(), this);
        pluginManager.registerEvents(new PitfallEvents(), this);
        pluginManager.registerEvents(new SmithingEvents(), this);
    }
    @Override
    public void onDisable()
    {
        HandlerList.unregisterAll(instance);
    }

    public static void registerEvents(Listener events)
    {
        pluginManager.registerEvents(events, instance);
    }
    public static void unregisterEvents(Listener events)
    {
        HandlerList.unregisterAll(events);
    }
}
