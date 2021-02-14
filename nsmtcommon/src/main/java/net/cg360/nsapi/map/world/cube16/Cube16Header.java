package net.cg360.nsapi.map.world.cube16;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.cg360.nsapi.commons.Check;
import net.cg360.nsapi.commons.exception.MissingPropertyException;

import java.util.Optional;

// Could consider a 32x format. Would use empty/full chunk modes less but run-length
// encoding would probably get a boost.
public class Cube16Header {

    public static final String FORMAT_TYPE = "cube16";

    protected Integer formatVersion;

    protected Cube16ChunkType chunkType;

    protected Integer posX;
    protected Integer posY;
    protected Integer posZ;

    // Maybe add a reference to a Cube16Level ?


    public Cube16Header(Integer formatVersion, Cube16ChunkType chunkType, Integer posX, Integer posY, Integer posZ) {
        Check.nullParam(formatVersion, "formatVersion");
        Check.nullParam(posX, "posX");
        Check.nullParam(posY, "posY");
        Check.nullParam(posZ, "posZ");
        this.formatVersion = formatVersion;

        this.chunkType = chunkType == null ? Cube16ChunkType.DEFAULT : chunkType; // Kinda bad if this is missing. o.o
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public static Optional<Cube16Header> getHeaderFromJson(JsonObject root) {
        Integer formatV = null;
        Cube16ChunkType chunkT = null;
        Integer pX = null, pY = null, pZ = null;

        JsonElement formatType = root.get("format_type");
        JsonElement formatElement = root.get("format_version");

        JsonElement chunkTypeElement = root.get("chunk_type");

        JsonElement positionElement = root.get("position");

        if(formatType instanceof JsonPrimitive) {
            JsonPrimitive formatPrim = (JsonPrimitive) formatType;

            if(formatPrim.getAsString().equalsIgnoreCase(FORMAT_TYPE)) { // If this is false, it's probably not a Cube16 chunk.

                if (formatElement instanceof JsonPrimitive) {
                    JsonPrimitive p = (JsonPrimitive) formatElement;
                    if (p.isNumber()) formatV = p.getAsNumber().intValue();
                }
                Check.missingProperty(formatV, "Cube16 chunk", "format_version");

                if (chunkTypeElement instanceof JsonPrimitive) {
                    JsonPrimitive p = (JsonPrimitive) chunkTypeElement;
                    if (p.isNumber()) chunkT = Cube16ChunkType.getFromID(p.getAsNumber().intValue());
                }

                if (positionElement instanceof JsonArray) {
                    JsonArray posArray = (JsonArray) positionElement;

                    if(posArray.size() >= 3) {
                        JsonElement xElement = posArray.get(0);
                        JsonElement yElement = posArray.get(1);
                        JsonElement zElement = posArray.get(2);

                        if (xElement instanceof JsonPrimitive) {
                            JsonPrimitive p = (JsonPrimitive) xElement;
                            if (p.isNumber()) pX = p.getAsNumber().intValue();
                        }

                        if (yElement instanceof JsonPrimitive) {
                            JsonPrimitive p = (JsonPrimitive) yElement;
                            if (p.isNumber()) pY = p.getAsNumber().intValue();
                        }

                        if (zElement instanceof JsonPrimitive) {
                            JsonPrimitive p = (JsonPrimitive) zElement;
                            if (p.isNumber()) pZ = p.getAsNumber().intValue();
                        }
                    }
                    Check.missingProperty(pX, "Cube16 Chunk", "position (x)");
                    Check.missingProperty(pY, "Cube16 Chunk", "position (y)");
                    Check.missingProperty(pZ, "Cube16 Chunk", "position (z)");
                }

                return Optional.of(new Cube16Header(formatV, chunkT, pX, pY, pZ));
            }
        }
        return Optional.empty();
    }

    public Integer getFormatVersion() { return formatVersion; }
    public Cube16ChunkType getChunkType() { return chunkType; }
    public Integer getChunkX() { return posX; }
    public Integer getChunkY() { return posY; }
    public Integer getChunkZ() { return posZ; }
}
