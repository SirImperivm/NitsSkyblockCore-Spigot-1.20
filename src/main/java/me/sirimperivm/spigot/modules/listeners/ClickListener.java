package me.sirimperivm.spigot.modules.listeners;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import me.sirimperivm.spigot.assets.managers.dependencies.Vault;
import me.sirimperivm.spigot.assets.others.Strings;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

@SuppressWarnings("all")
public class ClickListener implements Listener {

    private static Main plugin = Main.getPlugin();
    private void log(String message) {plugin.log(message);}
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();
        int slot = e.getSlot();

        if (title.equalsIgnoreCase(Config.getTransl("guis", "guis.mainRacesGui"))) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);

            for (String item : conf.getGuis().getConfigurationSection("guis.mainRacesGui.items").getKeys(false)) {
                String itemsPath = "guis.mainRacesGui.items" + item;
                List<Integer> slots = conf.getGuis().getIntegerList(itemsPath + ".slots");
                if (slots.contains(slot)) {
                    String actionType = conf.getGuis().getString(itemsPath + ".action");
                    if (actionType.equalsIgnoreCase("CLOSE_GUI")) {
                        p.getOpenInventory().close();
                    } else if (actionType.equalsIgnoreCase("RESET_RACE")) {
                        String username = p.getName();
                        String userRace = data.getUsersRaces().getUserRace(username);
                        String defaultRaceRaw = Main.getDefaultRaceRaw();
                        if (!userRace.equalsIgnoreCase(defaultRaceRaw)) {
                            data.getUsersRaces().updateMemberData(username, "raceName", defaultRaceRaw);
                            mods.resetter(p, userRace);
                            p.sendMessage(Config.getTransl("settings", "messages.success.users.races.reset"));
                        } else {
                            p.sendMessage(Config.getTransl("settings", "messages.errors.users.races.dont-have"));
                        }
                    } else if (actionType.equalsIgnoreCase("CHOOSE_RACE")) {
                        boolean haveRequirements = true;
                        for (String requirement : conf.getGuis().getConfigurationSection("guis.mainRacesGui.items." + item + ".requirements").getKeys(false)) {
                            String requirementsPath = "guis.mainRacesGui.items." + item + ".requirements." + requirement;
                            String requirementType = conf.getGuis().getString(requirementsPath + ".type");
                            if (requirementType.equalsIgnoreCase("permission")) {
                                String node = conf.getGuis().getString(requirementsPath + ".node");

                                if (p.hasPermission(node)) {
                                    haveRequirements = true;
                                } else {
                                    haveRequirements = false;
                                }
                            }

                            if (requirementType.equalsIgnoreCase("money")) {
                                double value = conf.getGuis().getDouble(requirementsPath + ".value");
                                double userBalance = Vault.getEcon().getBalance(p);

                                if (userBalance >= value) {
                                    haveRequirements = true;
                                } else {
                                    haveRequirements = false;
                                }
                            }
                        }

                        if (haveRequirements) {
                            String raceSelected = conf.getGuis().getString(itemsPath + ".settings.raceName");
                            String username = p.getName();
                            String actualUserRace = data.getUsersRaces().getUserRace(username);

                            if (actualUserRace.equalsIgnoreCase(Main.getDefaultRaceRaw())) {
                                for (String requirement : conf.getGuis().getConfigurationSection("guis.mainRacesGui.items." + item + ".requirements").getKeys(false)) {
                                    String requirementsPath = "guis.mainRacesGui.items." + item + ".requirements." + requirement;
                                    String requirementType = conf.getGuis().getString(requirementsPath + ".type");

                                    if (requirementType.equalsIgnoreCase("money")) {
                                        double value = conf.getGuis().getDouble(requirementsPath + ".value");
                                        double userBalance = Vault.getEcon().getBalance(p);

                                        Vault.getEcon().withdrawPlayer(p, value);
                                        p.sendMessage(Config.getTransl("settings", "messages.info.money.take")
                                                .replace("${value}", String.valueOf(value))
                                                .replace("${value-formatted}", Strings.formatNumber(value))
                                        );
                                    }
                                }

                                data.getUsersRaces().updateMemberData(username, "raceName", raceSelected);
                                mods.setter(p);
                                p.sendMessage(Config.getTransl("settings", "messages.success.users.races.choose")
                                        .replace("${raceName}", raceSelected));
                            } else {
                                p.sendMessage(Config.getTransl("settings", "messages.errors.users.races.already-have"));
                            }
                        } else {
                            p.sendMessage(Config.getTransl("guis", itemsPath + ".settings.requirements.not-have"));
                        }
                    }
                }
            }
        }
    }
}
