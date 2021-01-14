package net.cg360.nsapi.io.versions;

import com.google.gson.JsonElement;
import net.cg360.nsapi.MapID;
import net.cg360.nsapi.io.MIDHeader;

public class MIDUpgraderV2 implements MIDUpgraderBase {

    @Override
    public MapID interpretJsonTree(MIDHeader formatProperties, JsonElement jsonDataIn) {
        return null;
    }

}
