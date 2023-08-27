package me.sirimperivm.spigot.modules.listeners;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.utils.Colors;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("all")
public class JoinListener implements Listener {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (mods.getModulesInfo().get("races")) {
            String username = p.getName();
            String uuid = p.getUniqueId().toString();
            if (!data.getUsersRaces().existMemberData(username)) {
                String defaultRaceRaw = conf.getSettings().getString("settings.modules.races.defaultRaceRaw");
                data.getUsersRaces().insertMemberData(username, uuid, defaultRaceRaw);
            }
            String query = "SELECT * FROM " + data.getTasks().database;

            try {
                PreparedStatement state = data.conn.prepareStatement(query);
                ResultSet rs = state.executeQuery();
                while (rs.next()) {
                    int taskId = rs.getInt("taskId");
                    String taskType = rs.getString("taskType");
                    String taskValue = rs.getString("taskValue");

                    if (taskType.equalsIgnoreCase("sendUserMessage")) {
                        String user = taskValue.split(";")[0];
                        if (user.equalsIgnoreCase(username)) {
                            p.sendMessage(Colors.text(taskValue.split(";")[1]));
                            mods.deleteTask(taskId);
                        }
                    }

                    if (taskType.equalsIgnoreCase("resetRace")) {
                        String user = taskValue.split(";")[0];
                        String actualUserRace = taskValue.split(";")[1];
                        if (user.equalsIgnoreCase(username)) {
                            mods.resetter(p, actualUserRace);
                            mods.deleteTask(taskId);
                        }
                    }
                }
            } catch (SQLException ex) {
                plugin.log(Main.getFailPrefix() + Colors.text("Impossibile eseguire una task al join."));
                ex.printStackTrace();
            }

            mods.setter(p);
        }
    }
}
