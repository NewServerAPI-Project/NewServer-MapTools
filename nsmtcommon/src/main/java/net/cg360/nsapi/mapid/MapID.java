package net.cg360.nsapi.mapid;

import com.google.gson.JsonObject;
import net.cg360.nsapi.commons.Utility;
import net.cg360.nsapi.commons.data.MapRegionDataStore;
import net.cg360.nsapi.commons.data.PointEntityDataStore;
import net.cg360.nsapi.commons.math.PosRot;

import java.util.*;

/**
 * @author CG360;
 */
public abstract class MapID {

    protected MIDHeader header;

    protected String displayName;
    protected String description;
    protected List<String> authors;
    protected List<String> supportedGamemodes;

    protected Map<String, List<PosRot>> spawns;
    protected Map<String, MapRegionDataStore> regions;
    protected Map<String, PointEntityDataStore> pointEntities;
    protected Map<String, String> strings;
    protected Map<String, Number> numbers;
    protected Map<String, Boolean> switches;

    protected JsonObject extraData;

    public MapID(MIDHeader header, String displayName, String description, List<String> authors, List<String> supportedGamemodes, Map<String, List<PosRot>> spawns, Map<String, MapRegionDataStore> regions, Map<String, PointEntityDataStore> pointEntities, Map<String, String> strings, Map<String, Number> numbers, Map<String, Boolean> switches, JsonObject extraData) {
        this(header, true, displayName, description, authors, supportedGamemodes, spawns, regions, pointEntities, strings, numbers, switches, extraData);
        // Public facing method is always unmodifiable.
    }

    /**
     * Same as public constructor with an extra parameter for use in the constructor.
     * @param u is the MapID unmodifiable? Used for constructor.
     */
    protected MapID(MIDHeader header, boolean u, String displayName, String description, List<String> authors, List<String> supportedGamemodes, Map<String, List<PosRot>> spawns, Map<String, MapRegionDataStore> regions, Map<String, PointEntityDataStore> pointEntities, Map<String, String> strings, Map<String, Number> numbers, Map<String, Boolean> switches, JsonObject extraData) {
        if(header == null) throw new IllegalArgumentException("MapID is somehow missing a header. This is a plugin bug, please report with a list of plugins."); //Whoever triggers this will make me screeeech.

        // NOTICE: All maps + lists in constructor should be encapsulated in umap or ulist.

        this.header = header;
        this.displayName = displayName == null ? Utility.pickRandomString(MapIDConstants.MAPID_MISSING_NAMES) : displayName;
        this.description = description == null ? Utility.pickRandomString(MapIDConstants.MAPID_MISSING_DESCRIPTIONS) : description;;
        this.authors = uList(authors == null ? new ArrayList<>(Collections.singletonList("None")) : authors, u); //Maybe use contributors: Seems like they would be a bad way to deal with it. Opinions?
        this.supportedGamemodes = uList(supportedGamemodes == null ? new ArrayList<>() : supportedGamemodes, u);

        if(spawns == null){
            this.spawns = uMap(new HashMap<>(), u);
        } else {
            HashMap<String, List<PosRot>> spawnlists = new HashMap<>();
            // For each list, make it unmodifiable if u = true
            for(Map.Entry<String, List<PosRot>> i : spawns.entrySet()) spawnlists.put(i.getKey(), uList(i.getValue(), u));
            this.spawns = uMap(spawnlists, u);
        }
        this.regions = uMap(regions == null ? new HashMap<>() : regions, u);
        this.pointEntities = uMap(pointEntities == null ? new HashMap<>() : pointEntities, u);
        this.strings = uMap(strings == null ? new HashMap<>() : strings, u);
        this.numbers = uMap(numbers == null ? new HashMap<>() : numbers, u);
        this.switches = uMap(switches == null ? new HashMap<>() : switches, u);
        this.extraData = extraData == null ? new JsonObject() : extraData.deepCopy(); //Create empty object if not present.
    }

