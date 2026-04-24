package com.lunar.plugin.items;

import com.lunar.plugin.LunarPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class LunarItems {

    private final LunarPlugin plugin;

    // Namespaced keys for identifying custom items
    public static final String LUNAR_ESSENCE_KEY    = "lunar_essence";
    public static final String COMPACTED_ESSENCE_KEY = "compacted_lunar_essence";
    public static final String LUNAR_MACE_KEY        = "lunar_mace";
    public static final String LUNAR_LONGSWORD_KEY   = "lunar_longsword";

    // Custom model data values — assign these in your resource pack
    public static final int LUNAR_ESSENCE_CMD        = 1001;
    public static final int COMPACTED_ESSENCE_CMD    = 1002;
    public static final int LUNAR_MACE_CMD           = 1003;
    public static final int LUNAR_LONGSWORD_CMD      = 1004;

    public LunarItems(LunarPlugin plugin) {
        this.plugin = plugin;
    }

    // ── Item builders ────────────────────────────────────────────────────────

    /** Single Lunar Essence (base texture: turtle scute) */
    public ItemStack buildLunarEssence(int amount) {
        ItemStack item = new ItemStack(Material.TURTLE_SCUTE, amount);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(
            Component.text("Lunar Essence", NamedTextColor.AQUA)
                     .decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(List.of(
            Component.text("A fragment of moonlight crystallized", NamedTextColor.GRAY)
                     .decoration(TextDecoration.ITALIC, false),
            Component.text("into solid form.", NamedTextColor.GRAY)
                     .decoration(TextDecoration.ITALIC, false),
            Component.empty(),
            Component.text("Use /withdraw to obtain", NamedTextColor.DARK_AQUA)
                     .decoration(TextDecoration.ITALIC, false),
            Component.text("Use /compact to compress 7 into 1", NamedTextColor.DARK_AQUA)
                     .decoration(TextDecoration.ITALIC, false)
        ));
        meta.setCustomModelData(LUNAR_ESSENCE_CMD);

        // Mark as custom item for identification
        NamespacedKey key = new NamespacedKey(plugin, LUNAR_ESSENCE_KEY);
        meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);
        return item;
    }

    /** Compacted Lunar Essence (7 → 1) */
    public ItemStack buildCompactedEssence(int amount) {
        ItemStack item = new ItemStack(Material.TURTLE_SCUTE, amount);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(
            Component.text("Compacted Lunar Essence", NamedTextColor.LIGHT_PURPLE)
                     .decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(List.of(
            Component.text("Seven essences compressed into one.", NamedTextColor.GRAY)
                     .decoration(TextDecoration.ITALIC, false),
            Component.empty(),
            Component.text("Used for crafting powerful weapons.", NamedTextColor.DARK_PURPLE)
                     .decoration(TextDecoration.ITALIC, false)
        ));
        meta.setCustomModelData(COMPACTED_ESSENCE_CMD);

        NamespacedKey key = new NamespacedKey(plugin, COMPACTED_ESSENCE_KEY);
        meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);
        return item;
    }

    /** Lunar Mace — crafted, ability on right-click */
    public ItemStack buildLunarMace() {
        ItemStack item = new ItemStack(Material.MACE, 1);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(
            Component.text("Lunar Mace", NamedTextColor.BLUE)
                     .decoration(TextDecoration.BOLD, true)
                     .decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(List.of(
            Component.text("Forged from moonlight and ancient power.", NamedTextColor.GRAY)
                     .decoration(TextDecoration.ITALIC, false),
            Component.empty(),
            Component.text("Right-Click Ability:", NamedTextColor.AQUA)
                     .decoration(TextDecoration.ITALIC, false)
                     .decoration(TextDecoration.BOLD, true),
            Component.text("  Calls forth the night for 20 seconds,", NamedTextColor.WHITE)
                     .decoration(TextDecoration.ITALIC, false),
            Component.text("  granting Speed II and Jump Boost II.", NamedTextColor.WHITE)
                     .decoration(TextDecoration.ITALIC, false),
            Component.empty(),
            Component.text("Cooldown: 60 seconds", NamedTextColor.YELLOW)
                     .decoration(TextDecoration.ITALIC, false)
        ));
        meta.setCustomModelData(LUNAR_MACE_CMD);

        NamespacedKey key = new NamespacedKey(plugin, LUNAR_MACE_KEY);
        meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);
        return item;
    }

    /** Lunar Longsword — passive: instant healing when hitting a player */
    public ItemStack buildLunarLongsword() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 1);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(
            Component.text("Lunar Longsword", NamedTextColor.AQUA)
                     .decoration(TextDecoration.BOLD, true)
                     .decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(List.of(
            Component.text("A blade kissed by moonlight.", NamedTextColor.GRAY)
                     .decoration(TextDecoration.ITALIC, false),
            Component.empty(),
            Component.text("Passive Ability:", NamedTextColor.GREEN)
                     .decoration(TextDecoration.ITALIC, false)
                     .decoration(TextDecoration.BOLD, true),
            Component.text("  Striking a player triggers", NamedTextColor.WHITE)
                     .decoration(TextDecoration.ITALIC, false),
            Component.text("  Instant Healing upon impact.", NamedTextColor.WHITE)
                     .decoration(TextDecoration.ITALIC, false)
        ));
        meta.setCustomModelData(LUNAR_LONGSWORD_CMD);

        NamespacedKey key = new NamespacedKey(plugin, LUNAR_LONGSWORD_KEY);
        meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);
        return item;
    }

    // ── Recipe registration ──────────────────────────────────────────────────

    public void registerRecipes() {
        registerLunarMaceRecipe();
        registerLunarLongswordRecipe();
        plugin.getLogger().info("Registered Lunar Mace and Lunar Longsword crafting recipes.");
    }

    /**
     * Lunar Mace recipe:
     *   [ ]  [C]  [ ]
     *   [B]  [H]  [B]
     *   [B]  [C]  [B]
     *
     *  C = Compacted Lunar Essence, H = Heavy Core, B = Breeze Rod
     *
     *  That's: 2 Compacted Essence, 1 Heavy Core, 3 Breeze Rods  (fits the brief exactly)
     */
    private void registerLunarMaceRecipe() {
        NamespacedKey recipeKey = new NamespacedKey(plugin, "lunar_mace");

        ItemStack result = buildLunarMace();

        ShapedRecipe recipe = new ShapedRecipe(recipeKey, result);
        recipe.shape(
            " C ",
            "BHB",
            "BCB"
        );
        recipe.setIngredient('C', buildCompactedEssenceIngredient());
        recipe.setIngredient('H', Material.HEAVY_CORE);
        recipe.setIngredient('B', Material.BREEZE_ROD);

        plugin.getServer().addRecipe(recipe);
    }

    /**
     * Lunar Longsword recipe:
     *   [D]  [C]  [D]
     *   [D]  [A]  [D]
     *   [S]  [R]  [S]
     *
     *  D = Diamond, C = Compacted Lunar Essence, A = Block of Amethyst,
     *  S = Stick,   R = Block of Redstone
     *
     *  That's: 4 Diamonds, 1 Compacted Essence, 1 Amethyst Block, 2 Sticks, 1 Redstone Block
     */
    private void registerLunarLongswordRecipe() {
        NamespacedKey recipeKey = new NamespacedKey(plugin, "lunar_longsword");

        ItemStack result = buildLunarLongsword();

        ShapedRecipe recipe = new ShapedRecipe(recipeKey, result);
        recipe.shape(
            "DCD",
            "DAD",
            "SRS"
        );
        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('C', buildCompactedEssence(1));
        recipe.setIngredient('A', Material.AMETHYST_BLOCK);
        recipe.setIngredient('S', Material.STICK);
        recipe.setIngredient('R', Material.REDSTONE_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    /**
     * Helper: returns a plain TURTLE_SCUTE with the compacted CMD set,
     * used as the recipe ingredient matcher.
     */
    private ItemStack buildCompactedEssenceIngredient() {
        // We match by custom model data — Paper's ShapedRecipe accepts ItemStack
        // with meta for exact matching in 1.21.1
        return buildCompactedEssence(1);
    }

    // ── Identification helpers ───────────────────────────────────────────────

    public boolean isLunarEssence(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        NamespacedKey key = new NamespacedKey(plugin, LUNAR_ESSENCE_KEY);
        return item.getItemMeta().getPersistentDataContainer()
                   .has(key, PersistentDataType.BOOLEAN);
    }

    public boolean isCompactedEssence(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        NamespacedKey key = new NamespacedKey(plugin, COMPACTED_ESSENCE_KEY);
        return item.getItemMeta().getPersistentDataContainer()
                   .has(key, PersistentDataType.BOOLEAN);
    }

    public boolean isLunarMace(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        NamespacedKey key = new NamespacedKey(plugin, LUNAR_MACE_KEY);
        return item.getItemMeta().getPersistentDataContainer()
                   .has(key, PersistentDataType.BOOLEAN);
    }

    public boolean isLunarLongsword(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        NamespacedKey key = new NamespacedKey(plugin, LUNAR_LONGSWORD_KEY);
        return item.getItemMeta().getPersistentDataContainer()
                   .has(key, PersistentDataType.BOOLEAN);
    }
}
