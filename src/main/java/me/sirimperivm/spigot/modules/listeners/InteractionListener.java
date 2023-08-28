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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SuppressWarnings("all")
public class InteractionListener implements Listener {

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
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action act = e.getAction();

        if (mods.getModulesInfo().get("destroyUnbreakable")) {
            if (act == Action.RIGHT_CLICK_BLOCK && p.isSneaking()) {
                Block b = e.getClickedBlock();
                Material m = b.getType();
                String path = "settings.modules.destroyUnbreakable";
                List<String> worldBlacklist = conf.getSettings().getStringList(path + ".world-blacklist");
                if (worldBlacklist.contains(p.getLocation().getWorld().getName())) return;
                for (String block : conf.getSettings().getConfigurationSection(path + ".block-list").getKeys(false)) {
                    String blocksPath = "settings.modules.destroyUnbreakable.block-list." + block;
                    Material blockMaterial = Material.getMaterial(conf.getSettings().getString(blocksPath + ".material"));
                    if (blockMaterial == m) {
                        e.setCancelled(true);
                        b.setType(Material.AIR);
                        boolean pickupEnabled = conf.getSettings().getBoolean(blocksPath + ".pickup.enabled");
                        boolean inInventory = conf.getSettings().getBoolean(blocksPath + ".pickup.inInventory");
                        ItemStack is = new ItemStack(blockMaterial);

                        if (pickupEnabled) {
                            if (inInventory && !inventoryFull(p, is)) {
                                p.getInventory().addItem(is);
                            } else {
                                p.getLocation().getWorld().dropItem(p.getLocation(), is);
                            }
                        }
                    }
                }
            }
        }
    }
}
