package me.sirimperivm.spigot.assets.managers;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.others.Strings;
import me.sirimperivm.spigot.assets.utils.Colors;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("all")
public class Modules {

    private static Main plugin = Main.getPlugin();
    private void log(String value) {plugin.log(value);}
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();

    private static HashMap<String, Boolean> modulesInfo;
    private static HashMap<String, String> generatedRacesId;

    public Modules() {
        modulesInfo = new HashMap<String, Boolean>();
        for (String module : conf.getSettings().getConfigurationSection("settings.modules").getKeys(false)) {
            modulesInfo.put(module, conf.getSettings().getBoolean("settings.modules." + module + ".enable"));
        }
    }

    public void setupModules() {
        modulesInfo = new HashMap<String, Boolean>();
        for (String module : conf.getSettings().getConfigurationSection("settings.modules").getKeys(false)) {
            modulesInfo.put(module, conf.getSettings().getBoolean("settings.modules." + module + ".enable"));
        }
        if (modulesInfo.get("races")) {
            generatedRacesId = data.getRaces().getGeneratedId();
        }
        executeTasks();
    }

    private void executeTasks() {
        BukkitScheduler schedule = Bukkit.getScheduler();
        schedule.runTaskTimer(plugin, () -> {
            String query = "SELECT * FROM " + data.getTasks().database;

            try {
                PreparedStatement state = data.conn.prepareStatement(query);
                ResultSet rs = state.executeQuery();
                while (rs.next()) {
                    int taskId = rs.getInt("taskId");
                    String taskType = rs.getString("taskType");
                    String taskValue = rs.getString("taskValue");

                    if (taskType.equalsIgnoreCase("sendUserMessage")) {
                        String username = taskValue.split(";")[0];
                        Player target = Bukkit.getPlayerExact(username);
                        if (target != null) {
                            target.sendMessage(Colors.text(taskValue.split(";")[1]));
                            deleteTask(taskId);
                        }
                    }
                }
            } catch (SQLException e) {
                log(Main.getFailPrefix() + Colors.text("Impossibile eseguire una task."));
                e.printStackTrace();
            }
        }, 20, 20*10);
    }

    private void deleteTask(int taskId) {
        data.getTasks().deleteTask(taskId);
    }

    public void createRace(CommandSender s, String raceName) {
        if (!existRace(raceName)) {
            String generateID = Strings.generateUUID();
            for (String generated : generatedRacesId.keySet()) {
                if (generateID.equalsIgnoreCase(generated)) {
                    generateID = Strings.generateUUID();
                }
            }
            generatedRacesId.put(generateID, raceName);

            conf.getRaces().set("races." + raceName + ".settings.raceId", generateID);
            conf.getRaces().set("races." + raceName + ".dayEffects", new ArrayList<String>());
            conf.getRaces().set("races." + raceName + ".nightEffects", new ArrayList<String>());
            conf.getRaces().set("races." + raceName + ".dayAttributes", new ArrayList<String>());
            conf.getRaces().set("races." + raceName + ".nightAttributes", new ArrayList<String>());
            conf.save(conf.getRaces(), conf.getRacesFile());
            data.getRaces().insertRaceData(generateID, raceName);
            for (String message : conf.getSettings().getStringList("messages.success.admin.races.created")) {
                if (message.equalsIgnoreCase("${copyRaceId}")) {
                    if (s instanceof Player) {
                        TextComponent component = new TextComponent();
                        component.setText(Colors.text("&5[Copia l'ID]"));
                        component.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, generateID));
                        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(generateID).create()));
                        s.spigot().sendMessage(component);
                    } else {
                        s.sendMessage(Colors.text("&5Copia l'ID &f-> &7" + generateID));
                    }
                } else {
                    s.sendMessage(Colors.text(message
                            .replace("${raceName}", raceName)
                            .replace("${raceId}", generateID)
                    ));
                }
            }
        } else {
            s.sendMessage(Config.getTransl("settings", "messages.errors.admin.races.already-exists"));
        }
    }

    public void deleteRace(CommandSender s, String raceName) {
        if (existRace(raceName)) {
            List<String> playersInRace = data.getUsersRaces().playersInARace(raceName);
            for (String player : playersInRace) {
                resetRace(player, Config.getTransl("settings", "messages.info.users.races.reset.from-death")
                        .replace("${raceName}", raceName));
            }
            data.getRaces().deleteRace(raceName);
            conf.getRaces().set("races." + raceName, null);
            conf.save(conf.getRaces(), conf.getRacesFile());
            generatedRacesId = data.getRaces().getGeneratedId();
            s.sendMessage(Config.getTransl("settings", "messages.success.admin.races.deleted")
                    .replace("${raceName}", raceName));
        } else {
            s.sendMessage(Config.getTransl("settings", "messages.errors.admin.races.dont-exists"));
        }
    }

    public void setRace(CommandSender s, String targetName, String raceName, String typeFrom) {
        switch (typeFrom) {
            case "fromAdmin":
                data.getUsersRaces().updateMemberData(targetName, "raceName", raceName);

                Player target = Bukkit.getPlayerExact(targetName);
                if (target != null) {
                    target.sendMessage(Config.getTransl("settings", "messages.info.users.races.received.from-admin")
                            .replace("${raceName}", raceName)
                    );
                    setter(target);
                } else {
                    data.getTasks().insertTask("sendUserMessage", targetName + ";" +
                            Config.getTransl("settings", "messages.info.users.races.received.from-admin")
                                    .replace("${raceName}", raceName)
                    );
                }
                s.sendMessage(Config.getTransl("settings", "messages.success.admin.races.targets.set")
                        .replace("${raceName}", raceName)
                        .replace("${username}", targetName));
                break;
            case "fromGui":
                break;
            default:
                break;
        }
    }

    public void resetRace(CommandSender s, String targetName, String typeFrom) {
        switch (typeFrom) {
            case "fromAdmin":
                data.getUsersRaces().updateMemberData(targetName, "raceName", Main.getDefaultRaceRaw());

                Player target = Bukkit.getPlayerExact(targetName);
                if (target != null) {
                    target.sendMessage(Config.getTransl("settings", "messages.info.users.races.reset.from-admin"));
                    setter(target);
                } else {
                    data.getTasks().insertTask("sendUserMessage", targetName + ";" + Config.getTransl("settings", "messages.info.users.races.reset.from-admin"));
                }
                s.sendMessage(Config.getTransl("settings", "messages.success.admin.races.targets.reset")
                        .replace("${username}", targetName));
                break;
            case "fromGui":
                break;
            default:
                break;
        }
    }

    public void resetRace(String targetName, String message) {
        data.getUsersRaces().updateMemberData(targetName, "raceName", Main.getDefaultRaceRaw());

        Player target = Bukkit.getPlayerExact(targetName);
        if (target != null) {
            target.sendMessage(message);
            setter(target);
        } else {
            data.getTasks().insertTask("sendUserMessage", targetName + ";" + message);
        }
    }

    private void setter(Player target) {

    }

    public boolean existRace(String raceName) {
        boolean value = false;
        for (String raceId : generatedRacesId.keySet()) {
            String race = generatedRacesId.get(raceId);
            if (race.equalsIgnoreCase(raceName)) {
                value = true;
                break;
            }
        }
        return value;
    }

    public static HashMap<String, Boolean> getModulesInfo() {
        return modulesInfo;
    }

    public static HashMap<String, String> getGeneratedRacesId() {
        return generatedRacesId;
    }
}
