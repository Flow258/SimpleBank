package com.flowey258.simplebank.data;

import com.flowey258.simplebank.SimpleBank;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class DataManager {

    private final SimpleBank plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;
    private final Map<UUID, Double> pendingSaves;

    public DataManager(SimpleBank plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "bankdata.yml");
        this.pendingSaves = new HashMap<>();

        createDataFile();
        loadDataConfig();
    }

    private void createDataFile() {
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create bank data file!", e);
            }
        }
    }

    private void loadDataConfig() {
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void saveBankData(UUID uuid, double balance) {
        // Store in pending saves for batch processing
        if (balance <= 0) {
            pendingSaves.put(uuid, null); // Mark for removal
        } else {
            pendingSaves.put(uuid, balance);
        }

        // Save immediately for now (can be optimized to batch saves)
        saveDataImmediately();
    }

    private void saveDataImmediately() {
        try {
            // Apply pending saves
            for (Map.Entry<UUID, Double> entry : pendingSaves.entrySet()) {
                String key = "players." + entry.getKey().toString() + ".bank-balance";
                if (entry.getValue() == null || entry.getValue() <= 0) {
                    dataConfig.set("players." + entry.getKey().toString(), null);
                } else {
                    dataConfig.set(key, entry.getValue());
                }
            }

            dataConfig.save(dataFile);
            pendingSaves.clear();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save bank data!", e);
        }
    }

    public double loadBankData(UUID uuid) {
        String key = "players." + uuid.toString() + ".bank-balance";
        return dataConfig.getDouble(key, 0.0);
    }

    public Map<UUID, Double> loadAllBankData() {
        Map<UUID, Double> data = new HashMap<>();

        if (!dataConfig.contains("players")) {
            return data;
        }

        for (String uuidString : dataConfig.getConfigurationSection("players").getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                double balance = dataConfig.getDouble("players." + uuidString + ".bank-balance", 0.0);
                if (balance > 0) {
                    data.put(uuid, balance);
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID in bank data: " + uuidString);
            }
        }

        return data;
    }

    public void saveData() {
        saveDataImmediately();
    }

    public void reloadData() {
        loadDataConfig();
    }

    public void backupData() {
        File backupFile = new File(plugin.getDataFolder(), "bankdata_backup_" + System.currentTimeMillis() + ".yml");
        try {
            dataConfig.save(backupFile);
            plugin.getLogger().info("Bank data backed up to: " + backupFile.getName());
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create backup!", e);
        }
    }
}