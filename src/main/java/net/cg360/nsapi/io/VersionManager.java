package net.cg360.nsapi.io;

import net.cg360.nsapi.io.versions.MIDUpgraderBase;
import net.cg360.nsapi.io.versions.MIDUpgraderV2;

import java.util.HashMap;
import java.util.Optional;

public final class VersionManager {

    private static VersionManager versionManager;

    private HashMap<Integer, MIDUpgraderBase> versionUpgraders;


    public VersionManager() { this.versionUpgraders = new HashMap<>(); }


    public void registerVersionUpgrader(Integer version, MIDUpgraderBase upgrader){
        versionUpgraders.put(version, upgrader);
    }

    public Optional<MIDUpgraderBase> getVersionUpgrader(Integer version) {
        return Optional.ofNullable(versionUpgraders.get(version));
    }

    public static VersionManager get() { return versionManager; }



    static {
        versionManager = new VersionManager();
        get().registerVersionUpgrader(2, new MIDUpgraderV2());
    }
}
