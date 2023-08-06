package me.sirimperivm.spigot.assets.managers.databases;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.utils.Colors;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class UsersRaces {

    private static Main plugin = Main.getPlugin();
    private void log(String value) {plugin.log(value);}
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();
    private static Modules mods = data.getMods();

    static Connection conn = data.conn;
    String dbName = data.dbName;
    String tablePrefix = data.tablePrefix;
    String tableName = "users_races";
    String database = dbName + "." + tablePrefix + tableName;

    boolean tableExists() {
        boolean value = false;
        try {
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet rs = dmd.getTables(null, null, database, new String[]{"TABLE"});
            value = rs.next();
        } catch (SQLException e) {
            log(Main.getFailPrefix() + Colors.text("Impossibile capire se la tabella delle razze utente è presente nel database!"));
            e.printStackTrace();
        }
        return value;
    }

    public void createTable() {
        if (!tableExists()) {
            String query = "CREATE TABLE " + database + "(`id` INT AUTO_INCREMENT primary key NOT NULL, `username` TEXT NOT NULL, `uuid` TEXT NOT NULL, `raceName` TEXT NOT NULL)";

            try {
                PreparedStatement state = conn.prepareStatement(query);
                state.executeUpdate();
                log(Main.getSuccessPrefix() + Colors.text("Database delle razze utente creato con successo!"));
            } catch (SQLException e) {
                log(Main.getFailPrefix() + Colors.text("Impossibile creare il database delle razze utente!"));
                e.printStackTrace();
                plugin.setCanConnect(false);
                plugin.disablePlugin();
            }
        }
    }

    public void insertMemberData(String username, String uuid, String raceName) {
        String query = "INSERT INTO " + database + "(username, uuid, raceName) VALUES (?, ?, ?);";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, username);
            state.setString(2, uuid);
            state.setString(3, raceName);
            state.executeUpdate();
        } catch (SQLException e) {
            log(Main.getFailPrefix() + Colors.text("Impossibile inserire i dati di un membro nel database delle razze: " +
                    "\n Username: " + username +
                    "\n Uuid: " + uuid +
                    "\n RaceName: " + raceName +
                    "\n...!"));
            e.printStackTrace();
        }
    }

    public void updateMemberData(String username, String key, String value) {
        String query = "UPDATE " + database + " SET " + key + "='" + value + "' WHERE username='" + username + "'";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.executeUpdate();
        } catch (SQLException e) {
            log(Main.getFailPrefix() + Colors.text("E' stato impossibile modificare un dato relativo all'utente " + username + "!"));
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
            log(Main.getFailPrefix() + Colors.text("Impossibile capire se l'utente " + username + " è già presente nel database delle razze."));
            e.printStackTrace();
        }
        return value;
    }

    public String getUserRace(String username) {
        String userRace = "null";
        String query = "SELECT * FROM " + database;
        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("username").equalsIgnoreCase(username)) {
                    userRace = rs.getString("raceName");
                    break;
                }
            }
        } catch (SQLException e) {
            log(Main.getFailPrefix() + Colors.text("Impossibile ottenere la razza dell'utente " + username + "!"));
            e.printStackTrace();
        }
        return userRace;
    }

    public List<String> playersInARace(String raceName) {
        List<String> players = new ArrayList<String>();
        String query = "SELECT * FROM " + database;

        try {
            PreparedStatement state = conn.prepareStatement(query);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("raceName").equalsIgnoreCase(raceName)) {
                    players.add(rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            log(Main.getFailPrefix() + Colors.text("Impossibile creare una lista di membri che abbiano la razza " + raceName + "!"));
            e.printStackTrace();
        }
        return players;
    }
}
