package net.cg360.nsapi.io.versions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.cg360.nsapi.MapID;
import net.cg360.nsapi.io.MIDHeader;

/**
 * @author CG360;
 */
public interface MIDUpgraderBase {

    MapID interpretJsonTree(MIDHeader formatProperties, JsonObject jsonDataIn);

}
