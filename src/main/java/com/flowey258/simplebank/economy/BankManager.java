package com.flowey258.simplebank.economy;

import com.flowey258.simplebank.SimpleBank;

import java.util.*;
import java.util.stream.Collectors;

public class BankManager {

    private final SimpleBank plugin;
    private final Map<UUID, Double> bankBalances;

    public BankManager(SimpleBank plugin) {
        this.plugin = plugin;
        this.bankBalances = new HashMap<>();
        loadBankData();
    }

    public double getBankBalance(UUID uuid) {
        return bankBalances.getOrDefault(uuid, 0.0);
    }

    public void setBankBalance(UUID uuid, double amount) {
        if (amount <= 0) {
            bankBalances.remove(uuid);
        } else {
            bankBalances.put(uuid, amount);
        }
        plugin.getDataManager().saveBankData(uuid, amount);
    }

    public void addToBankBalance(UUID uuid, double amount) {
        double currentBalance = getBankBalance(uuid);
        setBankBalance(uuid, currentBalance + amount);
    }

    public void removeFromBankBalance(UUID uuid, double amount) {
        double currentBalance = getBankBalance(uuid);
        setBankBalance(uuid, Math.max(0, currentBalance - amount));
    }

    public boolean hasBankBalance(UUID uuid, double amount) {
        return getBankBalance(uuid) >= amount;
    }

    public Set<UUID> getAllBankAccounts() {
        return new HashSet<>(bankBalances.keySet());
    }

    public List<BankEntry> getTopBalances(int limit) {
        return bankBalances.entrySet().stream()
                .map(entry -> new BankEntry(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> Double.compare(b.getBalance(), a.getBalance()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public double getTotalBankMoney() {
        return bankBalances.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    private void loadBankData() {
        Map<UUID, Double> data = plugin.getDataManager().loadAllBankData();
        bankBalances.putAll(data);
    }

    public void applyInterest() {
        if (!plugin.getConfig().getBoolean("interest.enabled", false)) {
            return;
        }

        double interestRate = plugin.getConfig().getDouble("interest.rate", 0.01);
        double minBalance = plugin.getConfig().getDouble("interest.minimum-balance", 1000);
        double maxInterest = plugin.getConfig().getDouble("interest.maximum-interest", 10000);

        for (Map.Entry<UUID, Double> entry : bankBalances.entrySet()) {
            UUID uuid = entry.getKey();
            double currentBalance = entry.getValue();

            // Skip if balance is below minimum
            if (currentBalance < minBalance) {
                continue;
            }

            // Skip if player has interest exemption
            if (plugin.getServer().getOfflinePlayer(uuid).isOnline()) {
                if (plugin.getServer().getPlayer(uuid).hasPermission("bank.interest.exempt")) {
                    continue;
                }
            }

            double interest = currentBalance * interestRate;

            // Cap interest at maximum
            if (interest > maxInterest) {
                interest = maxInterest;
            }

            // Apply interest
            setBankBalance(uuid, currentBalance + interest);

            // Notify player if online
            if (plugin.getServer().getOfflinePlayer(uuid).isOnline()) {
                String message = plugin.getConfig().getString("messages.interest-earned")
                        .replace("{amount}", String.format("%.2f", interest))
                        .replace("{balance}", String.format("%.2f", currentBalance + interest));
                plugin.getServer().getPlayer(uuid).sendMessage(message);
            }
        }
    }

    public static class BankEntry {
        private final UUID uuid;
        private final double balance;

        public BankEntry(UUID uuid, double balance) {
            this.uuid = uuid;
            this.balance = balance;
        }

        public UUID getUuid() {
            return uuid;
        }

        public double getBalance() {
            return balance;
        }
    }
}