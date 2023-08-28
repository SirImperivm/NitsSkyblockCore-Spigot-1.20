package me.sirimperivm.spigot.assets.managers;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.dependencies.Vault;
import me.sirimperivm.spigot.assets.others.General;
import me.sirimperivm.spigot.assets.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class Gui {

    private static Main plugin = Main.getPlugin();
    private static Config conf = Main.getConf();
    private static Db data = Main.getData();
    private static Modules mods = Main.getMods();

    public Inventory mainRacesGui(Player p) {
        String title = Config.getTransl("guis", "guis.mainRacesGui.title");
        int size = 9* conf.getGuis().getInt("guis.mainRacesGui.rows");

        Inventory inv = Bukkit.createInventory(null, size, title);

        for (String item : conf.getGuis().getConfigurationSection("guis.mainRacesGui.items").getKeys(false)) {
            String itemsPath = "guis.mainRacesGui.items." + item;
            String actionType = conf.getGuis().getString(itemsPath + ".action");
            if (actionType.equalsIgnoreCase("CHOOSE_RACE")) {
                boolean hasRequirements = true;
                for (String requirement : conf.getGuis().getConfigurationSection("guis.mainRacesGui.items." + item + ".requirements").getKeys(false)) {
                    String requirementPath = "guis.mainRacesGui.items." + item + ".requirements." + requirement;
                    String requirementType = conf.getGuis().getString(requirementPath + ".type");
                    if (requirementType.equalsIgnoreCase("permission")) {
                        String node = conf.getGuis().getString(requirementPath + ".node");
                        if (p.hasPermission(node)) {
                            hasRequirements = true;
                        } else {
                            hasRequirements = false;
                        }
                    }

                    if (requirementType.equalsIgnoreCase("money")) {
                        double moneyValue = conf.getGuis().getDouble(requirementPath + ".value");
                        double balance = Vault.getEcon().getBalance(p);
                        if (balance >= moneyValue) {
                            hasRequirements = true;
                        } else {
                            hasRequirements = false;
                        }
                    }
                }
                String raceName = conf.getGuis().getString(itemsPath + ".settings.raceName");
                if (hasRequirements) {
                    String path = "guis.mainRacesGui.items." + item + ".available";
                    List<Integer> slots = conf.getGuis().getIntegerList(itemsPath + ".slots");
                    ItemStack material = new ItemStack(Material.getMaterial(conf.getGuis().getString(path + ".material")));
                    ItemMeta meta = material.getItemMeta();
                    String displayName = conf.getGuis().getString(path + ".displayName");
                    if (!displayName.equalsIgnoreCase("null")) {
                        meta.setDisplayName(Colors.text(displayName));
                    }
                    List<String> lore = new ArrayList<String>();
                    for (String line : conf.getGuis().getStringList(path + ".lore")) {
                        lore.add(Colors.text(line
                                .replace("${raceTitle}", mods.getRaceTitle(raceName))
                                .replace("${raceName}", raceName)
                        ));
                    }
                    meta.setLore(lore);
                    boolean glowing = conf.getGuis().getBoolean(path + ".glowing");
                    if (glowing) {
                        meta.addEnchant(Enchantment.DURABILITY, 1, true);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    }
                    for (String flag : conf.getGuis().getStringList(path + ".itemFlags")) {
                        meta.addItemFlags(ItemFlag.valueOf(flag));
                    }
                    meta.setCustomModelData(conf.getGuis().getInt(path + ".model"));
                    material.setItemMeta(meta);

                    for (Integer i : slots) {
                        inv.setItem(i, material);
                    }
                } else {
                    String path = "guis.mainRacesGui.items." + item + ".not-available";
                    List<Integer> slots = conf.getGuis().getIntegerList(itemsPath + ".slots");
                    ItemStack material = new ItemStack(Material.getMaterial(conf.getGuis().getString(path + ".material")));
                    ItemMeta meta = material.getItemMeta();
                    String displayName = conf.getGuis().getString(path + ".displayName");
                    if (!displayName.equalsIgnoreCase("null")) {
                        meta.setDisplayName(Colors.text(displayName));
                    }
                    List<String> lore = new ArrayList<String>();
                    for (String line : conf.getGuis().getStringList(path + ".lore")) {
                        lore.add(Colors.text(line
                                .replace("${raceTitle}", mods.getRaceTitle(raceName))
                                .replace("${raceName}", raceName)
                        ));
                    }
                    meta.setLore(lore);
                    boolean glowing = conf.getGuis().getBoolean(path + ".glowing");
                    if (glowing) {
                        meta.addEnchant(Enchantment.DURABILITY, 1, true);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    }
                    for (String flag : conf.getGuis().getStringList(path + ".itemFlags")) {
                        meta.addItemFlags(ItemFlag.valueOf(flag));
                    }
                    meta.setCustomModelData(conf.getGuis().getInt(path + ".model"));
                    material.setItemMeta(meta);

                    for (Integer i : slots) {
                        inv.setItem(i, material);
                    }
                }

                if (raceName.equalsIgnoreCase(data.getUsersRaces().getUserRace(p.getName()))) {
                    String path = "guis.mainRacesGui.items." + item + ".already-have";
                    List<Integer> slots = conf.getGuis().getIntegerList(itemsPath + ".slots");
                    ItemStack material = new ItemStack(Material.getMaterial(conf.getGuis().getString(path + ".material")));
                    ItemMeta meta = material.getItemMeta();
                    String displayName = conf.getGuis().getString(path + ".displayName");
                    if (!displayName.equalsIgnoreCase("null")) {
                        meta.setDisplayName(Colors.text(displayName));
                    }
                    List<String> lore = new ArrayList<String>();
                    for (String line : conf.getGuis().getStringList(path + ".lore")) {
                        lore.add(Colors.text(line
                                .replace("${raceTitle}", mods.getRaceTitle(raceName))
                                .replace("${raceName}", raceName)
                        ));
                    }
                    meta.setLore(lore);
                    boolean glowing = conf.getGuis().getBoolean(path + ".glowing");
                    if (glowing) {
                        meta.addEnchant(Enchantment.DURABILITY, 1, true);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    }
                    for (String flag : conf.getGuis().getStringList(path + ".itemFlags")) {
                        meta.addItemFlags(ItemFlag.valueOf(flag));
                    }
                    meta.setCustomModelData(conf.getGuis().getInt(path + ".model"));
                    material.setItemMeta(meta);

                    for (Integer i : slots) {
                        inv.setItem(i, material);
                    }
                }
            } else {
                List<Integer> slots = conf.getGuis().getIntegerList(itemsPath + ".slots");
                ItemStack material = new ItemStack(Material.getMaterial(conf.getGuis().getString(itemsPath + ".material")));
                ItemMeta meta = material.getItemMeta();
                String displayName = conf.getGuis().getString(itemsPath + ".displayName");
                if (!displayName.equalsIgnoreCase("null")) {
                    meta.setDisplayName(Colors.text(displayName));
                }
                meta.setLore(General.lore(conf.getGuis().getStringList(itemsPath + ".lore")));
                boolean glowing = conf.getGuis().getBoolean(itemsPath + ".glowing");
                if (glowing) {
                    meta.addEnchant(Enchantment.DURABILITY, 1, false);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                meta.setCustomModelData(conf.getGuis().getInt(itemsPath + ".model"));
                material.setItemMeta(meta);

                for (Integer i : slots) {
                    inv.setItem(i, material);
                }
            }
        }

        return inv;
    }
}
