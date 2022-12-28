package dev.badbird.base64utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public final class Base64Utils extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        getCommand("base64toitem").setExecutor(this);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /base64toitem <file>");
            return true;
        }
        Player player = (Player) sender;
        String fileName = args[0];
        File file = new File(getDataFolder(), fileName);
        if (!file.exists()) {
            sender.sendMessage(ChatColor.RED + "File does not exist!");
            return true;
        }
        String contents;
        try {
            contents = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "An error occurred while reading the file: " + e.getMessage());
            return true;
        }
        List<String> lines = Arrays.asList(contents.split("\n"));
        int i = 0, x = 0;
        for (String line : lines) {
            i++;
            player.sendMessage(ChatColor.GREEN + "Decoding line " + i + "...");
            try {
                byte[] decoded = Base64.getDecoder().decode(line);
                ItemStack item = ItemStack.deserializeBytes(decoded);
                player.getInventory().addItem(item);
                player.sendMessage(ChatColor.GREEN + "Successfully decoded line " + i + "! Item: " + item.getType().name());
                x++;
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(ChatColor.RED + "An error occurred while decoding line " + i + ": " + e.getMessage());
            }
        }
        player.sendMessage(ChatColor.GREEN + "Done! Successfully decoded " + x + " lines.");
        return true;
    }
}
