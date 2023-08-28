package me.sirimperivm.spigot;

import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.managers.dependencies.PapiExpansions;
import me.sirimperivm.spigot.assets.managers.dependencies.Vault;
import me.sirimperivm.spigot.assets.utils.Colors;
import me.sirimperivm.spigot.modules.commands.admin.core.AdminCoreCommand;
import me.sirimperivm.spigot.modules.commands.admin.races.AdminRaceCommand;
import me.sirimperivm.spigot.modules.commands.user.races.RaceCommand;
import me.sirimperivm.spigot.modules.listeners.ChatListener;
import me.sirimperivm.spigot.modules.listeners.ClickListener;
import me.sirimperivm.spigot.modules.listeners.InteractionListener;
import me.sirimperivm.spigot.modules.listeners.JoinListener;
import me.sirimperivm.spigot.modules.tabCompleters.AdminCoreTabCompleter;
import me.sirimperivm.spigot.modules.tabCompleters.AdminRaceTabCompleter;
import me.sirimperivm.spigot.modules.tabCompleters.RaceTabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getPluginManager;

@SuppressWarnings("all")
public final class Main extends JavaPlugin {

    private static Main plugin;
    private static Config conf;
    private static String successPrefix;
    private static String infoPrefix;
    private static String failPrefix;
    private static Db data;
    private static Modules mods;
    private static String defaultRaceRaw;
    private static String defaultRaceName;
    private static Vault vault;
    private static PapiExpansions papi;

    private boolean canConnect = false;

    public void setCanConnect(boolean canConnect) {
        this.canConnect = canConnect;
    }

    public void log(String value) {
        plugin.getServer().getConsoleSender().sendMessage(value);
    }

    void setup() {
        plugin = this;
        conf = new Config();
        conf.loadAll();
        successPrefix = Colors.text(conf.getSettings().getString("messages.prefixes.success"));
        infoPrefix = Colors.text(conf.getSettings().getString("messages.prefixes.info"));
        failPrefix = Colors.text(conf.getSettings().getString("messages.prefixes.fail"));
        defaultRaceRaw = conf.getSettings().getString("settings.modules.races.defaultRaceRaw");
        defaultRaceName = Colors.text(conf.getSettings().getString("settings.modules.races.defaultRaceName"));
        canConnect = true;
        data = new Db();
        data.setup();
        mods = data.getMods();
        mods.createRace(getServer().getConsoleSender(), defaultRaceRaw, defaultRaceName);
        vault = new Vault();
        papi = new PapiExpansions(plugin);
        if (getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PapiExpansions(plugin).register();
        }
    }

    void close() {
        data.disconnect();
    }

    @Override
    public void onEnable() {
        setup();

        getServer().getPluginCommand("skycore").setExecutor(new AdminCoreCommand());
        getServer().getPluginCommand("racesadmin").setExecutor(new AdminRaceCommand());
        getServer().getPluginCommand("race").setExecutor(new RaceCommand());

        getServer().getPluginCommand("skycore").setTabCompleter(new AdminCoreTabCompleter());
        getServer().getPluginCommand("racesadmin").setTabCompleter(new AdminRaceTabCompleter());
        getServer().getPluginCommand("race").setTabCompleter(new RaceTabCompleter());

        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new ClickListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new InteractionListener(), this);

        log(Colors.text("<RAINBOW1>[NitsSkyblockCore] Plugin attivato correttamente!</RAINBOW>"));
    }

    @Override
    public void onDisable() {
        close();
        log(Colors.text("<RAINBOW1>[NitsSkyblockCore] Plugin disattivato correttamente!</RAINBOW>"));
    }

    public void disablePlugin() {
        getPluginManager().disablePlugin(plugin);
    }
    public void reloadPlugin() {
        conf.loadAll();
        mods.setupModules();
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

    public static String getDefaultRaceRaw() {
        return defaultRaceRaw;
    }

    public static String getDefaultRaceName() {
        return defaultRaceName;
    }

    public boolean getCanConnect() {
        return canConnect;
    }

    public static Db getData() {
        return data;
    }

    public static Modules getMods() {
        return mods;
    }

    public static PapiExpansions getPapi() {
        return papi;
    }

    public static Vault getVault() {
        return vault;
    }
}
