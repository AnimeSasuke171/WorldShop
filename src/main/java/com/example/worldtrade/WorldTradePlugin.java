package com.example.worldtrade;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class WorldTradePlugin extends JavaPlugin implements CommandExecutor {

    private final Market market = new Market();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        market.load(this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        var cmd = getCommand("worldtrade");
        if (cmd != null) cmd.setExecutor(this);
    }

    public Market market() {
        return market;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("worldtrade.reload")) {
                sender.sendMessage(Component.text("No permission.", NamedTextColor.RED));
                return true;
            }
            reloadConfig();
            market.load(this);
            sender.sendMessage(Component.text("World Trade config reloaded.", NamedTextColor.GREEN));
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can open this menu.", NamedTextColor.RED));
            return true;
        }
        new MainMenu(this).open(player);
        return true;
    }
}
