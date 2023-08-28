package me.sirimperivm.spigot.modules.commands.admin.core;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.utils.Colors;
import me.sirimperivm.spigot.assets.utils.Errors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@SuppressWarnings("all")
public class AdminCoreCommand implements CommandExecutor {

    private static Main plugin = Main.getPlugin();
    private void log(String value) {
        plugin.log(value);
    }
    private static Config conf = Main.getConf();

    private void getUsage(CommandSender s) {
        for (String usage : conf.getHelps().getStringList("helps.admin-commands.core")) {
            s.sendMessage(Colors.text(usage));
        }
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {

        if (c.getName().equalsIgnoreCase("skycore")) {
            if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.core.main"))) {
                return true;
            } else {
                if (a.length == 0) {
                    getUsage(s);
                } else if (a.length == 1) {
                    if (a[0].equalsIgnoreCase("reload")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.core.reload"))) {
                            return true;
                        } else {
                            plugin.reloadPlugin();
                            s.sendMessage(Config.getTransl("settings", "messages.success.plugin.reloaded"));
                        }
                    } else {
                        getUsage(s);
                    }
                } else {
                    getUsage(s);
                }
            }
        }
        return false;
    }
}