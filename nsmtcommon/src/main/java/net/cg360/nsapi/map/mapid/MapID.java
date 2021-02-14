package net.cg360.nsapi.map.mapid;

import com.google.gson.JsonObject;
import net.cg360.nsapi.commons.Immutable;
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
        // uMaps + Ulists are used to shorten making them immutable.

        this.header = header;
        this.displayName = displayName == null ? Utility.pickRandomString(MapIDConstants.MAPID_MISSING_NAMES) : displayName;
        this.description = description == null ? Utility.pickRandomString(MapIDConstants.MAPID_MISSING_DESCRIPTIONS) : description;;
        this.authors = Immutable.uList(authors == null ? new ArrayList<>(Collections.singletonList("None")) : authors, u); //Maybe use contributors: Seems like they would be a bad way to deal with it. Opinions?
        this.supportedGamemodes = Immutable.uList(supportedGamemodes == null ? new ArrayList<>() : supportedGamemodes, u);

        if(spawns == null){
            this.spawns = Immutable.uMap(new HashMap<>(), u);
        } else {
            HashMap<String, List<PosRot>> spawnlists = new HashMap<>();
            // For each list, make it unmodifiable if u = true
            for(Map.Entry<String, List<PosRot>> i : spawns.entrySet()) spawnlists.put(i.getKey(), Immutable.uList(i.getValue(), u));
            this.spawns = Immutable.uMap(spawnlists, u);
        }
        this.regions = Immutable.uMap(regions == null ? new HashMap<>() : regions, u);
        this.pointEntities = Immutable.uMap(pointEntities == null ? new HashMap<>() : pointEntities, u);
        this.strings = Immutable.uMap(strings == null ? new HashMap<>() : strings, u);
        this.numbers = Immutable.uMap(numbers == null ? new HashMap<>() : numbers, u);
        this.switches = Immutable.uMap(switches == null ? new HashMap<>() : switches, u);
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

            if((value != null) && (spawnlist != null)) {
                String spListID = spawnlist.toLowerCase().trim();

                if (this.spawns.get(spListID) == null) {
                    ArrayList<PosRot> newList = new ArrayList<>();
                    newList.add(value);
                    this.spawns.put(spListID, newList);

                } else {
                    this.spawns.get(spawnlist).add(value);
                }
            }
            return this;
        }

        public Builder addAuthor(String author){

            if(author != null) {
                authors.add(author);
            }
            return this;
        }

        public Builder addSupportedGamemode(String gamemode){

            if(gamemode != null) {
                supportedGamemodes.add(gamemode);
            }
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
            this.authors = authors == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(authors));
            return this;
        }

        public Builder setSupportedGamemodes(String[] supportedGamemodes) {
            this.supportedGamemodes = supportedGamemodes == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(supportedGamemodes));
            return this;
        }

        public Builder setAuthorsList(List<String> authors) {
            this.authors = authors == null ? new ArrayList<>() : new ArrayList<>(authors);
            return this;
        }

        public Builder setSupportedGamemodesList(List<String> supportedGamemodes) {
            this.supportedGamemodes = supportedGamemodes == null ? new ArrayList<>() : new ArrayList<>(supportedGamemodes);
            return this;
        }

        public Builder setSpawnslists(Map<String, List<PosRot>> spawns) {
            this.spawns = new HashMap<>();

            if(spawns != null) {

                for (Map.Entry<String, List<PosRot>> e : spawns.entrySet()) {
                    this.spawns.put(e.getKey().trim(), e.getValue());
                }
            }
            return this;
        }

        public Builder setMapRegions(Map<String, MapRegionDataStore> regions) {
            this.regions = new HashMap<>();

            if(regions != null) {

                for (Map.Entry<String, MapRegionDataStore> e : regions.entrySet()) {
                    this.regions.put(e.getKey().trim().toLowerCase(), e.getValue());
                }
            }
            return this;
        }

        public Builder setPointEntities(Map<String, PointEntityDataStore> pointEntities) {
            this.pointEntities = new HashMap<>();

            if(pointEntities != null) {

                for (Map.Entry<String, PointEntityDataStore> e : pointEntities.entrySet()) {
                    this.pointEntities.put(e.getKey().trim().toLowerCase(), e.getValue());
                }
            }
            return this;
        }

        public Builder setStrings(Map<String, String> strings) {
            this.strings = new HashMap<>();

            if(strings != null) {

                for (Map.Entry<String, String> e : strings.entrySet()) {
                    this.strings.put(e.getKey().trim().toLowerCase(), e.getValue());
                }
            }
            return this;
        }

        public Builder setNumbers(Map<String, Number> numbers) {
            this.numbers = new HashMap<>();

            if(numbers != null) {

                for (Map.Entry<String, Number> e : numbers.entrySet()) {
                    this.numbers.put(e.getKey().trim().toLowerCase(), e.getValue());
                }
            }
            return this;
        }

        public Builder setSwitches(Map<String, Boolean> switches) {
            this.switches = new HashMap<>();

            if(switches != null) {

                for (Map.Entry<String, Boolean> e : switches.entrySet()) {
                    this.switches.put(e.getKey().trim().toLowerCase(), e.getValue());
                }
            }
            return this;
        }

        public Builder setExtraData(JsonObject extraData) {
            this.extraData = extraData;
            return this;
        }

        public Builder setSpawnlist(String spawnlist, List<PosRot> value){

            if(spawnlist != null) {
                List<PosRot> posRots = value == null ? new ArrayList<>() : new ArrayList<>(value);
                this.spawns.put(spawnlist.trim(), posRots);
            }
            return this;
        }

        public Builder setMapRegion(String entry, MapRegionDataStore value){

            if(entry != null){

                if(value == null){
                    this.regions.remove(entry.trim().toLowerCase());

                } else {
                    this.regions.put(entry.trim().toLowerCase(), value);
                }
            }
            return this;
        }

        public Builder setPointEntity(String entry, PointEntityDataStore value){

            if(entry != null) {

                if (value == null) {
                    this.pointEntities.remove(entry.trim().toLowerCase());

                } else {
                    this.pointEntities.put(entry.trim().toLowerCase(), value);
                }
            }
            return this;
        }

        public Builder setString(String entry, String value){

            if(entry != null) {

                if (value == null) {
                    this.strings.remove(entry.trim().toLowerCase());

                } else {
                    this.strings.put(entry.trim().toLowerCase(), value);
                }
            }
            return this;
        }

        public Builder setNumber(String entry, Number value){

            if(entry != null) {

                if (value == null) {
                    this.numbers.remove(entry.trim().toLowerCase());

                } else {
                    this.numbers.put(entry.trim().toLowerCase(), value);
                }
            }
            return this;
        }

        public Builder setSwitch(String entry, Boolean value){

            if(entry != null) {

                if (value == null) {
                    this.switches.remove(entry.trim().toLowerCase());

                } else {
                    this.switches.put(entry.trim().toLowerCase(), value);
                }
            }
            return this;
        }
    }
}
