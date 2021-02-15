package net.cg360.nsapi.map.io.load;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.cg360.nsapi.map.mapid.format.MIDHeader;
import net.cg360.nsapi.map.mapid.format.MapID;
import net.cg360.nsapi.map.mapid.MapIDConstants;
import net.cg360.nsapi.commons.exception.UnsupportedFormatException;
import net.cg360.nsapi.map.io.upgrade.mapid.MIDUpgraderBase;
import net.cg360.nsapi.map.io.upgrade.mapid.MIDUpgraderV2;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;

/**
 * @author CG360;
 */
public final class MapIDLoader {

    private static MapIDLoader mapIDLoader;

    private HashMap<Integer, MIDUpgraderBase> versionUpgraders;


    public MapIDLoader() {
        this.versionUpgraders = new HashMap<>();
    }


    public void registerVersionUpgrader(Integer version, MIDUpgraderBase upgrader){
        versionUpgraders.put(version, upgrader);
    }

    public MapID loadMapID(String mapRootPath) throws IOException {
        String path = mapRootPath.endsWith("/") ? mapRootPath : mapRootPath + "/";
        File mapRootFile = new File(mapRootPath);

        if(!mapRootFile.isDirectory()) throw new IllegalArgumentException("Location specified is not a directory");
        String mapIDPath = path + "mapid.json";
        FileReader r = new FileReader(mapIDPath);
        BufferedReader read = new BufferedReader(r);
        Iterator<String> lines = read.lines().iterator();
        String content = "";

        while (lines.hasNext()){
            content = content.concat(lines.next());
        }

        JsonElement result = JsonParser.parseString(content);
        return loadMapIDFromJson(result);
    }

    public MapID loadMapIDFromJson(JsonElement root) { return loadMapIDFromJson(root, null); }
    public MapID loadMapIDFromJson(JsonElement rootElement, String mapPath) {

        if(rootElement instanceof JsonObject){
            JsonObject root = (JsonObject) rootElement;
            MIDHeader header = MIDHeader.getHeaderFromJson(root, mapPath);

            if(versionUpgraders.containsKey(header.getFormatVersion())){
                MIDUpgraderBase upgrade = versionUpgraders.get(header.getFormatVersion());
                return upgrade.interpretJsonTree(header, root);

            } else throw new UnsupportedFormatException(
                    String.format("MapID is not compatible with this version. (Current: %s | Supported: %s)", MapIDConstants.MAPID_FORMAT_VERSION, Arrays.toString(versionUpgraders.keySet().toArray(new Integer[0])))
            );
        } else throw new JsonParseException("Provided json does not have an object root");
    }


    public Optional<MIDUpgraderBase> getVersionUpgrader(Integer version) {
        return Optional.ofNullable(versionUpgraders.get(version));
    }

    public static MapIDLoader get() { return mapIDLoader; }



    static {
        mapIDLoader = new MapIDLoader();
        get().registerVersionUpgrader(2, new MIDUpgraderV2());
    }
}
