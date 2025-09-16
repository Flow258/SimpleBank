package com.flowey258.simplebank.api;

import com.flowey258.simplebank.SimpleBank;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * API class for other plugins to interact with SimpleBank
 */
public class BankAPI {

    private static SimpleBank plugin;

    /**
     * Initialize the API with the plugin instance
     * This is called automatically when the plugin loads
     */
    public static void initialize(SimpleBank pluginInstance) {
        plugin = pluginInstance;
    }

    /**
     * Get a player's bank balance
     * @param uuid The player's UUID
     * @return The player's bank balance
     */
    public static double getBankBalance(UUID uuid) {
        if (plugin == null) return 0.0;
        return plugin.getBankManager().getBankBalance(uuid);
    }

    /**
     * Get a player's bank balance
     * @param player The offline player
     * @return The player's bank balance
     */
    public static double getBankBalance(OfflinePlayer player) {
        return getBankBalance(player.getUniqueId());
    }

    /**
     * Set a player's bank balance
     * @param uuid The player's UUID
     * @param amount The new balance
     * @return True if successful
     */
    public static boolean setBankBalance(UUID uuid, double amount) {
        if (plugin == null) return false;
        if (amount < 0) return false;

        plugin.getBankManager().setBankBalance(uuid, amount);
        return true;
    }

    /**
     * Set a player's bank balance
     * @param player The offline player
     * @param amount The new balance
     * @return True if successful
     */
    public static boolean setBankBalance(OfflinePlayer player, double amount) {
        return setBankBalance(player.getUniqueId(), amount);
    }

    /**
     * Add money to a player's bank balance
     * @param uuid The player's UUID
     * @param amount The amount to add
     * @return True if successful
     */
    public static boolean addToBankBalance(UUID uuid, double amount) {
        if (plugin == null) return false;
        if (amount <= 0) return false;

        plugin.getBankManager().addToBankBalance(uuid, amount);
        return true;
    }

    /**
     * Add money to a player's bank balance
     * @param player The offline player
     * @param amount The amount to add
     * @return True if successful
     */
    public static boolean addToBankBalance(OfflinePlayer player, double amount) {
        return addToBankBalance(player.getUniqueId(), amount);
    }

    /**
     * Remove money from a player's bank balance
     * @param uuid The player's UUID
     * @param amount The amount to remove
     * @return True if successful
     */
    public static boolean removeFromBankBalance(UUID uuid, double amount) {
        if (plugin == null) return false;
        if (amount <= 0) return false;

        double currentBalance = plugin.getBankManager().getBankBalance(uuid);
        if (currentBalance < amount) return false;

        plugin.getBankManager().removeFromBankBalance(uuid, amount);
        return true;
    }

    /**
     * Remove money from a player's bank balance
     * @param player The offline player
     * @param amount The amount to remove
     * @return True if successful
     */
    public static boolean removeFromBankBalance(OfflinePlayer player, double amount) {
        return removeFromBankBalance(player.getUniqueId(), amount);
    }

    /**
     * Check if a player has enough money in their bank
     * @param uuid The player's UUID
     * @param amount The amount to check
     * @return True if the player has enough money
     */
    public static boolean hasBankBalance(UUID uuid, double amount) {
        if (plugin == null) return false;
        return plugin.getBankManager().hasBankBalance(uuid, amount);
    }

    /**
     * Check if a player has enough money in their bank
     * @param player The offline player
     * @param amount The amount to check
     * @return True if the player has enough money
     */
    public static boolean hasBankBalance(OfflinePlayer player, double amount) {
        return hasBankBalance(player.getUniqueId(), amount);
    }

    /**
     * Get the total money stored in all bank accounts
     * @return The total bank money
     */
    public static double getTotalBankMoney() {
        if (plugin == null) return 0.0;
        return plugin.getBankManager().getTotalBankMoney();
    }

    /**
     * Check if the plugin is properly loaded
     * @return True if the API is available
     */
    public static boolean isAvailable() {
        return plugin != null;
    }
}
