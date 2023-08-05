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
    private File settingsFile;
    private FileConfiguration settings;

    public Config() {
        settingsFile = new File(folder, "settings.yml");
        settings = new YamlConfiguration();

        if (!folder.exists()) folder.mkdir();

        if (!settingsFile.exists()) create(settings, settingsFile);
    }

    private void create(FileConfiguration c, File f) {
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
    }

    public void loadAll() {
        load(settings, settingsFile);
    }

    public File getSettingsFile() {
        return settingsFile;
    }

    public FileConfiguration getSettings() {
        return settings;
    }

    public static String getTransl(String type, String key) {
        switch (type) {
            default:
                return Colors.text(Main.getConf().getSettings().getString(key)
                        .replace("%sp", Main.getSuccessPrefix())
                        .replace("%ip", Main.getInfoPrefix())
                        .replace("%fp", Main.getFailPrefix())
                );
        }
    }
}
