package com.lunar.plugin;

import com.lunar.plugin.commands.CompactCommand;
import com.lunar.plugin.commands.LunarCommand;
import com.lunar.plugin.commands.WithdrawCommand;
import com.lunar.plugin.listeners.EntityDeathListener;
import com.lunar.plugin.listeners.LunarMaceListener;
import com.lunar.plugin.listeners.LunarLongswordListener;
import com.lunar.plugin.listeners.CraftingListener;
import com.lunar.plugin.managers.EssenceManager;
import com.lunar.plugin.items.LunarItems;
import org.bukkit.plugin.java.JavaPlugin;

public class LunarPlugin extends JavaPlugin {

    private static LunarPlugin instance;
    private EssenceManager essenceManager;
    private LunarItems lunarItems;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Initialize managers
        essenceManager = new EssenceManager(this);
        lunarItems = new LunarItems(this);

        // Register listeners
        getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new LunarMaceListener(this), this);
        getServer().getPluginManager().registerEvents(new LunarLongswordListener(this), this);
        getServer().getPluginManager().registerEvents(new CraftingListener(this), this);

        // Register commands
        getCommand("lunar").setExecutor(new LunarCommand(this));
        getCommand("withdraw").setExecutor(new WithdrawCommand(this));
        getCommand("compact").setExecutor(new CompactCommand(this));

        // Register custom recipes
        lunarItems.registerRecipes();

        getLogger().info("LunarPlugin enabled! Lunar Essence system is active.");
    }

    @Override
    public void onDisable() {
        essenceManager.saveAllData();
        getLogger().info("LunarPlugin disabled. All data saved.");
    }

    public static LunarPlugin getInstance() {
        return instance;
    }

    public EssenceManager getEssenceManager() {
        return essenceManager;
    }

    public LunarItems getLunarItems() {
        return lunarItems;
    }
}
