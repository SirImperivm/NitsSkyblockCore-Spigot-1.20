package me.sirimperivm.spigot.assets.managers.dependencies;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import org.bukkit.OfflinePlayer;

@SuppressWarnings("all")
public class PapiExpansions extends PlaceholderExpansion {

    private final Main plugin;
    public PapiExpansions(Main plugin) {
        this.plugin = plugin;
    }

    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    @Override
    public String getIdentifier() {
        return "skycore";
    }
    @Override
    public String getAuthor() {
        return "SirImperivm_";
    }
    @Override
    public String getVersion() {
        return "2.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String param) {
        String toReturn = "ERROR!";
        String path = "placeholders.";

        if (param.equalsIgnoreCase(conf.getSettings().getString(path + "user.race.name"))) {
            toReturn = data.getUsersRaces().getUserRace(player.getName());
        }

        if (param.equalsIgnoreCase(conf.getSettings().getString(path + "user.race.title"))) {
            toReturn = mods.getRaceTitle(data.getUsersRaces().getUserRace(player.getName()));
        }

        return toReturn;
    }
}