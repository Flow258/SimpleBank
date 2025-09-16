package com.flowey258.simplebank.listeners;

import com.flowey258.simplebank.SimpleBank;
import com.flowey258.simplebank.utils.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final SimpleBank plugin;

    public PlayerListener(SimpleBank plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Check if player has any bank balance and notify them
        double bankBalance = plugin.getBankManager().getBankBalance(event.getPlayer().getUniqueId());

        if (bankBalance > 0 && plugin.getConfig().getBoolean("notifications.login-balance", true)) {
            // Delay the message slightly so it doesn't get lost in join messages
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                String message = plugin.getConfig().getString("messages.login-notification")
                        .replace("{balance}", String.format("%.2f", bankBalance));
                event.getPlayer().sendMessage(MessageUtils.colorize(message));
            }, 40L); // 2 seconds delay
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Save player data when they leave (optional, data is already saved on changes)
        // This is just an extra safety measure
        plugin.getDataManager().saveData();
    }
}