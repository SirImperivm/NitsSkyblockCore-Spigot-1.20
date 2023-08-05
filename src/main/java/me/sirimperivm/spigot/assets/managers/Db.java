package me.sirimperivm.spigot.assets.managers;

import me.sirimperivm.spigot.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SuppressWarnings("all")
public class Db {

    private static Main plugin = Main.getPlugin();
    private void log(String value) {
        plugin.getServer().getConsoleSender().sendMessage(value);
    }
    private static Config conf = Main.getConf();
    public static Modules mods = Main.getMods();

    public static Connection conn;
    public static String tablePrefix = conf.getSettings().getString("settings.database.tablePrefix");

    public void connect() {
        String user = conf.getSettings().getString("settings.database.user");
        String password = conf.getSettings().getString("settings.database.password");

        if (plugin.canConnectDatabase) {
            String url = conf.getSettings().getString("settings.database.url");

            try {
                conn = DriverManager.getConnection(url, user, password);
                log(Config.getTransl("settings", "messages.success.database.connected"));
            } catch (SQLException e) {
                log(Config.getTransl("settings", "messages.errors.database.cant-connect"));
                plugin.setCanConnectDatabase(false);
                e.printStackTrace();
                plugin.disablePlugin();
            }
        } else {
            log(Config.getTransl("settings", "messages.errors.database.cant-connect"));
            plugin.disablePlugin();
        }
    }

    public void disconnect() {
        if (plugin.canConnectDatabase) {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    log(Config.getTransl("settings", "messages.info.database.disconnected"));
                }
            } catch (SQLException e) {
                log(Config.getTransl("settings", "messages.errors.database.cant-disconnect"));
                e.printStackTrace();
            }
        }
    }
}
