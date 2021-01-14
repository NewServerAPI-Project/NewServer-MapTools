package net.cg360.nsapi.io.versions;

import com.google.gson.JsonElement;
import net.cg360.nsapi.MapID;
import net.cg360.nsapi.io.MIDHeader;

public interface MIDUpgraderBase {

    MapID interpretJsonTree(MIDHeader formatProperties, JsonElement jsonDataIn);

}
