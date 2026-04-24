package com.lunar.plugin.commands;

import com.lunar.plugin.LunarPlugin;
import com.lunar.plugin.managers.EssenceManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LunarCommand implements CommandExecutor {

    private final EssenceManager essenceManager;

    public LunarCommand(LunarPlugin plugin) {
        this.essenceManager = plugin.getEssenceManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return true;
        }

        long balance = essenceManager.getBalance(player);

        player.sendMessage(
            Component.text("───────────────────────────", NamedTextColor.DARK_AQUA)
                     .decoration(TextDecoration.ITALIC, false)
        );
        player.sendMessage(
            Component.text("        ✦ Lunar Essence ✦", NamedTextColor.AQUA)
                     .decoration(TextDecoration.BOLD, true)
                     .decoration(TextDecoration.ITALIC, false)
        );
        player.sendMessage(Component.empty());
        player.sendMessage(
            Component.text("  Balance: ", NamedTextColor.GRAY)
                     .decoration(TextDecoration.ITALIC, false)
                     .append(Component.text(balance + " Lunar Essence", NamedTextColor.AQUA)
                                      .decoration(TextDecoration.ITALIC, false)
                                      .decoration(TextDecoration.BOLD, true))
        );
        player.sendMessage(Component.empty());
        player.sendMessage(
            Component.text("  /withdraw <amount>", NamedTextColor.YELLOW)
                     .decoration(TextDecoration.ITALIC, false)
                     .append(Component.text(" — take essence as items", NamedTextColor.GRAY)
                                      .decoration(TextDecoration.ITALIC, false))
        );
        player.sendMessage(
            Component.text("  /compact [amount|all]", NamedTextColor.LIGHT_PURPLE)
                     .decoration(TextDecoration.ITALIC, false)
                     .append(Component.text(" — compress 7 into 1", NamedTextColor.GRAY)
                                      .decoration(TextDecoration.ITALIC, false))
        );
        player.sendMessage(
            Component.text("───────────────────────────", NamedTextColor.DARK_AQUA)
                     .decoration(TextDecoration.ITALIC, false)
        );

        return true;
    }
}
