package me.sirimperivm.spigot.assets.managers;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.databases.Races;
import me.sirimperivm.spigot.assets.managers.databases.Tasks;
import me.sirimperivm.spigot.assets.managers.databases.UsersRaces;
import me.sirimperivm.spigot.assets.utils.Colors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SuppressWarnings("all")
public class Db {

    private static Main plugin = Main.getPlugin();
    private void log(String value) {plugin.log(value);}
    private static Config conf = Main.getConf();
    private static Modules mods;

    private static UsersRaces usersRaces;
    private static Races races;
    private static Tasks tasks;
    public static Connection conn;

    public static String dbName = conf.getSettings().getString("settings.database.name");
    public static String tablePrefix = conf.getSettings().getString("settings.database.tablePrefix");

    private void connect() {
        String host = conf.getSettings().getString("settings.database.host");
        int port = conf.getSettings().getInt("settings.database.port");
        String user = conf.getSettings().getString("settings.database.user");
        String password = conf.getSettings().getString("settings.database.password");
        String options = conf.getSettings().getString("settings.database.options");

        if (plugin.getCanConnect()) {
            String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName + options;

            try {
                conn = DriverManager.getConnection(url, user, password);
                log(Main.getSuccessPrefix() + Colors.text("Plugin connesso al database con successo!"));
            } catch (SQLException e) {
                log(Main.getFailPrefix() + Colors.text("Impossibile connettersi al database, il plugin si disattiverà!"));
                e.printStackTrace();
                plugin.setCanConnect(false);
                plugin.disablePlugin();
            }
        } else {
            log(Main.getFailPrefix() + Colors.text("Impossibile connettersi al database, il plugin si disattiverà!"));
            plugin.disablePlugin();
        }
    }

    public void disconnect() {
        if (plugin.getCanConnect()) {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    log(Main.getSuccessPrefix() + Colors.text("Plugin disconnesso dal database con successo!"));
                }
            } catch (SQLException e) {
                log(Main.getFailPrefix() + Colors.text("Impossibile disconnettersi dal database!"));
                e.printStackTrace();
            }
        }
    }

    public void setup() {
        connect();
        mods = new Modules();
        usersRaces = new UsersRaces();
        races = new Races();
        tasks = new Tasks();
        tasks.createTable();
        if (mods.getModulesInfo().get("races")) {
            usersRaces.createTable();
            races.createTable();
        }
        mods.setupModules();
    }

    public static Modules getMods() {
        return mods;
    }

    public static Tasks getTasks() {
        return tasks;
    }

    public static UsersRaces getUsersRaces() {
        return usersRaces;
    }

    public static Races getRaces() {
        return races;
    }
}
