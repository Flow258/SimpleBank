package com.flowey258.simplebank;

import com.flowey258.simplebank.commands.BankCommand;
import com.flowey258.simplebank.data.DataManager;
import com.flowey258.simplebank.economy.BankManager;
import com.flowey258.simplebank.listeners.PlayerListener;
import com.flowey258.simplebank.tasks.InterestTask;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleBank extends JavaPlugin {

    private static SimpleBank instance;
    private Economy economy = null;
    private DataManager dataManager;
    private BankManager bankManager;
    private InterestTask interestTask;

    @Override
    public void onEnable() {
        instance = this;

        // Check for Vault
        if (!setupEconomy()) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Save default config
        saveDefaultConfig();

        // Initialize managers
        dataManager = new DataManager(this);
        bankManager = new BankManager(this);

        // Register commands
        getCommand("bank").setExecutor(new BankCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        // Start interest task if enabled
        if (getConfig().getBoolean("interest.enabled", false)) {
            interestTask = new InterestTask(this);
            interestTask.start();
        }

        getLogger().info("SimpleBank has been enabled!");
    }

    @Override
    public void onDisable() {
        // Stop interest task
        if (interestTask != null) {
            interestTask.stop();
        }

        // Save data
        if (dataManager != null) {
            dataManager.saveData();
        }

        getLogger().info("SimpleBank has been disabled!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public static SimpleBank getInstance() {
        return instance;
    }

    public Economy getEconomy() {
        return economy;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public BankManager getBankManager() {
        return bankManager;
    }
}
