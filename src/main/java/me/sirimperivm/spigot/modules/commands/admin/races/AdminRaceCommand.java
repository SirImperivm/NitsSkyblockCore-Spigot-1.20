package me.sirimperivm.spigot.modules.commands.admin.races;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.utils.Colors;
import me.sirimperivm.spigot.assets.utils.Errors;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

@SuppressWarnings("all")
public class AdminRaceCommand implements CommandExecutor {

    private static Main plugin = Main.getPlugin();
    private void log(String value) {plugin.log(value);}
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();
    private static Modules mods = Main.getMods();

    private void getUsage(CommandSender s) {
        for (String usage : conf.getHelps().getStringList("helps.admin-commands.races")) {
            s.sendMessage(Colors.text(usage));
        }
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {

        if (c.getName().equalsIgnoreCase("racesadmin")) {
            if (!mods.getModulesInfo().get("races")) {
                s.sendMessage(Config.getTransl("settings", "messages.errors.general.module-disabled"));
                return true;
            }

            if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.races.main"))) {
                return true;
            } else {
                if (a.length == 0) {
                    getUsage(s);
                } else if (a.length == 1) {
                    if (a[0].equalsIgnoreCase("list")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.races.list"))) {
                            return true;
                        } else {
                            HashMap<String, String> generatedRacesList = mods.getGeneratedRacesId();
                            s.sendMessage(Config.getTransl("settings", "messages.others.admin.races.list.header"));
                            s.sendMessage(Config.getTransl("settings", "messages.others.admin.races.list.title"));
                            s.sendMessage(Config.getTransl("settings", "messages.others.admin.races.list.spacer"));
                            s.sendMessage(Config.getTransl("settings", "messages.others.admin.races.list.lines.title"));
                            for (String generated : generatedRacesList.keySet()) {
                                s.sendMessage(Config.getTransl("settings", "messages.others.admin.races.list.lines.line")
                                        .replace("${raceName}", generatedRacesList.get(generated))
                                );
                                if (s instanceof Player) {
                                    TextComponent component = new TextComponent(Colors.text("    &5[Copia l'ID]"));
                                    component.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, generated));
                                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(generated).create()));
                                    s.spigot().sendMessage(component);
                                } else {
                                    s.sendMessage(Colors.text("   &5Copia l'ID &f-> &7" + generated));
                                }
                            }
                            s.sendMessage(Config.getTransl("settings", "messages.others.admin.races.list.footer"));
                        }
                    } else {
                        getUsage(s);
                    }
                } else if (a.length == 2) {
                    if (a[0].equalsIgnoreCase("create")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.races.create"))) {
                            return true;
                        } else {
                            String raceName = a[1];
                            mods.createRace(s, raceName);
                        }
                    } else if (a[0].equalsIgnoreCase("delete")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.races.delete"))) {
                            return true;
                        } else {
                            String raceName = a[1];
                            mods.deleteRace(s, raceName);
                        }
                    } else if (a[0].equalsIgnoreCase("reset")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.races.targets.reset"))) {
                            return true;
                        } else {
                            String userName = a[1];
                            mods.resetRace(s, userName, "fromAdmin");
                        }
                    } else if (a[0].equalsIgnoreCase("info")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.races.targets.info"))) {
                            return true;
                        } else {
                            String userName = a[1];
                            String userRace = data.getUsersRaces().getUserRace(userName);
                            if (!userRace.equalsIgnoreCase(Main.getDefaultRaceRaw())) {
                                for (String message : conf.getSettings().getStringList("messages.info.admin.races.info.hasRace")) {
                                    s.sendMessage(Colors.text(message
                                            .replace("${raceName}", userRace)
                                    ));
                                }
                            } else {
                                for (String message : conf.getSettings().getStringList("messages.info.admin.races.info.hasntRace")) {
                                    s.sendMessage(Colors.text(message
                                            .replace("${raceName}", userRace)
                                    ));
                                }
                            }
                        }
                    } else {
                        getUsage(s);
                    }
                } else if (a.length == 3) {
                    if (a[0].equalsIgnoreCase("set")) {
                        if (Errors.noPermCommand(s, conf.getSettings().getString("permissions.admin-commands.races.targets.set"))) {
                            return true;
                        } else {
                            String playerName = a[1];
                            if (data.getUsersRaces().existMemberData(playerName)) {
                                String raceName = a[2];
                                if (mods.existRace(raceName)) {
                                    if (!raceName.equalsIgnoreCase(data.getUsersRaces().getUserRace(playerName))) {
                                        mods.setRace(s, playerName, raceName, "fromAdmin");
                                    } else {
                                        s.sendMessage(Config.getTransl("settings", "messages.errors.admin.races.targets.already-have"));
                                    }
                                } else {
                                    s.sendMessage(Config.getTransl("settings", "messages.errors.admin.races.dont-exists"));
                                }
                            } else {
                                s.sendMessage(Config.getTransl("settings", "messages.errors.admin.races.targets.user-not-found"));
                            }
                        }
                    }
                } else {
                    getUsage(s);
                }
            }
        }
        return false;
    }
}
