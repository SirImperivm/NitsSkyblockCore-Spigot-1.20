package me.sirimperivm.spigot.assets.managers;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.utils.Colors;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.logging.Logger;

@SuppressWarnings("all")
public class Config {

    private static Logger log = Logger.getLogger("NitsSkyblockCore");
    private static Main plugin = Main.getPlugin();
    private File folder = plugin.getDataFolder();
    private File settingsFile, dataFile, helpsFile;
    private FileConfiguration settings, data, helps;

    public Config() {
        settingsFile = new File(folder, "settings.yml");
        settings = new YamlConfiguration();
        dataFile = new File(folder, "data.yml");
        data = new YamlConfiguration();
        helpsFile = new File(folder, "helps.yml");
        helps = new YamlConfiguration();

        if (!folder.exists()) folder.mkdir();

        if (!settingsFile.exists()) create(settings, settingsFile);

        if (!dataFile.exists()) create(data, dataFile);

        if (!helpsFile.exists()) create(helps, helpsFile);
    }

    public void create(FileConfiguration c, File f) {
        String n = f.getName();
        try {
            Files.copy(plugin.getResource(n), f.toPath(), new CopyOption[0]);
            load(c, f);
        } catch (IOException e) {
            log.severe("Impossibile creare il file " + n + "!");
            e.printStackTrace();
        }
    }

    public void save(FileConfiguration c, File f) {
        String n = f.getName();
        try {
            c.save(f);
        } catch (IOException e) {
            log.severe("Impossibile salvare il file " + n + "!");
            e.printStackTrace();
        }
    }

    public void load(FileConfiguration c, File f) {
        String n = f.getName();
        try {
            c.load(f);
        } catch (IOException | InvalidConfigurationException e) {
            log.severe("Impossibile caricare il file " + n + "!");
            e.printStackTrace();
        }
    }

    public void saveAll() {
        save(settings, settingsFile);
        if (!plugin.isMysql()) {
            save(data, dataFile);
        }
        save(helps, helpsFile);
    }

    public void loadAll() {
        load(settings, settingsFile);
        if (!plugin.isMysql()) {
            load(data, dataFile);
        }
        load(helps, helpsFile);
    }

    public File getSettingsFile() {
        return settingsFile;
    }

    public File getDataFile() {
        return dataFile;
    }

    public File getHelpsFile() {
        return helpsFile;
    }

    public FileConfiguration getSettings() {
        return settings;
    }

    public FileConfiguration getData() {
        return data;
    }

    public FileConfiguration getHelps() {
        return helps;
    }

    public static String getTransl(String type, String key) {
        switch (type) {
            case "helps":
                return Colors.text(Main.getConf().getHelps().getString(key)
                        .replace("%sp", Main.getSuccessPrefix())
                        .replace("%ip", Main.getInfoPrefix())
                        .replace("%fp", Main.getFailPrefix())
                );
            case "data":
                if (!plugin.isMysql()) {
                    return Colors.text(Main.getConf().getData().getString(key)
                            .replace("%sp", Main.getSuccessPrefix())
                            .replace("%ip", Main.getInfoPrefix())
                            .replace("%fp", Main.getFailPrefix())
                    );
                } else {
                    return "N/A";
                }
            default:
                return Colors.text(Main.getConf().getSettings().getString(key)
                        .replace("%sp", Main.getSuccessPrefix())
                        .replace("%ip", Main.getInfoPrefix())
                        .replace("%fp", Main.getFailPrefix())
                );
        }
    }
}
