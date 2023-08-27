package me.sirimperivm.spigot.modules.tabCompleters;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class AdminRaceTabCompleter implements TabCompleter {

    private static Main plugin = Main.getPlugin();
    private void log(String value) {
        plugin.log(value);
    }
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {

        if (c.getName().equalsIgnoreCase("racesadmin")) {
            if (s.hasPermission(Config.getTransl("settings", "permissions.admin-commands.races.main"))) {
                if (a.length == 1) {
                    List<String> toReturn = new ArrayList<>();
                    toReturn.add("create");
                    toReturn.add("delete");
                    toReturn.add("info");
                    toReturn.add("list");
                    toReturn.add("reset");
                    toReturn.add("set");
                    return toReturn;
                } else if (a.length == 2) {
                    List<String> toReturn = new ArrayList<>();
                    if (a[0].equalsIgnoreCase("create")) {
                        toReturn.add("<nome razza>");
                    }
                    if (a[0].equalsIgnoreCase("delete")) {
                        for (String raceId : mods.getGeneratedRacesId().keySet()) {
                            String raceName = mods.getGeneratedRacesId().get(raceId);
                            toReturn.add(raceName);
                        }
                        toReturn.remove(Main.getDefaultRaceRaw());
                    }
                    if (a[0].equalsIgnoreCase("info") || a[0].equalsIgnoreCase("reset") || a[0].equalsIgnoreCase("set")) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            toReturn.add(p.getName());
                        }
                    }
                    return toReturn;
                } else if (a.length == 3) {
                    List<String> toReturn = new ArrayList<>();
                    if (a[0].equalsIgnoreCase("set")) {
                        if (!a[1].equals(null)) {
                            for (String raceId : mods.getGeneratedRacesId().keySet()) {
                                String raceName = mods.getGeneratedRacesId().get(raceId);
                                toReturn.add(raceName);
                            }
                            toReturn.remove(Main.getDefaultRaceRaw());
                        }
                    }
                    if (a[0].equalsIgnoreCase("create")) {
                        if (!a[1].equals(null)) {
                            toReturn.add("<titolo visualizzato>");
                        }
                    }
                    return toReturn;
                }
            }
        }
        return null;
    }
}
