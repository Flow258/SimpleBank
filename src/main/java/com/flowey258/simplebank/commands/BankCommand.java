package com.flowey258.simplebank.commands;

import com.flowey258.simplebank.SimpleBank;
import com.flowey258.simplebank.economy.BankManager;
import com.flowey258.simplebank.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BankCommand implements CommandExecutor, TabCompleter {

    private final SimpleBank plugin;
    private final BankManager bankManager;

    public BankCommand(SimpleBank plugin) {
        this.plugin = plugin;
        this.bankManager = plugin.getBankManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.colorize("&cThis command can only be used by players!"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("bank.use")) {
            player.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.no-permission")));
            return true;
        }

        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "balance":
            case "bal":
                handleBalance(player, args);
                break;
            case "deposit":
            case "dep":
                handleDeposit(player, args);
                break;
            case "withdraw":
            case "wd":
                handleWithdraw(player, args);
                break;
            case "set":
                handleSet(player, args);
                break;
            case "reset":
                handleReset(player, args);
                break;
            case "top":
                handleTop(player);
                break;
            case "help":
            default:
                sendHelpMessage(player);
                break;
        }

        return true;
    }

    private void handleBalance(Player player, String[] args) {
        if (!player.hasPermission("bank.balance")) {
            player.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.no-permission")));
            return;
        }

        // Check if checking another player's balance
        if (args.length > 1 && player.hasPermission("bank.admin")) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            if (!target.hasPlayedBefore() && !target.isOnline()) {
                player.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.player-not-found")));
                return;
            }
            double balance = bankManager.getBankBalance(target.getUniqueId());
            String message = plugin.getConfig().getString("messages.balance-other")
                    .replace("{player}", target.getName())
                    .replace("{balance}", String.format("%.2f", balance));
            player.sendMessage(MessageUtils.colorize(message));
        } else {
            double balance = bankManager.getBankBalance(player.getUniqueId());
            String message = plugin.getConfig().getString("messages.balance")
                    .replace("{balance}", String.format("%.2f", balance));
            player.sendMessage(MessageUtils.colorize(message));
        }
    }

    private void handleDeposit(Player player, String[] args) {
        if (!player.hasPermission("bank.deposit")) {
            player.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.no-permission")));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(MessageUtils.colorize("&cUsage: /bank deposit <amount>"));
            return;
        }

        double amount;
        try {
            if (args[1].equalsIgnoreCase("all")) {
                amount = plugin.getEconomy().getBalance(player);
            } else {
                amount = Double.parseDouble(args[1]);
            }
        } catch (NumberFormatException e) {
            player.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.invalid-amount")));
            return;
        }

        if (amount <= 0) {
            player.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.invalid-amount")));
            return;
        }

        // Check if player has enough money
        if (plugin.getEconomy().getBalance(player) < amount) {
            player.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.insufficient-funds")));
            return;
        }

        // Check bank balance limit
        double currentBankBalance = bankManager.getBankBalance(player.getUniqueId());
        double maxBalance = plugin.getConfig().getDouble("bank.max-balance", -1);

        if (maxBalance > 0 && (currentBankBalance + amount) > maxBalance) {
            String message = plugin.getConfig().getString("messages.bank-limit-reached")
                    .replace("{limit}", String.format("%.2f", maxBalance));
            player.sendMessage(MessageUtils.colorize(message));
            return;
        }

        // Perform the transaction
        plugin.getEconomy().withdrawPlayer(player, amount);
        bankManager.addToBankBalance(player.getUniqueId(), amount);

        String message = plugin.getConfig().getString("messages.deposit-success")
                .replace("{amount}", String.format("%.2f", amount))
                .replace("{balance}", String.format("%.2f", currentBankBalance + amount));
        player.sendMessage(MessageUtils.colorize(message));
    }

    private void handleWithdraw(Player player, String[] args) {
        if (!player.hasPermission("bank.withdraw")) {
            player.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.no-permission")));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(MessageUtils.colorize("&cUsage: /bank withdraw <amount>"));
            return;
        }

        double amount;
        double currentBankBalance = bankManager.getBankBalance(player.getUniqueId());

        try {
            if (args[1].equalsIgnoreCase("all")) {
                amount = currentBankBalance;
            } else {
                amount = Double.parseDouble(args[1]);
            }
        } catch (NumberFormatException e) {
            player.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.invalid-amount")));
            return;
        }

        if (amount <= 0) {
            player.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.invalid-amount")));
            return;
        }

        // Check if player has enough in bank
        if (currentBankBalance < amount) {
            player.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.insufficient-bank-funds")));
            return;
        }

        // Perform the transaction
        bankManager.removeFromBankBalance(player.getUniqueId(), amount);
        plugin.getEconomy().depositPlayer(player, amount);

        String message = plugin.getConfig().getString("messages.withdraw-success")
                .replace("{amount}", String.format("%.2f", amount))
                .replace("{balance}", String.format("%.2f", currentBankBalance - amount));
        player.sendMessage(MessageUtils.colorize(message));
    }

    private void handleSet(Player player, String[] args) {
        if (!player.hasPermission("bank.admin.set")) {
            player.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.no-permission")));
            return;
        }

        if (args.length < 3) {
            player.sendMessage(MessageUtils.colorize("&cUsage: /bank set <player> <amount>"));
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            player.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.player-not-found")));
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.invalid-amount")));
            return;
        }

        if (amount < 0) {
            player.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.invalid-amount")));
            return;
        }

        bankManager.setBankBalance(target.getUniqueId(), amount);

        String message = plugin.getConfig().getString("messages.admin-set")
                .replace("{player}", target.getName())
                .replace("{amount}", String.format("%.2f", amount));
        player.sendMessage(MessageUtils.colorize(message));
    }

    private void handleReset(Player player, String[] args) {
        if (!player.hasPermission("bank.admin.reset")) {
            player.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.no-permission")));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(MessageUtils.colorize("&cUsage: /bank reset <player>"));
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            player.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.player-not-found")));
            return;
        }

        bankManager.setBankBalance(target.getUniqueId(), 0);

        String message = plugin.getConfig().getString("messages.admin-reset")
                .replace("{player}", target.getName());
        player.sendMessage(MessageUtils.colorize(message));
    }

    private void handleTop(Player player) {
        if (!player.hasPermission("bank.admin")) {
            player.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.no-permission")));
            return;
        }

        List<BankManager.BankEntry> topBalances = bankManager.getTopBalances(10);

        player.sendMessage(MessageUtils.colorize("&e&l=== Top Bank Balances ==="));
        for (int i = 0; i < topBalances.size(); i++) {
            BankManager.BankEntry entry = topBalances.get(i);
            OfflinePlayer p = Bukkit.getOfflinePlayer(entry.getUuid());
            player.sendMessage(MessageUtils.colorize(String.format("&7%d. &f%s &7- &e$%.2f",
                    i + 1, p.getName(), entry.getBalance())));
        }
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(MessageUtils.colorize("&e&l=== Bank Commands ==="));
        player.sendMessage(MessageUtils.colorize("&7/bank balance &f- Check your bank balance"));
        player.sendMessage(MessageUtils.colorize("&7/bank deposit <amount|all> &f- Deposit money to bank"));
        player.sendMessage(MessageUtils.colorize("&7/bank withdraw <amount|all> &f- Withdraw money from bank"));

        if (player.hasPermission("bank.admin")) {
            player.sendMessage(MessageUtils.colorize("&c&lAdmin Commands:"));
            player.sendMessage(MessageUtils.colorize("&7/bank balance <player> &f- Check player's balance"));
            player.sendMessage(MessageUtils.colorize("&7/bank set <player> <amount> &f- Set player's balance"));
            player.sendMessage(MessageUtils.colorize("&7/bank reset <player> &f- Reset player's balance"));
            player.sendMessage(MessageUtils.colorize("&7/bank top &f- View top balances"));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("balance", "deposit", "withdraw", "help");
            if (sender.hasPermission("bank.admin")) {
                subCommands = Arrays.asList("balance", "deposit", "withdraw", "set", "reset", "top", "help");
            }

            for (String subCommand : subCommands) {
                if (subCommand.startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("deposit") || args[0].equalsIgnoreCase("withdraw")) {
                completions.add("all");
                completions.add("100");
                completions.add("1000");
                completions.add("10000");
            } else if (sender.hasPermission("bank.admin") &&
                    (args[0].equalsIgnoreCase("balance") || args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("reset"))) {
                // Add online player names
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(player.getName());
                    }
                }
            }
        }

        return completions;
    }
}