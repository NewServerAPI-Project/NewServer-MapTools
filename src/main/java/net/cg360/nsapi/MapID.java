package net.cg360.nsapi;

import com.google.gson.JsonObject;
import net.cg360.nsapi.commons.Utility;
import net.cg360.nsapi.commons.data.MapRegionDataStore;
import net.cg360.nsapi.commons.data.PointEntityDataStore;
import net.cg360.nsapi.commons.math.PosRot;
import net.cg360.nsapi.io.MIDHeader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CG360;
 */
public abstract class MapID {

    protected MIDHeader header;

    protected String displayName;
    protected String description;
    protected String[] authors;
    protected String[] supportedGamemodes;

    protected Map<String, PosRot[]> spawns;
    protected Map<String, MapRegionDataStore> regions;
    protected Map<String, PointEntityDataStore> pointEntities;
    protected Map<String, String> strings;
    protected Map<String, Number> numbers;
    protected Map<String, Boolean> switches;


    public MapID(MIDHeader header, String displayName, String description, String[] authors, String[] supportedGamemodes, Map<String, PosRot[]> spawns, Map<String, MapRegionDataStore> regions, Map<String, PointEntityDataStore> pointEntities, Map<String, String> strings, Map<String, Number> numbers, Map<String, Boolean> switches) {
        if(header == null) throw new IllegalArgumentException("MapID is somehow missing a header. This is a plugin bug, please report with a list of plugins."); //Whoever triggers this will make me screeeech.

        this.header = header;
        this.displayName = displayName == null ? Utility.pickRandomString(MapIDConstants.MAPID_MISSING_NAMES) : displayName;
        this.description = description == null ? Utility.pickRandomString(MapIDConstants.MAPID_MISSING_DESCRIPTIONS) : description;;
        this.authors = authors == null ? new String[]{"Unknown"} : authors; //Maybe use contributors: Seems like they would be a bad way to deal with it. Opinions?
        this.supportedGamemodes = supportedGamemodes == null ? new String[0] : supportedGamemodes;
        this.spawns = spawns == null ? Collections.unmodifiableMap(new HashMap<>()) : spawns;
        this.regions = regions == null ? Collections.unmodifiableMap(new HashMap<>()) : regions;
        this.pointEntities = pointEntities == null ? Collections.unmodifiableMap(new HashMap<>()) : pointEntities;
        this.strings = strings == null ? Collections.unmodifiableMap(new HashMap<>()) : strings;
        this.numbers = numbers == null ? Collections.unmodifiableMap(new HashMap<>()) : numbers;
        this.switches = switches == null ? Collections.unmodifiableMap(new HashMap<>()) : switches;
    }

    public MIDHeader getHeader() { return header; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public String[] getAuthors() { return authors; }
    public String[] getSupportedGamemodes() { return supportedGamemodes; }
    public Map<String, PosRot[]> getSpawns() { return spawns; }
    public Map<String, MapRegionDataStore> getRegions() { return regions; }
    public Map<String, PointEntityDataStore> getPointEntities() { return pointEntities; }
    public Map<String, String> getStrings() { return strings; }
    public Map<String, Number> getNumbers() { return numbers; }
    public Map<String, Boolean> getSwitches() { return switches; }

    public static Builder builder(MIDHeader header){ return new Builder(header); }



    public static class AssembledMapID extends MapID {

        public AssembledMapID(MIDHeader header, String displayName, String description, String[] authors, String[] supportedGamemodes, Map<String, PosRot[]> spawns, Map<String, MapRegionDataStore> regions, Map<String, PointEntityDataStore> pointEntities, Map<String, String> strings, Map<String, Number> numbers, Map<String, Boolean> switches) {
            super(header, displayName, description, authors, supportedGamemodes, spawns, regions, pointEntities, strings, numbers, switches);
        }

    }



    public static class Builder extends MapID {

        public Builder(MIDHeader header) {
            super(header, null, null, null, null, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
        }


        public MapID build() {
            Map<String, PosRot[]> spawns = Collections.unmodifiableMap(this.spawns);
            Map<String, MapRegionDataStore> regions = Collections.unmodifiableMap(this.regions);
            Map<String, PointEntityDataStore> pointEntities = Collections.unmodifiableMap(this.pointEntities);
            Map<String, String> strings = Collections.unmodifiableMap(this.strings);
            Map<String, Number> numbers = Collections.unmodifiableMap(this.numbers);
            Map<String, Boolean> switches = Collections.unmodifiableMap(this.switches);

            return new AssembledMapID(this.header, this.displayName, this.description, this.authors, this.supportedGamemodes, spawns, regions, pointEntities, strings, numbers, switches);
        }


        public MapID setHeader(MIDHeader header) {
            if(header == null) throw new IllegalArgumentException("Within builder, header cannot be null");
            this.header = header;
            return this;
        }

        public MapID setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public MapID setDescription(String description) {
            this.description = description;
            return this;
        }

        public MapID setAuthors(String[] authors) {
            this.authors = authors;
            return this;
        }

        public MapID setSupportedGamemodes(String[] supportedGamemodes) {
            this.supportedGamemodes = supportedGamemodes;
            return this;
        }

        public Builder setSpawnslists(Map<String, PosRot[]> spawns) {
            this.spawns = new HashMap<>();

            for(Map.Entry<String, PosRot[]> e: spawns.entrySet()){
                this.spawns.put(e.getKey().trim().toLowerCase(), e.getValue());
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

        public Builder setSpawnlist(String spawnlist, PosRot[] value){
            this.spawns.put(spawnlist.trim().toLowerCase(), value);
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

        public Builder addSpawn(String spawnlist, PosRot value){
            String spListID = spawnlist.toLowerCase().trim();
            PosRot[] spList;
            if(this.spawns.get(spListID) == null){
                spList = new PosRot[]{value};
            } else {
                PosRot[] oldList = this.spawns.get(spawnlist);
                int oldLength = oldList.length;
                spList = new PosRot[oldLength + 1];

                for(int i = 0; i < oldLength; i++){
                    spList[i] = oldList[i];
                }
                spList[oldLength] = value; //Append onto the end after extending the list.
            }
            this.spawns.put(spListID, spList);
            return this;
        }

    }
}
