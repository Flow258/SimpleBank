package com.flowey258.simplebank.utils;

import org.bukkit.ChatColor;

public class MessageUtils {

    /**
     * Colorizes a string with Minecraft color codes
     * @param message The message to colorize
     * @return The colorized message
     */
    public static String colorize(String message) {
        if (message == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Formats a number with commas for better readability
     * @param number The number to format
     * @return The formatted number as a string
     */
    public static String formatNumber(double number) {
        return String.format("%,.2f", number);
    }

    /**
     * Formats currency with the server's currency symbol
     * @param amount The amount to format
     * @return The formatted currency string
     */
    public static String formatCurrency(double amount) {
        return "$" + formatNumber(amount);
    }

    /**
     * Strips color codes from a message
     * @param message The message to strip
     * @return The message without color codes
     */
    public static String stripColor(String message) {
        if (message == null) {
            return "";
        }
        return ChatColor.stripColor(colorize(message));
    }

    /**
     * Centers a message for chat display
     * @param message The message to center
     * @param length The target length
     * @return The centered message
     */
    public static String centerMessage(String message, int length) {
        if (message == null || message.length() >= length) {
            return message;
        }

        int padding = (length - stripColor(message).length()) / 2;
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < padding; i++) {
            builder.append(" ");
        }

        builder.append(message);
        return builder.toString();
    }

    /**
     * Creates a line separator for messages
     * @param character The character to use
     * @param length The length of the line
     * @param color The color of the line
     * @return The formatted separator line
     */
    public static String createSeparatorLine(char character, int length, ChatColor color) {
        StringBuilder builder = new StringBuilder();
        builder.append(color);

        for (int i = 0; i < length; i++) {
            builder.append(character);
        }

        return builder.toString();
    }
}