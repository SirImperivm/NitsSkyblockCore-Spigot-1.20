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
public class RaceTabCompleter implements TabCompleter {

    private static Main plugin = Main.getPlugin();
    private void log(String value) {
        plugin.log(value);
    }
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {

        if (c.getName().equalsIgnoreCase("race")) {
            if (s.hasPermission(Config.getTransl("settings", "permissions.user-commands.races.main"))) {
                if (a.length == 1) {
                    List<String> toReturn = new ArrayList<>();
                    toReturn.add("help");
                    toReturn.add("gui");
                    toReturn.add("choose");
                    toReturn.add("reset");
                    return toReturn;
                } else if (a.length == 2){
                    List<String> toReturn = new ArrayList<>();
                    if (a[0].equalsIgnoreCase("choose")) {
                        for (String raceId : mods.getGeneratedRacesId().keySet()) {
                            toReturn.add(mods.getGeneratedRacesId().get(raceId));
                        }
                        toReturn.remove("Nessuna");
                    }
                    return toReturn;
                }
            }
        }
        return null;
    }
}
