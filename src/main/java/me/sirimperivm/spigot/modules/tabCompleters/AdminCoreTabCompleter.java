package me.sirimperivm.spigot.modules.tabCompleters;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Modules;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class AdminCoreTabCompleter implements TabCompleter {

    private static Main plugin = Main.getPlugin();
    private void log(String value) {
        plugin.log(value);
    }
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {

        if (c.getName().equalsIgnoreCase("skycore")) {
            if (s.hasPermission(Config.getTransl("settings", "permissions.admin-commands.core.main"))) {
                if (a.length == 1) {
                    List<String> toReturn = new ArrayList<>();
                    toReturn.add("reload");
                    toReturn.add("sendmessage");
                    return toReturn;
                }
                else if (a.length == 2) {
                    List<String> toReturn = new ArrayList<>();
                    if (a[0].equalsIgnoreCase("sendmessage") && mods.getModulesInfo().get("message-sender")) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            toReturn.add(p.getName());
                        }
                    }
                    return toReturn;
                }
                else if (a.length >= 3) {
                    List<String> toReturn = new ArrayList<>();
                    if (a[0].equalsIgnoreCase("sendmessage") && mods.getModulesInfo().get("message-sender")) {
                        toReturn.add("<tuo_messaggio>");
                    }
                    return toReturn;
                }
            }
        }
        return null;
    }
}
