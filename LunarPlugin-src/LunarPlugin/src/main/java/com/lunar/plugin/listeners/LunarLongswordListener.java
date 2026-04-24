package com.lunar.plugin.listeners;

import com.lunar.plugin.LunarPlugin;
import com.lunar.plugin.items.LunarItems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LunarLongswordListener implements Listener {

    private final LunarItems lunarItems;

    public LunarLongswordListener(LunarPlugin plugin) {
        this.lunarItems = plugin.getLunarItems();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Attacker must be a player
        if (!(event.getDamager() instanceof Player attacker)) return;

        // Victim must also be a player (passive only triggers on PvP)
        if (!(event.getEntity() instanceof Player)) return;

        // Must be holding the Lunar Longsword in main hand
        ItemStack held = attacker.getInventory().getItemInMainHand();
        if (!lunarItems.isLunarLongsword(held)) return;

        // Apply Instant Health I (amplifier 0 = level I) for 1 second (20 ticks)
        // Instant Health is instantaneous — it fires once, duration is just for the effect object
        attacker.addPotionEffect(new PotionEffect(
            PotionEffectType.INSTANT_HEALTH,
            20,   // 1 second duration (the effect applies instantly anyway)
            0,    // amplifier 0 = Instant Health I
            false,
            true,
            true
        ));
    }
}
