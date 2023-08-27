package me.sirimperivm.spigot.modules.commands.user.races;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Gui;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.utils.Colors;
import me.sirimperivm.spigot.assets.utils.Errors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("all")
public class RaceCommand implements CommandExecutor {

    private static Main plugin = Main.getPlugin();
    private void log(String value) {plugin.log(value);}
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    private void getUsage(CommandSender s) {
        for (String usage : conf.getHelps().getStringList("helps.user-commands.races")) {
            s.sendMessage(Colors.text(usage));
        }
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {

        if (c.getName().equalsIgnoreCase("race")) {
            if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.races.main"))) {
                return true;
            } else {
                if (a.length == 0) {
                    getUsage(s);
                } else if (a.length == 1) {
                    if (a[0].equalsIgnoreCase("help")) {
                        getUsage(s);
                    } else if (a[0].equalsIgnoreCase("gui")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.races.gui"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                Gui g = new Gui();
                                p.openInventory(g.mainRacesGui(p));
                            }
                        }
                    } else if (a[0].equalsIgnoreCase("reset")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.races.reset"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                mods.resetUserRace(p);
                            }
                        }
                    } else {
                        getUsage(s);
                    }
                } else if (a.length == 2) {
                    if (a[0].equalsIgnoreCase("choose")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.races.choose"))) {
                            return true;
                        } else {
                            if (Errors.noConsole(s)) {
                                return true;
                            } else {
                                Player p = (Player) s;
                                String raceSelected = a[1];
                                boolean raceExists = false;
                                for (String racesId : mods.getGeneratedRacesId().keySet()) {
                                    String raceName = mods.getGeneratedRacesId().get(racesId);
                                    if (raceName.equalsIgnoreCase(raceSelected)) {
                                        raceExists = true;
                                        break;
                                    }
                                }

                                if (raceExists) {
                                    if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.user-commands.races.getRace")
                                            .replace("<raceName>", raceSelected))) {
                                        return true;
                                    } else {
                                        mods.setUserRace(p, raceSelected);
                                    }
                                } else {
                                    p.sendMessage(Config.getTransl("settings", "messages.errors.users.races.dont-exists"));
                                }
                            }
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
