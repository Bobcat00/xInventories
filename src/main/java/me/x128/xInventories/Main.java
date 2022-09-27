package me.x128.xInventories;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import me.x128.xInventories.command.xInventoriesCommand;
import me.x128.xInventories.listener.GamemodeChangeEvent;
import me.x128.xInventories.listener.LogInOutListener;
import me.x128.xInventories.listener.WorldChangeEvent;
import me.x128.xInventories.utils.LogoutGroup;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Created by Cole on 4/11/16.
 */
public class Main extends JavaPlugin {
    private static Plugin plugin;

    public void onEnable() {
        plugin = this;

        //get the config and set it up with defaults if it does not exist
        File config = new File(this.getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();

            for (World w : Bukkit.getWorlds()) {
                getConfig().set("worlds." + w.getName(), "default");
            }
            saveConfig();

            this.getLogger().info("New configuration file created with defaults");
        }
        if (getConfig().getBoolean("respect-gamemode")) {
            Bukkit.getServer().getPluginManager().registerEvents(new GamemodeChangeEvent(), this);
        }
        Bukkit.getServer().getPluginManager().registerEvents(new WorldChangeEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new LogInOutListener(), this);
        getCommand("xinventories").setExecutor(new xInventoriesCommand());

        checkFileStructure();
        this.getLogger().info("Plugin enabled");
    }

    public void onDisable() {
        plugin = null;
        this.getLogger().info("Plugin disabled");
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public void checkFileStructure() {
        //get groups folder
        File f = new File(this.getDataFolder() + File.separator + "groups");
        //check if groups exist
        if (f.isDirectory()) {
            //iterate through folders in the group files
            for (File subFile : f.listFiles()) {
                //check if group file is a folder
                if (subFile.isDirectory()) {
                    //create survival and crative folders
                    new File(subFile.getPath() + File.separator + "survival").mkdir();
                    new File(subFile.getPath() + File.separator + "creative").mkdir();

                    //move any existing ymls into survival folder
                    for (File groupFile : subFile.listFiles()) {
                        if (groupFile.getName().endsWith(".yml")) {
                            String nFP = subFile.getPath() + File.separator + "survival" + File.separator + groupFile.getName();
                            groupFile.renameTo(new File(nFP));
                        }
                    }
                }
            }
        }

        //clean up dumb file in version 2.1 - we now track the group and world in logout_worlds.yml
        File lg = new File(Main.getPlugin().getDataFolder() + File.separator + "logout_groups.yml");
        if (lg.exists()) {
            lg.delete();
        }

        //convert logout_worlds.yml to individual uuid.yml files
        File logoutWorlds = new File(Main.getPlugin().getDataFolder() + File.separator + "logout_worlds.yml");
        if (logoutWorlds.exists()) {
            YamlConfiguration worldsConfig = YamlConfiguration.loadConfiguration(logoutWorlds);
            ConfigurationSection worldsConfSect = worldsConfig.getConfigurationSection("v2-logout");
            int uuidCount = 0;
            if (worldsConfSect != null) {
                // Loop through the UUID keys
                for (String uuid : worldsConfSect.getKeys(false)) {
                    String group = worldsConfSect.getString(uuid + ".group");
                    String world = worldsConfSect.getString(uuid + ".world");
                    LogoutGroup logoutGroup = new LogoutGroup(uuid);
                    logoutGroup.setGroup(group, world);
                    logoutGroup.save(false);
                    ++uuidCount;
                }
            }
            //rename old logout_worlds.yml
            File logoutWorldsOld = new File(Main.getPlugin().getDataFolder() + File.separator + "logout_worlds_OLD.yml");
            logoutWorlds.renameTo(logoutWorldsOld);
            this.getLogger().info(uuidCount + " UUIDs converted from logout_worlds.yml");
        }
    }

    public static MagicAPI getMagicAPI() {
        Plugin magicPlugin = Bukkit.getPluginManager().getPlugin("Magic");
        if (magicPlugin == null || !(magicPlugin instanceof MagicAPI)) {
            return null;
        }
        return (MagicAPI)magicPlugin;
    }

}
