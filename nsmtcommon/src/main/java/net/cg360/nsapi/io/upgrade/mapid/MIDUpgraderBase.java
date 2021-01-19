package net.cg360.nsapi.io.upgrade.mapid;

import com.google.gson.JsonObject;
import net.cg360.nsapi.mapid.MapID;
import net.cg360.nsapi.mapid.MIDHeader;

/**
 * @author CG360;
 */
public interface MIDUpgraderBase {

    MapID interpretJsonTree(MIDHeader formatProperties, JsonObject jsonDataIn);

}
