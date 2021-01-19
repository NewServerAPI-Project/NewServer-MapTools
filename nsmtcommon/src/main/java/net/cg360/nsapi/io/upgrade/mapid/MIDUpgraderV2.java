package net.cg360.nsapi.io.upgrade.mapid;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.cg360.nsapi.mapid.MapID;
import net.cg360.nsapi.mapid.MIDHeader;

/**
 * @author CG360;
 */
public class MIDUpgraderV2 implements MIDUpgraderBase {

    @Override
    public MapID interpretJsonTree(MIDHeader formatProperties, JsonObject jsonDataIn) {
        if(jsonDataIn != null){
            JsonElement bodyElement = jsonDataIn.get("map");
            JsonElement extrasElement = jsonDataIn.get("extra"); // Extra data

            MapID.builder(formatProperties);

            if(bodyElement instanceof JsonObject){

            }
            if(extrasElement instanceof JsonObject){

            }
        }
        return null;
    }

}
