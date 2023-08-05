package me.sirimperivm.spigot.modules.listeners;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@SuppressWarnings("all")
public class JoinListener implements Listener {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (mods.getModulesInfo().get("class")) {
            String username = p.getName();
            String uuid = p.getUniqueId().toString().replace("-", "");
            if (!p.hasPlayedBefore() || !data.getUsersClass().existMemberData(username)) {
                data.getUsersClass().insertMemberData(username, uuid, mods.getDefaultClassRaw());
            }
        }
    }
}
