package me.sirimperivm.spigot.modules.tabCompleters;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

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
    private static Db data = Main.getData();

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {

        if (c.getName().equalsIgnoreCase("skycore")) {
            if (s.hasPermission(Config.getTransl("settings", "permissions.admin-commands.core.main"))) {
                if (a.length == 1) {
                    List<String> toReturn = new ArrayList<>();
                    toReturn.add("reload");
                    return toReturn;
                }
            }
        }
        return null;
    }
}
