# NewServer Map Tools
![Badge: Java](https://img.shields.io/badge/Java-8-red?style=for-the-badge)

![Dependency: Commons](https://img.shields.io/badge/Depend-NSAPI_Commons:_1.0-blue?style=for-the-badge)
![Dependency: Commons](https://img.shields.io/badge/Depend-JO--NBT%3A%201.3.1-yellow?style=for-the-badge)
![Dependency: Commons](https://img.shields.io/badge/Depend-Gson:_2.8.6-default?style=for-the-badge)

### Summary
This library, written in **Java 8**, which holds a collection of tools used to read and write certain world/map related formats such as *in the list below*. Furthermore, the library offers ways to interact with the formats from a context of a plugin or program, allowing for implementation of richer features using the extra data provided.

*Some formats provided were primarily developed for the NewGamesAPI 2 project.*

### Format support list

- [x] MapID 2 (json)
- [x] NewServer Cube32 (nbt)
- [ ] MC Schematic (nbt)
  - Supports all base features.
  - Partial MCEdit Unified support (Biomes)
   

---

## MapID Universal (Json)
*Also referred to as MapID 2*

![Badge: Version](https://img.shields.io/badge/Version-MapID2-orange?style=for-the-badge)

### Summary:
MapID is a json-based format used to provide extra metadata to servers for minigame maps (or any other purpose), offering simple properties such as **names**, **descriptions**, **authors**, as well as richer data such as **Point Entities**, **Map Regions** as well as other custom properties. It is designed to sit in the root directory of any map (However a world/map is chosen to be stored) and be read by the server.

This component of the library is designed to read these files from any version (From MapID 2 onwards) with backwards compatibility as well as writing them in the latest version of the format. Furthermore, it offers an interface for Minecraft plugins (Or even other games) to access the data stored within the format.

It otherwise does not offer systems that run games with the functionality that a Point Entity, or a Map Region could provide. That is up to the library implementor to provide dynamic functionality. This library is purely for reading and writing these files.


### History:
The original MapID project started back in March 2020 when Mooncraft Games had moved on to a minigame network while Skyblock progress was restricted. The underlying minigame architecture, which at the time was closed-source, was called NGAPI 1.0.

The MapID format, while still being developed, initially started with an id, name, description, authors, and properties as the only purpose was to offer simple metadata for minigame maps. Soon, however, the MapRegion was introduced with a limited set of functionality which NGAPI 1 was stuck with. A region could have multiple behavior "tags" (similar to PointEntity types) as well as defining the size and if it was active, however, there were no properties. It shortly got followed up with the PointEntity which is close to its current counterpart in MapID 2 as it had a more flexible design. This is why the modern MapRegion closely mimics the current PointEntity.

Furthermore, while MapID 1 was designed to be easily extendable in the future, it did not have any required properties that tracked the version, thus it will **not** be compatible with MapID 2 (If you can somehow obtain a MapID 1)


## Technical Details

### Format:
The MapID format is json-based to make editing by hand more approachable by people familiar with Minecraft's resource packs. Detailed documentation on all the formats from MapID2 onwards can be found on the wiki.

Latest: `//TODO: Insert link here when available` (MapID 2)

A few parts of the format don't change between versions (As of MapID 2.0) as they are used for interpreting the format across all versions. Specifically, they're used for the auto-upgrade system which allows older maps to work on newer formats. These are all the elements found in the 'header' (root) of the json file.

```
{
    "identifier": "ExampleMap",
    "format_version": 2,
    
    "map_storage_type": "nscube",
    "map_storage_version": 1,
    
    "case_sensitive_ids": true,
    
    "map": {
        ... This is the main body of the MapID ...
    },
    
    "extra_data": {
        ... Where custom stuff goes | Lib offers direct access to the JsonObject ...
    }    
}
```

### Changelog (MapID 2):
_(Compared to the internal 1.1 format and codebase used at Mooncraft Games)_

`[ A ]` = Addition;
`[ X ]` = Removal;
`[ C ]` = Change;

  - `[ A ]` New Builder for the MapID object.
  - `[ A ]` Format: Added permanent `map_storage_type` header property.
  - `[ A ]` Format: Added permanent `map_storage_version` header property.
  - `[ A ]` Format: Added permanent `case_sensitive_ids` header property. (Default: false)
  - `[ A ]` Format: Added standalone `extra_data` field.
  - `[ A ]` MapRegions can now be defined as "anonymous" (Array instead of map structure)
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

---

## NS Cube16 (NBT)

### Summary

Cube16 is a chunk-based world storage format with parallels to Minecraft Java's own chunk format. It stores "chunks" in 16x16x16 cubes with methods to reduce file size applied across the format. Any metadata for worlds stored in this format should be stored in a companion **MapID** file.

### Format:
*Using a JSON like structure for display purposes.*

```
{
    "format_type": "cube32",
    "format_version": 1,
    "chunk_type": 0,
    "encode_mode": 1,
    
    "position": [     // Chunk coords (16x normal coords, offset from center point.)
        1,
        -2,
        5
    ],
    
    "data": {
        "palette": [
            {
                "identifier": "minecraft:furnace",
                "properties": {
                    "lit": "true" // All properties are strings.
                }
            },
            {
                "identifier": "minecraft:grass"
            }
        ]
        "runlength": 4  //Amount of bits to use for length in runlength. Longer runs need more bits.
        "blocks": [ bytearrayhere ],
        
        "tileentities": [
            {tile entity nbt -> see minecraft chunk format} // I'll write this in later
        ]
    }
}
```

Some notes about the format:
 - `format_type` should not change. It's only there to mark "valid" files so the library can properly scream about errors.
 - `chunk_type` offers shortcuts based on a chunk's state.
   - `0` = ***Default:*** Use the default file structure.
   - `1` = ***Empty:*** There's no block data. Chunk is just air.
   - `2` = ***Full:*** Chunk is just one block thus an array is useless. Set the chunk's block
 - `encode_mode` can be different per-chunk. The types are:
   - 0: No added compression other than the use of the palette.
   - 1: run-length encoding is applied. (Group together runs of blocks)