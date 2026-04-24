package com.lunar.plugin.listeners;

import com.lunar.plugin.LunarPlugin;
import com.lunar.plugin.items.LunarItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LunarMaceListener implements Listener {

    private final LunarPlugin plugin;
    private final LunarItems lunarItems;

    // Maps player UUID → System.currentTimeMillis() of last ability use
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public LunarMaceListener(LunarPlugin plugin) {
        this.plugin = plugin;
        this.lunarItems = plugin.getLunarItems();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Right-click only (both air and block)
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack held = player.getInventory().getItemInMainHand();

        if (!lunarItems.isLunarMace(held)) return;

        event.setCancelled(true); // prevent any default right-click behaviour

        // ── Cooldown check ───────────────────────────────────────────────────
        int cooldownSeconds = plugin.getConfig().getInt("lunar-mace.ability.cooldown", 60);
        long cooldownMillis = cooldownSeconds * 1000L;
        long now = System.currentTimeMillis();
        UUID uuid = player.getUniqueId();

        if (cooldowns.containsKey(uuid)) {
            long elapsed = now - cooldowns.get(uuid);
            if (elapsed < cooldownMillis) {
                long remaining = (cooldownMillis - elapsed) / 1000 + 1;
                player.sendMessage(
                    Component.text("✦ Lunar Mace ability on cooldown! ", NamedTextColor.RED)
                             .decoration(TextDecoration.ITALIC, false)
                             .append(Component.text(remaining + "s remaining.", NamedTextColor.YELLOW)
                                              .decoration(TextDecoration.ITALIC, false))
                );
                return;
            }
        }

        // ── Activate ability ─────────────────────────────────────────────────
        cooldowns.put(uuid, now);
        activateLunarNight(player);
    }

    private void activateLunarNight(Player player) {
        int nightDuration  = plugin.getConfig().getInt("lunar-mace.ability.night-duration", 20);
        int effectDuration = plugin.getConfig().getInt("lunar-mace.ability.effect-duration", 20);
        int amplifier      = plugin.getConfig().getInt("lunar-mace.ability.effect-amplifier", 1);

        World world = player.getWorld();
        long originalTime = world.getTime();

        // Force night (18000 ticks = midnight)
        world.setTime(18000);

        player.sendMessage(
            Component.text("✦ ", NamedTextColor.AQUA)
                     .decoration(TextDecoration.ITALIC, false)
                     .append(Component.text("The Lunar Mace calls forth the night!", NamedTextColor.LIGHT_PURPLE)
                                      .decoration(TextDecoration.ITALIC, false))
        );

        // Apply Speed II and Jump Boost II (duration in ticks: seconds × 20)
        int durationTicks = effectDuration * 20;
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,      durationTicks, amplifier, false, true, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, durationTicks, amplifier, false, true, true));

        // Restore time after the ability ends
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) return;

                // Restore to original time + nightDuration seconds (converted to ticks: ×20 then mod 24000)
                long restoredTime = (originalTime + (long) nightDuration * 20) % 24000;
                player.getWorld().setTime(restoredTime);

                player.sendMessage(
                    Component.text("✦ ", NamedTextColor.AQUA)
                             .decoration(TextDecoration.ITALIC, false)
                             .append(Component.text("The night recedes.", NamedTextColor.GRAY)
                                              .decoration(TextDecoration.ITALIC, false))
                );
            }
        }.runTaskLater(plugin, nightDuration * 20L);
    }

    public Map<UUID, Long> getCooldowns() {
        return cooldowns;
    }
}
