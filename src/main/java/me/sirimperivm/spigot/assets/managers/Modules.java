package me.sirimperivm.spigot.assets.managers;

import me.sirimperivm.spigot.Main;

import java.util.HashMap;

@SuppressWarnings("all")
public class Modules {

    private static Main plugin = Main.getPlugin();
    private void log(String value) {
        plugin.getServer().getConsoleSender().sendMessage(value);
    }
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();

    private static String defaultClassRaw;

    private static HashMap<String, Boolean> modulesInfo;

    public Modules() {
        modulesInfo = new HashMap<String, Boolean>();
        for (String key : conf.getSettings().getConfigurationSection("settings.modules").getKeys(false)) {
            String path = "settings.modules." + key;
            modulesInfo.put(key, conf.getSettings().getBoolean(path + ".enable"));
        }
        if (modulesInfo.get("class")) {
            defaultClassRaw = conf.getSettings().getString("settings.modules.class.defaultClassRaw");
        }
    }

    public static String getDefaultClassRaw() {
        return defaultClassRaw;
    }

    public static HashMap<String, Boolean> getModulesInfo() {
        return modulesInfo;
    }
}
