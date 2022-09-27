package me.x128.xInventories.listener;

import me.x128.xInventories.Main;
import me.x128.xInventories.utils.InventoryInstance;
import me.x128.xInventories.utils.LogoutGroup;
import me.x128.xInventories.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

/**
 * Created by Cole on 4/26/16.
 *
 * Designed to save a player's logout group to file, and check it when he logs back in
 * if the world differs, save his current inventory to the old group and load the new inventory from file
 */
public class LogInOutListener implements Listener {

    @EventHandler
    public void onLogout(PlayerQuitEvent ev) {
        String uuid = ev.getPlayer().getUniqueId().toString();
        String group = PlayerUtil.getPlayerCurrentGroup(ev.getPlayer());
        LogoutGroup lg = new LogoutGroup(uuid);
        lg.setGroup(group, ev.getPlayer().getWorld().getName());
        lg.save(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent ev) {
        Player p = ev.getPlayer();
        String uuid = p.getUniqueId().toString();
        LogoutGroup lg = new LogoutGroup(uuid);

        String prevGroup = lg.getGroup();
        String curGroup = PlayerUtil.getPlayerCurrentGroup(p);

        String prevWorld = lg.getWorld();
        String curWorld = p.getWorld().getName();

        if ((prevGroup != null && !curGroup.equalsIgnoreCase(prevGroup)) && (prevWorld != null && !curWorld.equalsIgnoreCase(prevWorld))) {
            if (Bukkit.getWorld(prevWorld) != null) {
                return;
            }
            Main.getPlugin().getLogger().info(p.getName() + " has logged into an unloaded world");
            //switch player inventory groups
            InventoryInstance fromInv = new InventoryInstance(p);
            fromInv.setGroup(prevGroup);
            fromInv.serialize();
            InventoryInstance toInv = new InventoryInstance(p, curGroup, p.getGameMode());
            toInv.append();
        }
    }
}
