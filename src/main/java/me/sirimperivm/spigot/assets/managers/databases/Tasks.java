package me.sirimperivm.spigot.assets.managers.databases;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.utils.Colors;

import java.sql.*;

@SuppressWarnings("all")
public class Tasks {

    private static Main plugin = Main.getPlugin();
    private void log(String value) {plugin.log(value);}
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();
    private static Modules mods = data.getMods();

    static Connection conn = data.conn;
    String dbName = data.dbName;
    String tablePrefix = data.tablePrefix;
    String tableName = "tasks";
    public String database = dbName + "." + tablePrefix + tableName;

    boolean tableExists() {
        boolean value = false;
        try {
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet rs = dmd.getTables(null, null, database, new String[]{"TABLE"});
            value = rs.next();
        } catch (SQLException e) {
            log(Main.getFailPrefix() + Colors.text("Impossibile capire se la tabella delle task è presente nel database!"));
            e.printStackTrace();
        }
        return value;
    }

    public void createTable() {
        if (!tableExists()) {
            String query = "CREATE TABLE " + database + "(`taskId` INT AUTO_INCREMENT primary key NOT NULL, `taskType` TEXT NOT NULL, `taskValue` TEXT NOT NULL)";

            try {
                PreparedStatement state = conn.prepareStatement(query);
                state.executeUpdate();
                log(Main.getSuccessPrefix() + Colors.text("Database delle task creato con successo!"));
            } catch (SQLException e) {
                log(Main.getFailPrefix() + Colors.text("Impossibile creare il database delle task!"));
                e.printStackTrace();
                plugin.setCanConnect(false);
                plugin.disablePlugin();
            }
        }
    }

    public void insertTask(String taskType, String taskValue) {
        String query = "INSERT INTO " + database + "(taskType, taskValue) VALUES (?, ?);";
        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.setString(1, taskType);
            state.setString(2, taskValue);
            state.executeUpdate();
        } catch (SQLException e) {
            log(Main.getFailPrefix() + Colors.text("Impossibile inserire un task: " +
                    "\n TaskType: " + taskType +
                    "\n TaskValue: " + taskType +
                    "\n...!"));
            e.printStackTrace();
        }
    }

    public void deleteTask(int taskId) {
        String query = "DELETE FROM " + database + " WHERE taskId=" + taskId + ";";

        try {
            PreparedStatement state = conn.prepareStatement(query);
            state.executeUpdate();
        } catch (SQLException e) {
            log(Main.getFailPrefix() + Colors.text("Impossibile eliminare la task " + taskId + "!"));
            e.printStackTrace();
        }
    }
}
