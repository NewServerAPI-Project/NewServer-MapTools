package net.cg360.nsapi.io;

public final class MIDHeader {

    private String identifier;
    private Integer formatVersion;
    private String mapStorageType;
    private Integer mapStorageVersion;


    protected MIDHeader(String identifier, Integer formatVersion, String mapStorageType, Integer mapStorageVersion) {
        if(identifier == null) throw new IllegalArgumentException("MapID is missing a map 'identifier' property.");
        if(formatVersion == null) throw new IllegalArgumentException("MapID is missing a 'format_version' property.");
        if(mapStorageType == null) throw new IllegalArgumentException("MapID is missing a 'map_storage_type' property.");
        if(mapStorageVersion == null) throw new IllegalArgumentException("MapID is missing a 'map_storage_version' property.");


        this.identifier = identifier.trim().toLowerCase();
        this.formatVersion = formatVersion;
        this.mapStorageType = mapStorageType.trim().toLowerCase();
        this.mapStorageVersion = mapStorageVersion;
    }

    public String getIdentifier() { return identifier; }
    public Integer getFormatVersion() { return formatVersion; }
    public String getMapStorageType() { return mapStorageType; }
    public Integer getMapStorageVersion() { return mapStorageVersion; }
}
