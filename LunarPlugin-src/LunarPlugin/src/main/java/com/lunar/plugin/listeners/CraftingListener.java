package com.lunar.plugin.listeners;

import com.lunar.plugin.LunarPlugin;
import com.lunar.plugin.items.LunarItems;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Validates that the Lunar Mace recipe only accepts genuine
 * Compacted Lunar Essence (identified by PersistentDataContainer),
 * not plain Turtle Scutes.
 */
public class CraftingListener implements Listener {

    private final LunarPlugin plugin;
    private final LunarItems lunarItems;

    public CraftingListener(LunarPlugin plugin) {
        this.plugin = plugin;
        this.lunarItems = plugin.getLunarItems();
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) return;

        ItemStack result = event.getRecipe().getResult();
        if (result == null) return;

        // Guard any recipe whose result contains a Compacted Essence ingredient slot
        boolean isMace      = lunarItems.isLunarMace(result);
        boolean isLongsword = lunarItems.isLunarLongsword(result);

        if (!isMace && !isLongsword) return;

        // Verify that every Turtle Scute slot contains a real Compacted Lunar Essence
        for (ItemStack ingredient : event.getInventory().getMatrix()) {
            if (ingredient == null) continue;
            if (ingredient.getType() == org.bukkit.Material.TURTLE_SCUTE) {
                if (!lunarItems.isCompactedEssence(ingredient)) {
                    event.getInventory().setResult(null);
                    return;
                }
            }
        }
    }
}
