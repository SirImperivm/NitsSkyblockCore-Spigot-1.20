package me.sirimperivm.spigot.modules.commands.admin.extraCave;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.utils.Colors;
import me.sirimperivm.spigot.assets.utils.Errors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("all")
public class AdminExtracaveCommand implements CommandExecutor {

    private static Main plugin = Main.getPlugin();
    private void log(String message) {plugin.log(message);}
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    private void getUsage(CommandSender s) {
        for (String usage : conf.getHelps().getStringList("helps.admin-commands.extraCave")) {
            s.sendMessage(Colors.text(usage));
        }
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {

        if (c.getName().equalsIgnoreCase("extracave")) {
            if (mods.getModulesInfo().get("extraCave")) {
                if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.extraCave.main"))) {
                    return true;
                } else {
                    if (a.length == 0) {
                        getUsage(s);
                    } else if (a.length == 1) {
                        if (a[0].equalsIgnoreCase("bypass")) {
                            if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.extraCave.bypass"))) {
                                return true;
                            } else {
                                if (Errors.noConsole(s)) {
                                    return true;
                                } else {
                                    Player p = (Player) s;
                                    String playerName = p.getName();
                                    boolean isBypassing = mods.isBypassing(playerName);
                                    String status = "";

                                    if (isBypassing) {
                                        mods.getExtraCaveBypasses().put(playerName, false);
                                        status = Config.getTransl("settings", "messages.info.admin.extraCave.bypass.formats.disabled");
                                    } else {
                                        mods.getExtraCaveBypasses().put(playerName, true);
                                        status = Config.getTransl("settings", "messages.info.admin.extraCave.bypass.formats.enabled");
                                    }
                                    p.sendMessage(Config.getTransl("settings", "messages.info.admin.extraCave.bypass.string")
                                            .replace("${status}", status));
                                }
                            }
                        } else {
                            getUsage(s);
                        }
                    } else {
                        getUsage(s);
                    }
                }
            } else {
                s.sendMessage(Config.getTransl("settings", "messages.errors.general.module-disabled"));
            }
        }
        return false;
    }
}
