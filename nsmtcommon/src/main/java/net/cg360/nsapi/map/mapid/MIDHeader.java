package net.cg360.nsapi.map.mapid;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.cg360.nsapi.commons.Utility;
import net.cg360.nsapi.commons.exception.MissingPropertyException;

import java.util.Optional;

/**
 * @author CG360;
 */
public final class MIDHeader {

    private String mapPath;
    private String identifier;
    private Integer formatVersion;
    private String mapStorageType;
    private String mapStorageVersion;
    private Boolean hasCaseSensitiveIDs;

    // Code-Generated headers have case-sensitivity forced on.
    // I don't think it's worth checking every ID in every builder, thus it's easier just to assume it's case-sensitive.
    public MIDHeader(String mapPath, String identifier, Integer formatVersion, String mapStorageType, String mapStorageVersion) {
        this(mapPath, identifier, formatVersion, mapStorageType, mapStorageVersion, true);
    }

    protected MIDHeader(String mapPath, String identifier, Integer formatVersion, String mapStorageType, String mapStorageVersion, Boolean hasCaseSensitiveIDs) {
        if(formatVersion == null) throw new IllegalArgumentException("MapID is missing a 'format_version' property.");

        this.mapPath = mapPath;
        this.identifier = identifier == null ? "generated-"+ Utility.generateUniqueToken(5, 3).toLowerCase() : identifier.trim().toLowerCase();
        this.formatVersion = formatVersion;
        this.mapStorageType = mapStorageType == null ? "null" : mapStorageType.trim().toLowerCase();
        this.mapStorageVersion = mapStorageVersion == null ? "null" : mapStorageVersion.trim().toLowerCase();
        this.hasCaseSensitiveIDs = hasCaseSensitiveIDs != null && hasCaseSensitiveIDs; // Header is forced false, default is false.
    }

    public static MIDHeader getHeaderFromJson(JsonObject root) { return getHeaderFromJson(root, null); }
    public static MIDHeader getHeaderFromJson(JsonObject root, String mapPath) {
        Integer formatV = null;
        String id = null, mapStorage = null, mapStorageV = null;
        Boolean caseSensitiveIDs = null;

        JsonElement formatElement = root.get("format_version");
        if(formatElement instanceof JsonPrimitive){
            JsonPrimitive p = (JsonPrimitive) formatElement;
            if (p.isNumber()) formatV = p.getAsNumber().intValue();
        }
        if(formatV == null) throw new MissingPropertyException("MapID is missing a 'format_version' property.");

        JsonElement idElement = root.get("identifier");
        if(idElement instanceof JsonPrimitive){
            JsonPrimitive p = (JsonPrimitive) idElement;
            id = p.getAsString();
        }

        JsonElement mapStoreElement = root.get("map_storage_type");
        if(mapStoreElement instanceof JsonPrimitive){
            JsonPrimitive p = (JsonPrimitive) mapStoreElement;
            mapStorage = p.getAsString();
        }

        JsonElement mapStoreVElement = root.get("map_storage_version");
        if(mapStoreVElement instanceof JsonPrimitive){
            JsonPrimitive p = (JsonPrimitive) mapStoreVElement;
            mapStorageV = p.getAsString();
        }

        JsonElement caseSensitiveIDElement = root.get("case_sensitive_ids");
        if(caseSensitiveIDElement instanceof JsonPrimitive){
            JsonPrimitive p = (JsonPrimitive) caseSensitiveIDElement;
            if(p.isBoolean() || p.isString()) caseSensitiveIDs = p.getAsBoolean();
        }

        return new MIDHeader(mapPath, id, formatV, mapStorage, mapStorageV, caseSensitiveIDs);
    }

    public Optional<String> getMapPath() { return Optional.ofNullable(mapPath); }
    public String getIdentifier() { return identifier; }
    public Integer getFormatVersion() { return formatVersion; }
    public String getMapStorageType() { return mapStorageType; }
    public String getMapStorageVersion() { return mapStorageVersion; }
    public Boolean hasCaseSensitiveIDs() { return hasCaseSensitiveIDs; }

}
