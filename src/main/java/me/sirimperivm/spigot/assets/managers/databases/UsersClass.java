package me.sirimperivm.spigot.assets.managers.databases;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.utils.Colors;

import java.sql.*;

@SuppressWarnings("all")
public class UsersClass {

    private static Main plugin = Main.getPlugin();
    private void log(String value) {
        plugin.getServer().getConsoleSender().sendMessage(value);
    }
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();
    private static Modules mods = data.mods;

    static Connection conn = data.conn;
    String url = conf.getSettings().getString("settings.database.url");
    String dbName = data.dbName;
    String tablePrefix = data.tablePrefix;
    String tableName = "user_class";
    public String database = dbName + "." + tablePrefix + tableName;

    boolean tableExists() {
        boolean value = false;
        try {
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet rs = dmd.getTables(null, null, database, new String[]{"TABLE"});
            value = rs.next();
        } catch (SQLException e) {
            plugin.setCanConnectDatabase(false);
            log(plugin.getFailPrefix() + Colors.text("Impossibile capire se è presente la tabella delle classi utente nel database!"));
            e.printStackTrace();
            plugin.disablePlugin();

        }
        return value;
    }

    public void createTable() {
        if (!tableExists()) {
            String query = "CREATE TABLE " + database + "(`id` INT AUTO_INCREMENT primary key NOT NULL, `username` TEXT NOT NULL, `userUuid` TEXT NOT NULL, `userClass` TEXT NOT NULL);";

            try {
                PreparedStatement state = conn.prepareStatement(query);
                state.executeUpdate();
                log(plugin.getSuccessPrefix() + Colors.text("Creata con successo la tabella delle classi utente!"));
            } catch (SQLException e) {
                plugin.setCanConnectDatabase(false);
                log(plugin.getFailPrefix() + Colors.text("Impossibile creare la tabella delle classi utente, il plugin si disattiverà!"));
            }
        }
    }

    public void insertMemberData(String username, String userUuid, String userClass) {
        String query = "INSERT INTO " + database + "(username, userUuid, userClass) VALUES (?, ?, ?);";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, username);
            state.setString(2, userUuid);
            state.setString(3, userClass);
            state.executeUpdate();
        } catch (SQLException e) {
            log(plugin.getFailPrefix() + Colors.text("Impossibile inserire i dati di un utente: " +
                    "\n Username: " + username +
                    "\n UserUuid: " + userUuid +
                    "\n UserClass: " + userClass +
                    "\n...!"));
            e.printStackTrace();
        }
    }

    public boolean existMemberData(String username) {
        boolean value = false;
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("username").equalsIgnoreCase(username)) {
                    value = true;
                    break;
                }
            }
        } catch (SQLException e) {
            log(plugin.getFailPrefix() + Colors.text("Impossibile capire se sono presenti dati relativi al membro: &c" + username + "&7 nel database delle classi!"));
            e.printStackTrace();
        }
        return value;
    }
}
