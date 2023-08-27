package me.sirimperivm.spigot.assets.managers;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.others.Strings;
import me.sirimperivm.spigot.assets.utils.Colors;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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

    private int settersLoopInt = 0;

    public void setupModules() {
        modulesInfo = new HashMap<String, Boolean>();
        for (String module : conf.getSettings().getConfigurationSection("settings.modules").getKeys(false)) {
            modulesInfo.put(module, conf.getSettings().getBoolean("settings.modules." + module + ".enable"));
        }
        if (modulesInfo.get("races")) {
            generatedRacesId = data.getRaces().getGeneratedId();
        }
        executeTasks();
        if (modulesInfo.get("races")) {
            if (settersLoopInt == 0) {
                settersLoop();
            } else {
                if (settersLoop().isQueued(settersLoopInt)) {
                    settersLoop().cancelTask(settersLoopInt);
                    settersLoop();
                }
            }
        }
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

                    if (taskType.equalsIgnoreCase("resetRace")) {
                        String username = taskValue.split(";")[0];
                        String actualUserRace = taskValue.split(";")[1];
                        Player target = Bukkit.getPlayerExact(username);
                        if (target != null) {
                            resetter(target, actualUserRace);
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

    private BukkitScheduler settersLoop() {
        BukkitScheduler schedule = Bukkit.getScheduler();
        settersLoopInt = schedule.runTaskTimer(plugin, () -> {
            List<String> worldBlackList = conf.getSettings().getStringList("settings.modules.races.world-blacklist");
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!worldBlackList.contains(p.getWorld().getName())) {
                    setter(p);
                } else {
                    String userRace = data.getUsersRaces().getUserRace(p.getName());
                    if (!userRace.equalsIgnoreCase(Main.getDefaultRaceRaw())) {
                        resetter(p, userRace);
                    }
                }
            }
        }, 20, 20).getTaskId();
        return schedule;
    }

    public void deleteTask(int taskId) {
        data.getTasks().deleteTask(taskId);
    }

    public void createRace(CommandSender s, String raceName, String raceTitle) {
        if (!existRace(raceName)) {
            String generateID = Strings.generateUUID();
            for (String generated : generatedRacesId.keySet()) {
                if (generateID.equalsIgnoreCase(generated)) {
                    generateID = Strings.generateUUID();
                }
            }
            generatedRacesId.put(generateID, raceName);

            conf.getRaces().set("races." + raceName + ".settings.raceId", generateID);
            conf.getRaces().set("races." + raceName + ".settings.raceTitle", raceTitle);
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
                            .replace("${raceTitle}", Colors.text(raceTitle))
                            .replace("${raceId}", generateID)
                    ));
                }
            }
        } else {
            if (s instanceof Player) {
                s.sendMessage(Config.getTransl("settings", "messages.errors.admin.races.already-exists"));
            }
        }
    }

    public void deleteRace(CommandSender s, String raceName) {
        if (existRace(raceName) && !raceName.equalsIgnoreCase(Main.getDefaultRaceRaw())) {
            for (String targets : data.getUsersRaces().playersInARace(raceName)) {
                deathResetUserRace(targets, raceName);
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

    public void adminSetUserRace(CommandSender s, String targetName, String raceName) {
        String actualUserRace = data.getUsersRaces().getUserRace(targetName);
        if (actualUserRace.equalsIgnoreCase(Main.getDefaultRaceRaw())) {
            Player target = Bukkit.getPlayerExact(targetName);
            data.getUsersRaces().updateMemberData(targetName, "raceName", raceName);
            if (target != null) {
                target.sendMessage(Config.getTransl("settings", "messages.info.users.races.received.from-admin")
                        .replace("${raceName}", raceName));
            } else {
                data.getTasks().insertTask("sendUserMessage", targetName + ";" + Config.getTransl("settings", "messages.info.users.races.received.from-admin")
                        .replace("${raceName}", raceName));
            }
            s.sendMessage(Config.getTransl("settings", "messages.success.admin.races.targets.set")
                    .replace("${username}", targetName)
                    .replace("${raceName}", raceName));
        } else {
            s.sendMessage(Config.getTransl("settings", "messages.errors.admin.races.targets.already-have-ones"));
        }
    }

    public void setUserRace(Player p, String raceSelected) {
        String playerName = p.getName();
        String actualUserRace = data.getUsersRaces().getUserRace(playerName);

        if (actualUserRace.equalsIgnoreCase(Main.getDefaultRaceRaw())) {
            data.getUsersRaces().updateMemberData(playerName, "raceName", raceSelected);
            setter(p);
            p.sendMessage(Config.getTransl("settings", "messages.success.users.races.choose")
                    .replace("${raceName}", raceSelected));
        } else {
            p.sendMessage(Config.getTransl("settings", "messages.errors.users.races.already-have"));
        }
    }

    public void adminResetUserRace(CommandSender s, String targetName) {
        String actualUserRace = data.getUsersRaces().getUserRace(targetName);
        if (!actualUserRace.equalsIgnoreCase(Main.getDefaultRaceRaw())) {
            data.getUsersRaces().updateMemberData(targetName, "raceName", Main.getDefaultRaceRaw());
            Player target = Bukkit.getPlayerExact(targetName);
            if (target != null) {
                resetter(target, actualUserRace);
                target.sendMessage(Config.getTransl("settings", "messages.info.users.races.reset.from-admin")
                        .replace("${raceName}", actualUserRace));
            } else {
                data.getTasks().insertTask("resetRace", targetName + ";" + actualUserRace);
                data.getTasks().insertTask("sendUserMessage", targetName + ";" + Config.getTransl("settings", "messages.info.users.races.reset.from-admin")
                        .replace("${raceName}", actualUserRace));
            }
            s.sendMessage(Config.getTransl("settings", "messages.success.admin.races.targets.reset")
                    .replace("${username}", targetName));
        } else {
            s.sendMessage(Config.getTransl("settings", "messages.errors.admin.races.targets.dont-have"));
        }
    }
    
    public void deathResetUserRace(String targetName, String raceName) {
        String userRace = data.getUsersRaces().getUserRace(targetName);
        if (userRace.equalsIgnoreCase(raceName)) {
            Player target = Bukkit.getPlayerExact(targetName);
            String defaultRaceRaw = Main.getDefaultRaceRaw();
            data.getUsersRaces().updateMemberData(targetName, "raceName", defaultRaceRaw);
            if (target != null) {
                resetter(target, raceName);
                target.sendMessage(Config.getTransl("settings", "messages.info.users.races.reset.from-death")
                        .replace("${raceName}", raceName));
            } else {
                data.getTasks().insertTask("resetRace", targetName + ";" + raceName);
                data.getTasks().insertTask("sendUserMessage", targetName + ";" + Config.getTransl("settings", "messages.info.users.races.reset.from-death")
                        .replace("${raceName}", raceName));
            }
        }
    }

    public void resetUserRace(Player p) {
        String username = p.getName();
        String userRace = data.getUsersRaces().getUserRace(username);
        String defaultRaceRaw = Main.getDefaultRaceRaw();
        if (!userRace.equalsIgnoreCase(defaultRaceRaw)) {
            data.getUsersRaces().updateMemberData(username, "raceName", defaultRaceRaw);
            resetter(p, userRace);
            p.sendMessage(Config.getTransl("settings", "messages.success.users.races.reset"));
        } else {
            p.sendMessage(Config.getTransl("settings", "messages.errors.users.races.dont-have"));
        }
    }

    public void setter(Player target) {
        String targetName = target.getName();
        String raceName = data.getUsersRaces().getUserRace(targetName);

        int durationInit = conf.getSettings().getInt("settings.modules.races.durations.init");
        int durationEnd = conf.getSettings().getInt("settings.modules.races.durations.end");

        long dayStart = conf.getSettings().getLong("settings.modules.races.day.start");
        long dayEnd = conf.getSettings().getLong("settings.modules.races.day.end");
        World w = target.getLocation().getWorld();
        if (w.getTime() >= dayStart && w.getTime() < dayEnd) {
            List<String> dayEffects = conf.getRaces().getStringList("races." + raceName + ".dayEffects");
            List<String> nightEffects = conf.getRaces().getStringList("races." + raceName + ".nightEffects");
            List<String> dayAttributes = conf.getRaces().getStringList("races." + raceName + ".dayAttributes");
            List<String> nightAttributes = conf.getRaces().getStringList("races." + raceName + ".nightAttributes");

            for (PotionEffect eff : target.getActivePotionEffects()) {
                boolean infinte = eff.isInfinite();
                if (infinte) {
                    target.removePotionEffect(eff.getType());
                }
            }

            for (String effectString : dayEffects) {
                String[] splitter = effectString.split(";");
                String effectName = splitter[0];
                int effectPower = Integer.parseInt(splitter[1]);
                PotionEffectType effect = PotionEffectType.getByName(effectName);

                target.addPotionEffect(new PotionEffect(effect, -1, effectPower - 1, false, false, false));
            }

            for (String attributeString : nightAttributes) {
                String[] splitter = attributeString.split(";");
                Attribute attribute = Attribute.valueOf(splitter[0]);
                double attributeLevel = Double.parseDouble(splitter[1]);

                if (target.getAttribute(attribute).getValue() == attributeLevel) {
                    target.getAttribute(attribute).setBaseValue(target.getAttribute(attribute).getDefaultValue());
                }
            }

            for (String attributeString : dayAttributes) {
                String[] splitter = attributeString.split(";");
                Attribute attribute = Attribute.valueOf(splitter[0]);
                double attributeLevel = Double.parseDouble(splitter[1]);

                target.getAttribute(attribute).setBaseValue(attributeLevel);
            }
        } else {
            List<String> dayEffects = conf.getRaces().getStringList("races." + raceName + ".dayEffects");
            List<String> nightEffects = conf.getRaces().getStringList("races." + raceName + ".nightEffects");
            List<String> dayAttributes = conf.getRaces().getStringList("races." + raceName + ".dayAttributes");
            List<String> nightAttributes = conf.getRaces().getStringList("races." + raceName + ".nightAttributes");

            for (PotionEffect eff : target.getActivePotionEffects()) {
                boolean infinte = eff.isInfinite();
                if (infinte) {
                    target.removePotionEffect(eff.getType());
                }
            }

            for (String effectString : nightEffects) {
                String[] splitter = effectString.split(";");
                String effectName = splitter[0];
                int effectPower = Integer.parseInt(splitter[1]);
                PotionEffectType effect = PotionEffectType.getByName(effectName);

                target.addPotionEffect(new PotionEffect(effect, -1, effectPower - 1, false, false, false));
            }

            for (String attributeString : dayAttributes) {
                String[] splitter = attributeString.split(";");
                Attribute attribute = Attribute.valueOf(splitter[0]);
                double attributeLevel = Double.parseDouble(splitter[1]);

                if (target.getAttribute(attribute).getValue() == attributeLevel) {
                    target.getAttribute(attribute).setBaseValue(target.getAttribute(attribute).getDefaultValue());
                }
            }

            for (String attributeString : nightAttributes) {
                String[] splitter = attributeString.split(";");
                Attribute attribute = Attribute.valueOf(splitter[0]);
                double attributeLevel = Double.parseDouble(splitter[1]);

                target.getAttribute(attribute).setBaseValue(attributeLevel);
            }
        }
    }

    public void resetter(Player target, String actualUserRace) {
        String targetName = target.getName();
        List<String> dayEffects = conf.getRaces().getStringList("races." + actualUserRace + ".dayEffects");
        List<String> nightEffects = conf.getRaces().getStringList("races." + actualUserRace + ".nightEffects");
        List<String> dayAttributes = conf.getRaces().getStringList("races." + actualUserRace + ".dayAttributes");
        List<String> nightAttributes = conf.getRaces().getStringList("races." + actualUserRace + ".nightAttributes");

        int durationInit = conf.getSettings().getInt("settings.modules.races.durations.init");
        int durationEnd = conf.getSettings().getInt("settings.modules.races.durations.end");

        for (PotionEffect eff : target.getActivePotionEffects()) {
            boolean infinte = eff.isInfinite();
            if (infinte) {
                target.removePotionEffect(eff.getType());
            }
        }

        for (String attributeString : dayAttributes) {
            String[] splitter = attributeString.split(";");
            Attribute attribute = Attribute.valueOf(splitter[0]);
            double attributeLevel = Double.parseDouble(splitter[1]);

            if (target.getAttribute(attribute).getValue() == attributeLevel) {
                target.getAttribute(attribute).setBaseValue(target.getAttribute(attribute).getDefaultValue());
            }
        }

        for (String attributeString : nightAttributes) {
            String[] splitter = attributeString.split(";");
            Attribute attribute = Attribute.valueOf(splitter[0]);
            double attributeLevel = Double.parseDouble(splitter[1]);

            if (target.getAttribute(attribute).getValue() == attributeLevel) {
                target.getAttribute(attribute).setBaseValue(target.getAttribute(attribute).getDefaultValue());
            }
        }
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

    public String getRaceTitle(String raceName) {
        return Config.getTransl("races", "races." + raceName + ".settings.raceTitle");
    }

    public static HashMap<String, Boolean> getModulesInfo() {
        return modulesInfo;
    }

    public static HashMap<String, String> getGeneratedRacesId() {
        return generatedRacesId;
    }
}
