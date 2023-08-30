package me.sirimperivm.spigot.modules.listeners;

import me.sirimperivm.spigot.Main;
import me.sirimperivm.spigot.assets.managers.Config;
import me.sirimperivm.spigot.assets.managers.Db;
import me.sirimperivm.spigot.assets.managers.Modules;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("all")
public class BreakListener implements Listener {

    private static Main plugin = Main.getPlugin();
    private void log(String message) {plugin.log(message);}
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    private boolean inventoryFull(Player p, ItemStack is) {
        boolean value = true;
        Inventory i = p.getInventory();
        for (ItemStack item : i.getContents()) {
            if (item == null || (item == is && item.getAmount() != item.getMaxStackSize())) {
                value = false;
                break;
            }
        }
        return value;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();

        if (mods.getModulesInfo().get("extraCave")) {
            String worldName = b.getLocation().getWorld().getName();
            String path = "settings.modules.extraCave";
            List<String> worldWhitelist = conf.getSettings().getStringList(path + ".world-whitelist");

            if (worldWhitelist.contains(worldName)) {
                if (mods.getExtraCaveBypasses().get(p.getName())) return;
                List<String> blockBreakList = conf.getSettings().getStringList(path + ".block-break-list");
                HashMap<String, Boolean> blockList = new HashMap<String, Boolean>();
                for (String line : blockBreakList) {
                    String[] splitter = line.split(";");
                    String material = splitter[0];
                    boolean regenerate = Boolean.valueOf(splitter[1]);
                    blockList.put(material, regenerate);
                }
                Material type = b.getType();
                if (blockList.containsKey(type.toString())) {
                    boolean regenerate = blockList.get(type.toString());
                    if (regenerate) {
                        e.setCancelled(true);
                        for (ItemStack drop : b.getDrops()) {
                            if (!inventoryFull(p, drop)) {
                                p.getInventory().addItem(drop);
                            } else {
                                p.getLocation().getWorld().dropItem(p.getLocation(), drop);
                            }
                        }
                        Material regenMaterial = Material.getMaterial(conf.getSettings().getString("settings.modules.extraCave.regenerativeOre"));
                        mods.getRegenerativeBlocks().put(b.getLocation(), type);
                        b.setType(regenMaterial);
                        long timeCooldown = type.toString().startsWith("DEEPSLATE_") ? conf.getSettings().getLong(path + ".timeCooldowns.deepslate") * 20 : conf.getSettings().getLong(path + ".timeCooldowns.normal") * 20;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                b.setType(type);
                                mods.getRegenerativeBlocks().remove(b.getLocation());
                            }
                        }.runTaskLater(plugin, timeCooldown);
                    }
                } else {
                    e.setCancelled(true);
                }
            }
        }
    }
}
