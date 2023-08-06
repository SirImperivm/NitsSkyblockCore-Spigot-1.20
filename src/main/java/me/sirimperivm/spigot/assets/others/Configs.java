package me.sirimperivm.spigot.assets.others;

import me.sirimperivm.spigot.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

@SuppressWarnings("all")
public enum Configs {
    settings(Main.getConf().getSettings(), Main.getConf().getSettingsFile()),
    helps(Main.getConf().getHelps(), Main.getConf().getHelpsFile()),
    races(Main.getConf().getRaces(), Main.getConf().getRacesFile()),
    guis(Main.getConf().getGuis(), Main.getConf().getGuisFile());

    private FileConfiguration c;
    private File f;

    Configs(FileConfiguration c, File f) {
        this.c = c;
        this.f = f;
    }

    public FileConfiguration getC() {
        return c;
    }

    public File getF() {
        return f;
    }
}
