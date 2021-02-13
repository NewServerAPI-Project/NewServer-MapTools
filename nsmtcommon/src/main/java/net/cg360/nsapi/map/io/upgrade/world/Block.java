package net.cg360.nsapi.map.io.upgrade.world;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Block {

    private String identifier;
    private Map<String, String> properties;

    public Block(String identifier){ this(identifier, null); }
    public Block(String identifier, HashMap<String, String> properties){
        this.identifier = identifier;
        this.properties = Collections.unmodifiableMap(properties == null ? new HashMap<>() : properties);
    }

    public String getIdentifier() { return identifier; }
    public Map<String, String> getBlockState() { return properties; }
}
