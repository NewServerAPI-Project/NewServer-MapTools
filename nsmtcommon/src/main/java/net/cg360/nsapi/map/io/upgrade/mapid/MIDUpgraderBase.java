package net.cg360.nsapi.map.io.upgrade.mapid;

import com.google.gson.JsonObject;
import net.cg360.nsapi.map.mapid.MapID;
import net.cg360.nsapi.map.mapid.MIDHeader;

/**
 * @author CG360;
 */
public interface MIDUpgraderBase {

    MapID interpretJsonTree(MIDHeader formatProperties, JsonObject jsonDataIn);

}
