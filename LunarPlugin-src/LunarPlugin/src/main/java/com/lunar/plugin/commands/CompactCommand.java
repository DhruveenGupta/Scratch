package com.lunar.plugin.commands;

import com.lunar.plugin.LunarPlugin;
import com.lunar.plugin.managers.EssenceManager;
import com.lunar.plugin.items.LunarItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CompactCommand implements CommandExecutor {

    private final LunarPlugin plugin;
    private final EssenceManager essenceManager;
    private final LunarItems lunarItems;

    public CompactCommand(LunarPlugin plugin) {
        this.plugin = plugin;
        this.essenceManager = plugin.getEssenceManager();
        this.lunarItems = plugin.getLunarItems();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return true;
        }

        int compactCost = plugin.getConfig().getInt("essence.compact-amount", 7);
        long balance    = essenceManager.getBalance(player);

        // Default: compact as many as possible (1 if none specified)
        int compactedAmount = 1;

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("all")) {
                compactedAmount = (int) (balance / compactCost);
            } else {
                try {
                    compactedAmount = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    player.sendMessage(Component.text("Please enter a valid number or 'all'.", NamedTextColor.RED)
                                                .decoration(TextDecoration.ITALIC, false));
                    return true;
                }
            }
        }

        if (compactedAmount <= 0) {
            player.sendMessage(Component.text("Amount must be greater than zero.", NamedTextColor.RED)
                                        .decoration(TextDecoration.ITALIC, false));
            return true;
        }

        long totalCost = (long) compactedAmount * compactCost;

        if (!essenceManager.hasEssence(player, totalCost)) {
            long canMake = balance / compactCost;
            player.sendMessage(
                Component.text("✦ Not enough Lunar Essence! You need ", NamedTextColor.RED)
                         .decoration(TextDecoration.ITALIC, false)
                         .append(Component.text(totalCost + "", NamedTextColor.YELLOW)
                                          .decoration(TextDecoration.ITALIC, false))
                         .append(Component.text(" but have ", NamedTextColor.RED)
                                          .decoration(TextDecoration.ITALIC, false))
                         .append(Component.text(balance + ".", NamedTextColor.YELLOW)
                                          .decoration(TextDecoration.ITALIC, false))
            );
            if (canMake > 0) {
                player.sendMessage(
                    Component.text("  You can compact up to ", NamedTextColor.GRAY)
                             .decoration(TextDecoration.ITALIC, false)
                             .append(Component.text(canMake + " Compacted Lunar Essence.", NamedTextColor.AQUA)
                                              .decoration(TextDecoration.ITALIC, false))
                );
            }
            return true;
        }

        // Inventory space check (max 64 per stack)
        if (compactedAmount > 64) {
            player.sendMessage(Component.text("You can compact at most 64 at a time.", NamedTextColor.RED)
                                        .decoration(TextDecoration.ITALIC, false));
            return true;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Component.text("Your inventory is full!", NamedTextColor.RED)
                                        .decoration(TextDecoration.ITALIC, false));
            return true;
        }

        essenceManager.removeEssence(player, totalCost);
        ItemStack compacted = lunarItems.buildCompactedEssence(compactedAmount);
        player.getInventory().addItem(compacted);
        essenceManager.saveAllData();

        player.sendMessage(
            Component.text("✦ ", NamedTextColor.LIGHT_PURPLE)
                     .decoration(TextDecoration.ITALIC, false)
                     .append(Component.text("Compacted ", NamedTextColor.WHITE)
                                      .decoration(TextDecoration.ITALIC, false))
                     .append(Component.text(totalCost + " Lunar Essence", NamedTextColor.AQUA)
                                      .decoration(TextDecoration.ITALIC, false))
                     .append(Component.text(" into ", NamedTextColor.WHITE)
                                      .decoration(TextDecoration.ITALIC, false))
                     .append(Component.text(compactedAmount + " Compacted Lunar Essence.", NamedTextColor.LIGHT_PURPLE)
                                      .decoration(TextDecoration.ITALIC, false))
        );
        player.sendMessage(
            Component.text("  Remaining balance: ", NamedTextColor.GRAY)
                     .decoration(TextDecoration.ITALIC, false)
                     .append(Component.text(essenceManager.getBalance(player) + " Lunar Essence", NamedTextColor.AQUA)
                                      .decoration(TextDecoration.ITALIC, false))
        );
        return true;
    }
}
