package me.sirimperivm.spigot.assets.managers;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.databases.UsersClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SuppressWarnings("all")
public class Db {

    private static Main plugin = Main.getPlugin();
    private void log(String value) {
        plugin.log(value);
    }
    private static Config conf = Main.getConf();
    public static Modules mods = Main.getMods();

    private static UsersClass usersClass;

    public static Connection conn;
    public static String dbName = conf.getSettings().getString("settings.database.name");
    public static String tablePrefix = conf.getSettings().getString("settings.database.tablePrefix");

    public void connect() {
        String host = conf.getSettings().getString("settings.database.host");
        int port = conf.getSettings().getInt("settings.database.port");
        String user = conf.getSettings().getString("settings.database.user");
        String password = conf.getSettings().getString("settings.database.password");
        String options = conf.getSettings().getString("settings.database.options");

        if (plugin.canConnectDatabase) {
            String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName + options;

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

    public void setup() {
        usersClass = new UsersClass();
        if (mods.getModulesInfo().get("class")) {
            usersClass.createTable();
        }
    }

    public static UsersClass getUsersClass() {
        return usersClass;
    }
}
