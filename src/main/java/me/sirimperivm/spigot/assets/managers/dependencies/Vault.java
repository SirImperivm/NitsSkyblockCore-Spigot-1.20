package me.sirimperivm.spigot.assets.managers.dependencies;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.others.Strings;
import me.sirimperivm.spigot.assets.utils.Colors;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

@SuppressWarnings("all")
public class Vault {

    private static Main plugin = Main.getPlugin();
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;
    private void log(String value) {plugin.log(value);}

    public Vault() {
        if (!setupEconomy()) {
            log(Main.getFailPrefix() + Colors.text("Libreria vault non installata, disattivo il plugin."));
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
        setupPermissions();
        setupChat();
    }

    private boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = plugin.getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static Economy getEcon() {
        return econ;
    }

    public static Permission getPerms() {
        return perms;
    }

    public static Chat getChat() {
        return chat;
    }

    public void takeMoney(OfflinePlayer p, double value) {
        econ.withdrawPlayer(p, value);
        if (p instanceof Player) {
            Player online = (Player) p;
            if (online != null) {
                online.sendMessage(Config.getTransl("settings", "messages.info.money.take")
                        .replace("${value}", String.valueOf(value))
                        .replace("${value-formatted}", Strings.formatNumber(value))
                );
            }
        }
    }

    public void giveMoney(OfflinePlayer p, double value) {
        econ.depositPlayer(p, value);
        if (p instanceof Player) {
            Player online = (Player) p;
            if (online != null) {
                online.sendMessage(Config.getTransl("settings", "messages.info.money.give")
                        .replace("${value}", String.valueOf(value))
                        .replace("${value-formatted}", Strings.formatNumber(value))
                );
            }
        }
    }
}
