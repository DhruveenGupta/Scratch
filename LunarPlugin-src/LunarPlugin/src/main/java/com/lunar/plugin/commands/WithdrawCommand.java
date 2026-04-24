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

public class WithdrawCommand implements CommandExecutor {

    private final LunarPlugin plugin;
    private final EssenceManager essenceManager;
    private final LunarItems lunarItems;

    public WithdrawCommand(LunarPlugin plugin) {
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

        long balance = essenceManager.getBalance(player);

        // /withdraw with no args → show balance
        if (args.length == 0) {
            player.sendMessage(
                Component.text("✦ ", NamedTextColor.AQUA)
                         .decoration(TextDecoration.ITALIC, false)
                         .append(Component.text("You have ", NamedTextColor.WHITE)
                                          .decoration(TextDecoration.ITALIC, false))
                         .append(Component.text(balance + " Lunar Essence", NamedTextColor.AQUA)
                                          .decoration(TextDecoration.ITALIC, false))
                         .append(Component.text(" stored.", NamedTextColor.WHITE)
                                          .decoration(TextDecoration.ITALIC, false))
            );
            player.sendMessage(
                Component.text("Usage: /withdraw <amount>", NamedTextColor.GRAY)
                         .decoration(TextDecoration.ITALIC, false)
            );
            return true;
        }

        // Parse amount
        long amount;
        try {
            amount = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Please enter a valid number.", NamedTextColor.RED)
                                        .decoration(TextDecoration.ITALIC, false));
            return true;
        }

        if (amount <= 0) {
            player.sendMessage(Component.text("Amount must be greater than zero.", NamedTextColor.RED)
                                        .decoration(TextDecoration.ITALIC, false));
            return true;
        }

        if (amount > 64) {
            player.sendMessage(Component.text("You can withdraw at most 64 at a time.", NamedTextColor.RED)
                                        .decoration(TextDecoration.ITALIC, false));
            return true;
        }

        if (!essenceManager.hasEssence(player, amount)) {
            player.sendMessage(
                Component.text("✦ Insufficient Lunar Essence! You only have ", NamedTextColor.RED)
                         .decoration(TextDecoration.ITALIC, false)
                         .append(Component.text(balance + ".", NamedTextColor.YELLOW)
                                          .decoration(TextDecoration.ITALIC, false))
            );
            return true;
        }

        // Check inventory space
        ItemStack essenceItem = lunarItems.buildLunarEssence((int) amount);
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Component.text("Your inventory is full!", NamedTextColor.RED)
                                        .decoration(TextDecoration.ITALIC, false));
            return true;
        }

        essenceManager.removeEssence(player, amount);
        player.getInventory().addItem(essenceItem);
        essenceManager.saveAllData();

        player.sendMessage(
            Component.text("✦ ", NamedTextColor.AQUA)
                     .decoration(TextDecoration.ITALIC, false)
                     .append(Component.text("Withdrew ", NamedTextColor.WHITE)
                                      .decoration(TextDecoration.ITALIC, false))
                     .append(Component.text(amount + " Lunar Essence", NamedTextColor.AQUA)
                                      .decoration(TextDecoration.ITALIC, false))
                     .append(Component.text(". Balance: ", NamedTextColor.WHITE)
                                      .decoration(TextDecoration.ITALIC, false))
                     .append(Component.text(essenceManager.getBalance(player), NamedTextColor.LIGHT_PURPLE)
                                      .decoration(TextDecoration.ITALIC, false))
        );
        return true;
    }
}
