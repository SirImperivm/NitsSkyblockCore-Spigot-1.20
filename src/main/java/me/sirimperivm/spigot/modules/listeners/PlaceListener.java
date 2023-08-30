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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("all")
public class PlaceListener implements Listener {

    private static Main plugin = Main.getPlugin();
    private void log(String message) {plugin.log(message);}
    private static Config conf = Main.getConf();
    private static Modules mods = Main.getMods();
    private static Db data = Main.getData();

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();

        if (mods.getModulesInfo().get("extraCave")) {
            String world = p.getLocation().getWorld().getName();
            String path = "settings.modules.extraCave";
            List<String> worldWhitelist = conf.getSettings().getStringList(path + ".world-whitelist");

            if (worldWhitelist.contains(world)) {
                List<String> blockPlaceList = conf.getSettings().getStringList(path + ".block-place-list");
                HashMap<String, Boolean> blockList = new HashMap<String, Boolean>();
                for (String line : blockPlaceList) {
                    String[] splitter = line.split(";");
                    String material = splitter[0];
                    boolean regenerate = Boolean.valueOf(splitter[1]);
                    blockList.put(material, regenerate);
                }
                Material type = b.getType();
                if (blockList.containsKey(type.toString())) {
                    boolean regenerate = blockList.get(type.toString());
                    if (regenerate) {
                        mods.getRegenerativePlacedBlocks().put(b.getLocation(), Material.AIR);
                        long timeCooldown = conf.getSettings().getLong(path + ".timeCooldowns.blockPlaced") * 20;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                b.getLocation().getBlock().setType(Material.AIR);
                                mods.getRegenerativePlacedBlocks().remove(b.getLocation());
                            }
                        }.runTaskLater(plugin, timeCooldown);
                    }
                } else {
                    e.setCancelled(true);
                    p.sendMessage(Config.getTransl("settings", "settings.modules.extraCave.deny-messages.block-place"));
                }
            }
        }
    }
}
