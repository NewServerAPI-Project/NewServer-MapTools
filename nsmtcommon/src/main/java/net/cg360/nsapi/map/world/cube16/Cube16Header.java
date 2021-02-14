package net.cg360.nsapi.map.world.cube16;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.cg360.nsapi.commons.Check;
import net.cg360.nsapi.commons.exception.MissingPropertyException;

import java.util.Optional;

// Consider 16. 32 may have issues with larger worlds + chunk loading.
public class Cube16Header {

    public static final String FORMAT_TYPE = "cube16";

    protected Integer formatVersion;

    protected Cube16ChunkType chunkType;
    protected Cube16Encode encodeMode;

    protected Integer posX;
    protected Integer posY;
    protected Integer posZ;

    // Maybe add a reference to a Cube16Level ?


    public Cube16Header(Integer formatVersion, Cube16ChunkType chunkType, Cube16Encode encodeMode, Integer posX, Integer posY, Integer posZ) {
        Check.nullParam(formatVersion, "Cube16 Chunk", "formatVersion");
        Check.nullParam(posX, "Cube16 Chunk", "posX");
        Check.nullParam(posY, "Cube16 Chunk", "posY");
        Check.nullParam(posZ, "Cube16 Chunk", "posZ");
        this.formatVersion = formatVersion;

        this.chunkType = chunkType == null ? Cube16ChunkType.DEFAULT : chunkType; // Kinda bad if this is missing. o.o
        this.encodeMode = encodeMode == null ? Cube16Encode.DEFAULT : encodeMode; // Should be fine but also bad.
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public static Optional<Cube16Header> getHeaderFromJson(JsonObject root) {
        Integer formatV = null;
        Cube16Encode encode = null;
        Cube16ChunkType chunkT = null;
        Integer pX = null, pY = null, pZ = null;

        JsonElement formatType = root.get("format_type");
        JsonElement formatElement = root.get("format_version");

        if(formatType instanceof JsonPrimitive) {
            JsonPrimitive formatPrim = (JsonPrimitive) formatType;

            if(formatPrim.getAsString().equalsIgnoreCase(FORMAT_TYPE)) {

                if (formatElement instanceof JsonPrimitive) {
                    JsonPrimitive p = (JsonPrimitive) formatElement;
                    if (p.isNumber()) formatV = p.getAsNumber().intValue();
                }
                if (formatV == null) throw new MissingPropertyException("Cube16 Chunk is missing a 'format_version' property.");


                return Optional.of(new Cube16Header(formatV, chunkT, encode, pX, pY, pZ));
            }
        }
        return Optional.empty();
    }

    public Integer getFormatVersion() { return formatVersion; }
    public Cube16ChunkType getChunkType() { return chunkType; }
    public Cube16Encode getEncodeMode() { return encodeMode; }
    public Integer getChunkX() { return posX; }
    public Integer getChunkY() { return posY; }
    public Integer getChunkZ() { return posZ; }
}