    public MIDHeader getHeader() { return header; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public List<String> getAuthors() { return authors; }
    public List<String> getSupportedGamemodes() { return supportedGamemodes; }
    public Map<String, List<PosRot>> getSpawns() { return spawns; }
    public Map<String, MapRegionDataStore> getRegions() { return regions; }
    public Map<String, PointEntityDataStore> getPointEntities() { return pointEntities; }
    public Map<String, String> getStrings() { return strings; }
    public Map<String, Number> getNumbers() { return numbers; }
    public Map<String, Boolean> getSwitches() { return switches; }
    public JsonObject getExtraData() { return extraData.deepCopy(); } //This is probably inefficient but I don't have any other solutions.

    public static Builder builder(MIDHeader header){ return new Builder(header); }
    public static <K, V> Map<K, V> uMap(Map<K, V> obj, boolean isUnmodifiable){
        return isUnmodifiable ? Collections.unmodifiableMap(obj) : obj;
        // Used in constructor. Shortened way of checking if the mapid is unmodifiable, setting the maps to the appropriate type.
    }
    public static <V> List<V> uList(List<V> obj, boolean isUnmodifiable){
        return isUnmodifiable ? Collections.unmodifiableList(obj) : obj;
        // Used in constructor. Shortened way of checking if the mapid is unmodifiable, setting the lists to the appropriate type.
    }


    protected static class AssembledMapID extends MapID {

        public AssembledMapID(MIDHeader header, String displayName, String description, List<String> authors, List<String> supportedGamemodes, Map<String, List<PosRot>> spawns, Map<String, MapRegionDataStore> regions, Map<String, PointEntityDataStore> pointEntities, Map<String, String> strings, Map<String, Number> numbers, Map<String, Boolean> switches, JsonObject extraData) {
            super(header, true, displayName, description, authors, supportedGamemodes, spawns, regions, pointEntities, strings, numbers, switches, extraData);
        }

    }



    public static class Builder extends MapID {

        public Builder(MIDHeader header) {
            super(header, false,null, null, new ArrayList<>(), new ArrayList<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new JsonObject());
        }

        public MapID build() {
            return new AssembledMapID(this.header, this.displayName, this.description, this.authors, this.supportedGamemodes, this.spawns, this.regions, this.pointEntities, this.strings, this.numbers, this.switches, this.extraData);
        }

        public Builder addSpawn(String spawnlist, PosRot value){
            String spListID = spawnlist.toLowerCase().trim();

            if(this.spawns.get(spListID) == null){
                ArrayList<PosRot> newList = new ArrayList<>();
                newList.add(value);
                this.spawns.put(spListID, newList);

            } else {
                this.spawns.get(spawnlist).add(value);
            }
            return this;
        }

        public Builder addAuthor(String author){
            authors.add(author);
            return this;
        }

        public Builder addSupportedGamemode(String gamemode){
            supportedGamemodes.add(gamemode);
            return this;
        }

        public Builder setHeader(MIDHeader header) {
            if(header == null) throw new IllegalArgumentException("Within builder, header cannot be null");
            this.header = header;
            return this;
        }

        public Builder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setAuthors(String[] authors) {
            this.authors = new ArrayList<>(Arrays.asList(authors));
            return this;
        }

        public Builder setSupportedGamemodes(String[] supportedGamemodes) {
            this.supportedGamemodes = new ArrayList<>(Arrays.asList(supportedGamemodes));
            return this;
        }

        public Builder setAuthorsList(List<String> authors) {
            this.authors = new ArrayList<>(authors);
            return this;
        }

        public Builder setSupportedGamemodesList(List<String> supportedGamemodes) {
            this.supportedGamemodes = new ArrayList<>(supportedGamemodes);
            return this;
        }

        public Builder setSpawnslists(Map<String, List<PosRot>> spawns) {
            this.spawns = new HashMap<>();

            for(Map.Entry<String, List<PosRot>> e: spawns.entrySet()){
                this.spawns.put(e.getKey().trim(), e.getValue());
            }
            return this;
        }

        public Builder setMapRegions(Map<String, MapRegionDataStore> regions) {
            this.regions = new HashMap<>();

            for(Map.Entry<String, MapRegionDataStore> e: regions.entrySet()){
                this.regions.put(e.getKey().trim().toLowerCase(), e.getValue());
            }
            return this;
        }

        public Builder setPointEntities(Map<String, PointEntityDataStore> pointEntities) {
            this.pointEntities = new HashMap<>();

            for(Map.Entry<String, PointEntityDataStore> e: pointEntities.entrySet()){
                this.pointEntities.put(e.getKey().trim().toLowerCase(), e.getValue());
            }
            return this;
        }

        public Builder setStrings(Map<String, String> strings) {
            this.strings = new HashMap<>();

            for(Map.Entry<String, String> e: strings.entrySet()){
                this.strings.put(e.getKey().trim().toLowerCase(), e.getValue());
            }
            return this;
        }

        public Builder setNumbers(Map<String, Number> floats) {
            this.numbers = new HashMap<>();

            for(Map.Entry<String, Number> e: floats.entrySet()){
                this.numbers.put(e.getKey().trim().toLowerCase(), e.getValue());
            }
            return this;
        }

        public Builder setSwitches(Map<String, Boolean> switches) {
            this.switches = new HashMap<>();

            for(Map.Entry<String, Boolean> e: switches.entrySet()){
                this.switches.put(e.getKey().trim().toLowerCase(), e.getValue());
            }
            return this;
        }

        public Builder setExtraData(JsonObject extraData) {
            this.extraData = extraData;
            return this;
        }

        public Builder setSpawnlist(String spawnlist, List<PosRot> value){
            this.spawns.put(spawnlist.trim(), value);
            return this;
        }

        public Builder setMapRegion(String entry, MapRegionDataStore value){
            this.regions.put(entry.trim().toLowerCase(), value);
            return this;
        }

        public Builder setPointEntity(String entry, PointEntityDataStore value){
            this.pointEntities.put(entry.trim().toLowerCase(), value);
            return this;
        }

        public Builder setString(String entry, String value){
            this.strings.put(entry.trim().toLowerCase(), value);
            return this;
        }

        public Builder setNumber(String entry, Number value){
            this.numbers.put(entry.trim().toLowerCase(), value);
            return this;
        }

        public Builder setSwitch(String entry, Boolean value){
            this.switches.put(entry.trim().toLowerCase(), value);
            return this;
        }
    }
}
