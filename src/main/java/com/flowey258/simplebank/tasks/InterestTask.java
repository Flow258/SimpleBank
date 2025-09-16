package com.flowey258.simplebank.tasks;

import com.flowey258.simplebank.SimpleBank;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class InterestTask {

    private final SimpleBank plugin;
    private BukkitTask task;

    public InterestTask(SimpleBank plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }

        // Get interval from config (in minutes, convert to ticks)
        long intervalMinutes = plugin.getConfig().getLong("interest.interval-minutes", 1440); // Default 24 hours
        long intervalTicks = intervalMinutes * 60 * 20; // Convert to ticks

        task = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    plugin.getBankManager().applyInterest();
                    plugin.getLogger().info("Interest applied to all eligible bank accounts.");
                } catch (Exception e) {
                    plugin.getLogger().severe("Error applying interest: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(plugin, intervalTicks, intervalTicks);

        plugin.getLogger().info("Interest task started. Interval: " + intervalMinutes + " minutes");
    }

    public void stop() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
            plugin.getLogger().info("Interest task stopped.");
        }
    }

    public boolean isRunning() {
        return task != null && !task.isCancelled();
    }

    public void runNow() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                plugin.getBankManager().applyInterest();
                plugin.getLogger().info("Manual interest application completed.");
            } catch (Exception e) {
                plugin.getLogger().severe("Error during manual interest application: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}