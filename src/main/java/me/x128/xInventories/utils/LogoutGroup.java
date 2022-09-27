package me.x128.xInventories.utils;

import me.x128.xInventories.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

/**
 * Created by Cole on 4/26/16.
 */
public class LogoutGroup {

    private FileConfiguration config;
    private File file;

    public LogoutGroup(String uuid) {
        //get the group file
        file = new File(Main.getPlugin().getDataFolder() + File.separator + "logout_worlds" + File.separator + uuid + ".yml");
        config = FileManager.getYaml(file);
    }

    public void save(boolean respectAsync) {
        FileManager.saveConfiguraton(file, config, respectAsync);
    }

    public void setGroup(String group, String world) {
        config.set("group", group);
        config.set("world", world);
    }

    public String getGroup() {
        if (config.contains("group")) {
            return config.getString("group");
        }
        return null;
    }

    public String getWorld() {
        if (config.contains("world")) {
            return config.getString("world");
        }
        return null;
    }
}
