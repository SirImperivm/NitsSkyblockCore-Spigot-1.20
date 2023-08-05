package me.sirimperivm.spigot;

import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.utils.Colors;
import me.sirimperivm.spigot.modules.listeners.JoinListener;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("all")
public final class Main extends JavaPlugin {

    private static Main plugin;
    private static Config conf;
    private static String successPrefix;
    private static String infoPrefix;
    private static String failPrefix;
    private static String dbType;
    private static Modules mods;
    private static Db data;
    public boolean canConnectDatabase;
    public void setCanConnectDatabase(boolean canConnectDatabase) {
        this.canConnectDatabase = canConnectDatabase;
    }

    private boolean isMysql;
    private void log(String value) {
        plugin.getServer().getConsoleSender().sendMessage(value);
    }

    public void setMysql(boolean mysql) {
        isMysql = mysql;
    }

    void setup() {
        plugin = this;
        conf = new Config();
        conf.loadAll();
        dbType = conf.getSettings().getString("settings.database.type");
        successPrefix = Colors.text(conf.getSettings().getString("messages.prefixes.success"));
        infoPrefix = Colors.text(conf.getSettings().getString("messages.prefixes.info"));
        failPrefix = Colors.text(conf.getSettings().getString("messages.prefixes.fail"));
        mods = new Modules();
        data = new Db();
        if (dbType.equalsIgnoreCase("MySQL")) {
            setCanConnectDatabase(true);
            setMysql(true);
            data.connect();
            data.setup();
        } else {
            setMysql(false);
        }
    }

    void close() {
        data.disconnect();
    }

    @Override
    public void onEnable() {
        setup();

        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        log(Colors.text("<RAINBOW1>[NitsSkyblockCore] Plugin attivato correttamente!</RAINBOW>"));
    }

    @Override
    public void onDisable() {
        close();
        log(Colors.text("<RAINBOW1>[NitsSkyblockCore] Plugin disattivato correttamente!</RAINBOW>"));
    }

    public void disablePlugin() {
        getServer().getPluginManager().disablePlugin(plugin);
    }
    public void reloadPlugin() {
        conf.loadAll();
        data.disconnect();
        data.connect();
    }

    public static Main getPlugin() {
        return plugin;
    }

    public static Config getConf() {
        return conf;
    }

    public static String getSuccessPrefix() {
        return successPrefix;
    }

    public static String getInfoPrefix() {
        return infoPrefix;
    }

    public static String getFailPrefix() {
        return failPrefix;
    }

    public boolean isMysql() {
        return isMysql;
    }

    public static Modules getMods() {
        return mods;
    }

    public static Db getData() {
        return data;
    }
}
