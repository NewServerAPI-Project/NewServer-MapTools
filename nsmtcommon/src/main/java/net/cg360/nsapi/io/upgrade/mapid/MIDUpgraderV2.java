package net.cg360.nsapi.io.upgrade.mapid;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.cg360.nsapi.mapid.MapID;
import net.cg360.nsapi.mapid.MIDHeader;

/**
 * @author CG360;
 */
public class MIDUpgraderV2 implements MIDUpgraderBase {

    public static final String KEY_IDDATA = "map";
    public static final String KEY_EXTRADATA = "extra";

    public static final String KEY_DISPLAY_NAME = "display_name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_AUTHOR = "author"; // Single author. Placed at the start of authors if both keys are present, otherwise creates new array
    public static final String KEY_AUTHOR_LIST = "authors";
    public static final String KEY_GAMEMODES = "supported_gamemodes"; // May add singular version in the future ( 2.1? )
    public static final String KEY_SPAWNLISTS = "spawnlists"; // Organised by groups
    //public static final String KEY_SPAWNS = "spawns"; // Just an array of spawns. No grouping. (3.0?)
    public static final String KEY_REGIONS = "regions";
    public static final String KEY_POINT_ENTITIES = "point_entities";
    public static final String KEY_PROPERTIES = "properties"; // Only supports String, Number, and Boolean

    @Override
    public MapID interpretJsonTree(MIDHeader formatProperties, JsonObject jsonDataIn) {
        if(jsonDataIn == null) throw new IllegalArgumentException("Missing Json data in upgrader");

        JsonElement bodyElement = jsonDataIn.get(KEY_IDDATA);
        JsonElement extrasElement = jsonDataIn.get(KEY_EXTRADATA); // Extra data

        MapID.Builder builder = MapID.builder(formatProperties);

        if(bodyElement instanceof JsonObject){
            JsonObject body = (JsonObject) bodyElement;
            JsonElement eName = body.get(KEY_DISPLAY_NAME);
            JsonElement eDesc = body.get(KEY_DESCRIPTION);
            JsonElement eAuthor = body.get(KEY_AUTHOR);
            JsonElement eAuthorList = body.get(KEY_AUTHOR_LIST);
            JsonElement eGamemodes = body.get(KEY_GAMEMODES);
            JsonElement eSpawnlist = body.get(KEY_SPAWNLISTS);
            JsonElement eRegions = body.get(KEY_REGIONS);
            JsonElement ePointEs = body.get(KEY_POINT_ENTITIES);
            JsonElement eProp = body.get(KEY_PROPERTIES);
        }

        if(extrasElement instanceof JsonObject){
            builder.setExtraData((JsonObject) extrasElement);
        }

        return builder.build();
    }

}
