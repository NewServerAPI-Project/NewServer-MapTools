package net.cg360.nsapi.map;

import org.bukkit.plugin.java.JavaPlugin;

public class MapToolsSpigot extends JavaPlugin {

    public static MapToolsSpigot maptools;

    @Override
    public void onEnable() {
        try {
            maptools = this;
            // Yeah I copied this from NSAPI. Probably not necessary.

        } catch (Exception err){
            maptools = null;
            err.printStackTrace();
            // Just making sure everything is properly nulled.
        }
    }

    public static MapToolsSpigot get() { return maptools; }
    public static boolean isMapToolsLoaded() { return maptools != null; }
}
