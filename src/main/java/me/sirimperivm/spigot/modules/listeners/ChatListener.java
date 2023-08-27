package me.sirimperivm.spigot.modules.listeners;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@SuppressWarnings("all")
public class ChatListener implements Listener {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage();

        if (mods.getModulesInfo().get("races")) {
            if (conf.getSettings().getBoolean("settings.modules.races.blockChat")) {
                String userName = p.getName();
                String actualUserRace = data.getUsersRaces().getUserRace(userName);
                if (actualUserRace.equalsIgnoreCase(Main.getDefaultRaceRaw())) {
                    e.setCancelled(true);
                    p.sendMessage(Config.getTransl("settings", "messages.errors.general.races.chat.not-allowed"));
                }
            }
        }
    }
}
