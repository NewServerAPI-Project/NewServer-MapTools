package net.cg360.nsapi.io.upgrade.mapid;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.cg360.nsapi.commons.data.MapRegionDataStore;
import net.cg360.nsapi.commons.data.PointEntityDataStore;
import net.cg360.nsapi.commons.math.PosRot;
import net.cg360.nsapi.mapid.MapID;
import net.cg360.nsapi.mapid.MIDHeader;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

/**
 * @author CG360;
 */
public class MIDUpgraderV2 implements MIDUpgraderBase {

    // UPDATE NOTES:
    // Here's a list of considerations to make every time this is modified for
    // compatibility with a new version.
    //
    // - Use old PosRot parsing (Copy from commons to here)
    // - Use old MapRegion parsing (Copy from commons to here)
    // - Use old PointEntity parsing (Copy from commons to here)


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
        if(jsonDataIn == null) throw new IllegalArgumentException("Missing Json data in upgrader. Data cannot be null.");

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

            if(eName instanceof JsonPrimitive) {
                JsonPrimitive prim = (JsonPrimitive) eName;
                builder.setDisplayName(prim.getAsString());
            }

            if(eDesc instanceof JsonPrimitive) {
                JsonPrimitive prim = (JsonPrimitive) eDesc;
                builder.setDescription(prim.getAsString());
            }


            // Authors + Gamemodes are added on one by one.

            if(eAuthor instanceof JsonPrimitive) {
                JsonPrimitive prim = (JsonPrimitive) eAuthor;
                builder.addAuthor(prim.getAsString());
            }

            if(eAuthorList instanceof JsonArray) {
                JsonArray array = (JsonArray) eAuthorList;

                for(JsonElement i: array){

                    if(i instanceof JsonPrimitive){
                        JsonPrimitive prim = (JsonPrimitive) i;
                        builder.addAuthor(prim.getAsString());
                    }
                }
            }

            if(eGamemodes instanceof JsonArray) {
                JsonArray array = (JsonArray) eGamemodes;

                for(JsonElement i: array){

                    if(i instanceof JsonPrimitive){
                        JsonPrimitive prim = (JsonPrimitive) i; // \/ Do case-sensitive check for ids \/
                        String id = formatProperties.hasCaseSensitiveIDs() ? prim.getAsString().trim() : prim.getAsString().trim().toLowerCase();
                        builder.addSupportedGamemode(id);
                    }
                }
            }

            if(eSpawnlist instanceof JsonObject){
                JsonObject spawnlistObject = (JsonObject) eSpawnlist;

                for(Map.Entry<String, JsonElement> pairs: spawnlistObject.entrySet()){
                    String spawnlistID = formatProperties.hasCaseSensitiveIDs() ? pairs.getKey().trim() : pairs.getKey().trim().toLowerCase();

                    if(pairs.getValue() instanceof JsonArray){
                        JsonArray spawnArray = (JsonArray) pairs.getValue();

                        for(JsonElement arrayChildElement: spawnArray){

                            if(arrayChildElement instanceof JsonObject){
                                JsonObject positionObject = (JsonObject) arrayChildElement;
                                PosRot parsedPosition = PosRot.parseFromJson(positionObject); // Use utility parser.
                                builder.addSpawn(spawnlistID, parsedPosition);
                            }
                        }

                    } else if (pairs.getValue() instanceof JsonObject){
                        JsonObject positionObject = (JsonObject) pairs.getValue();
                        PosRot parsedPosition = PosRot.parseFromJson(positionObject); // Use utility parser. This could be an issue in future versions
                        builder.addSpawn(spawnlistID, parsedPosition);
                    }


                }
            }

            if(eRegions instanceof JsonObject){
                JsonObject regionsRoot = (JsonObject) eRegions;

                for(Map.Entry<String, JsonElement> pairs : regionsRoot.entrySet()){

                    if(pairs.getValue() instanceof JsonObject){
                        JsonObject mapRegion = (JsonObject) pairs.getValue();
                        String identity = formatProperties.hasCaseSensitiveIDs() ? pairs.getKey().trim() : pairs.getKey().trim().toLowerCase();
                        builder.setMapRegion(identity, MapRegionDataStore.buildFromJson(identity, mapRegion));
                    }
                }

            } else if(eRegions instanceof JsonArray) {
                JsonArray regionArray = (JsonArray) eRegions;
                // Anonymous regions - They use a generated ID.
                for(JsonElement entry : regionArray){

                    if(entry instanceof JsonObject){
                        // Alternative notation? Could the ID be inside the mapregion? This would make an array the better option.
                        JsonObject mapRegion = (JsonObject) entry;
                        MapRegionDataStore data = MapRegionDataStore.buildFromJson(null, mapRegion);
                        builder.setMapRegion(data.getIdentifier(), data); // Identifier is lowercase if generated.
                    }
                }
            }

            if(ePointEs instanceof JsonObject){
                JsonObject pointEntitiesRoot = (JsonObject) ePointEs;

                for(Map.Entry<String, JsonElement> pairs : pointEntitiesRoot.entrySet()){

                    if(pairs.getValue() instanceof JsonObject){
                        JsonObject pointEntity = (JsonObject) pairs.getValue();
                        String identity = formatProperties.hasCaseSensitiveIDs() ? pairs.getKey().trim() : pairs.getKey().trim().toLowerCase();
                        builder.setPointEntity(identity, PointEntityDataStore.buildFromJson(identity, pointEntity));
                    }
                }

            } else if(eRegions instanceof JsonArray) {
                JsonArray pointEntityArray = (JsonArray) eRegions;
                // Anonymous regions - They use a generated ID.
                for(JsonElement entry : pointEntityArray){

                    if(entry instanceof JsonObject){
                        JsonObject pointEntity = (JsonObject) entry;
                        PointEntityDataStore data = PointEntityDataStore.buildFromJson(null, pointEntity);
                        builder.setPointEntity(data.getIdentifier(), data); // Identifier is lowercase if generated.
                    }
                }
            }


        }

        if(extrasElement instanceof JsonObject){
            builder.setExtraData((JsonObject) extrasElement); // Just chuck the extra data in as a JsonObject. It should deepcopy it.
        }

        return builder.build();
    }

}
