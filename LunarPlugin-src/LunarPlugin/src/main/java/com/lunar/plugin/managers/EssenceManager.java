package com.lunar.plugin.managers;

import com.lunar.plugin.LunarPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EssenceManager {

    private final LunarPlugin plugin;
    private final Map<UUID, Long> essenceBalances = new HashMap<>();
    private File dataFile;
    private FileConfiguration dataConfig;

    public EssenceManager(LunarPlugin plugin) {
        this.plugin = plugin;
        loadData();
    }

    // ── Balance operations ──────────────────────────────────────────────────

    public long getBalance(Player player) {
        return essenceBalances.getOrDefault(player.getUniqueId(), 0L);
    }

    public void addEssence(Player player, long amount) {
        UUID uuid = player.getUniqueId();
        essenceBalances.put(uuid, essenceBalances.getOrDefault(uuid, 0L) + amount);
    }

    /**
     * Attempts to remove essence. Returns true if successful, false if insufficient funds.
     */
    public boolean removeEssence(Player player, long amount) {
        long current = getBalance(player);
        if (current < amount) return false;
        essenceBalances.put(player.getUniqueId(), current - amount);
        return true;
    }

    public boolean hasEssence(Player player, long amount) {
        return getBalance(player) >= amount;
    }

    // ── Persistence ─────────────────────────────────────────────────────────

    private void loadData() {
        dataFile = new File(plugin.getDataFolder(), "essence_data.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create essence_data.yml: " + e.getMessage());
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        if (dataConfig.contains("balances")) {
            for (String key : dataConfig.getConfigurationSection("balances").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    long balance = dataConfig.getLong("balances." + key);
                    essenceBalances.put(uuid, balance);
                } catch (IllegalArgumentException ignored) {}
            }
        }
        plugin.getLogger().info("Loaded " + essenceBalances.size() + " player essence balances.");
    }

    public void saveAllData() {
        for (Map.Entry<UUID, Long> entry : essenceBalances.entrySet()) {
            dataConfig.set("balances." + entry.getKey().toString(), entry.getValue());
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save essence_data.yml: " + e.getMessage());
        }
    }
}
