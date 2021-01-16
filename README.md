
### Format:

The MapID format is json-based to make editing by hand more approachable by people familiar with Minecraft's resource packs. Detailed documentation on all the formats from MapID2 onwards can be found on the wiki.

Latest: `//Insert link here when available` (MapID 2)

A few parts of the format don't change between versions (As of MapID 2.0) as they are used for interpreting the format across all versions. Specifically, they're used for the auto-upgrade system which allows older maps to work on newer formats. These are all the elements found in the 'header' (root) of the json file.

```
{
    "identifier": "ExampleMap",
    "format_version": 2,
    
    "map_storage_type": "schem",
    "map_storage_version": 1,
    
    "map": {
        ... This is the main body of the MapID ...
    }
}
```

### Changelog (MapID 2):
_(Compared to the internal 1.1 format and codebase used at Mooncraft Games)_

`[ A ]` = Addition;
`[ X ]` = Removal;
`[ C ]` = Change;

  - `[ A ]` New Builder for the MapID object.
  - `[ A ]` Format: Added permanent `map_storage_type` property.
  - `[ A ]` Format: Added permanent `map_storage_version` property.
  - `[ C ]` Any mention of 'Level' is now changed to 'Map' (As it was artifact of the bedrock roots).
  - `[ C ]` Moved RotatablePosition (now PosRot) to Commons Project.
  - `[ C ]` Moved PointEntity (Stored data) to Commons Project.
  - `[ C ]` Moved MapRegion (Stored data) to Commons Project.
  - `[ C ]` PosRot now centers the Y value (Previously only X + Z) if shouldOffsetCenter is true.
  - `[ C ]` PointEntities and MapRegions are now more forgiving for missing properties.
  - `[ C ]` Merged internal `integers` and `floats` lists into `numbers`.
  - `[ C ]` Format: Any separated `strings`, `integers`, `floats`, and `switches` are now found in one `properties` section.
  - `[ C ]` Format: MapRegions are now mono-typed - They are structured like the old PointEntity.
  - `[ C ]` Format: `id` -> `identifier`.
  - `[ C ]` Format: `mapid_version` -> `format_version`.
  - `[ X ]` Format: Removed the `death_messages` property.