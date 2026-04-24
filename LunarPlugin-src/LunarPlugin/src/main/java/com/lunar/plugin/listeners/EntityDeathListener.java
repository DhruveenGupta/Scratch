package com.lunar.plugin.listeners;

import com.lunar.plugin.LunarPlugin;
import com.lunar.plugin.managers.EssenceManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Random;

public class EntityDeathListener implements Listener {

    private final LunarPlugin plugin;
    private final EssenceManager essenceManager;
    private final Random random = new Random();

    public EntityDeathListener(LunarPlugin plugin) {
        this.plugin = plugin;
        this.essenceManager = plugin.getEssenceManager();
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        // Only award if a player made the kill
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        int min = plugin.getConfig().getInt("essence.min-drop", 2);
        int max = plugin.getConfig().getInt("essence.max-drop", 5);

        // Random amount between min and max (inclusive)
        long amount = min + random.nextInt(max - min + 1);

        essenceManager.addEssence(killer, amount);

        killer.sendActionBar(
            Component.text("+" + amount + " ✦ Lunar Essence  (", NamedTextColor.AQUA)
                     .append(Component.text(essenceManager.getBalance(killer) + " total", NamedTextColor.LIGHT_PURPLE))
                     .append(Component.text(")", NamedTextColor.AQUA))
        );
    }
}
