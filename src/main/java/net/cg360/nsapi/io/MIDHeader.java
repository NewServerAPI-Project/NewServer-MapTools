package net.cg360.nsapi.io;

import net.cg360.nsapi.commons.Utility;

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


    protected MIDHeader(String mapPath, String identifier, Integer formatVersion, String mapStorageType, String mapStorageVersion) {
        if(formatVersion == null) throw new IllegalArgumentException("MapID is missing a 'format_version' property.");

        this.mapPath = mapPath;
        this.identifier = identifier == null ? "generated-"+ Utility.generateUniqueToken(5, 3).toLowerCase() : identifier.trim().toLowerCase();
        this.formatVersion = formatVersion;
        this.mapStorageType = mapStorageType == null ? "null" : mapStorageType.trim().toLowerCase();
        this.mapStorageVersion = mapStorageVersion == null ? "null" : mapStorageVersion.trim().toLowerCase();
    }

    public Optional<String> getMapPath() { return Optional.ofNullable(mapPath); }
    public String getIdentifier() { return identifier; }
    public Integer getFormatVersion() { return formatVersion; }
    public String getMapStorageType() { return mapStorageType; }
    public String getMapStorageVersion() { return mapStorageVersion; }
}
