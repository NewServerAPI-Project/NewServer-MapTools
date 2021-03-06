package net.cg360.nsapi.map.world.cube16;

import net.cg360.nsapi.map.io.upgrade.world.Block;
import net.cg360.nsapi.map.world.cube16.Cube16BlockBuffer;
import net.cg360.nsapi.map.world.cube16.enums.Cube16Encode;
import se.llbit.nbt.CompoundTag;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * The API-side representation of a Cube16 chunk.
 * It combines the block chunk and the entity chunk files into
 * one object to access reasons.
 *
 */
public abstract class Cube16Chunk {

    public static final short CHUNK_SIZE = 16; // This'll make scaling to 32x easier. May need to replace some datatypes.
    public static final short BIOME_SCALE = 4; // The amount of blocks biome data covers. Vanilla is 4x4x4 as of 1.15
    public static final byte[] SUPPORTED_PALETTE_SCALES = { 1, 2, 4, 8, 16 };

    //TODO: Constructor + Builder.
    //TODO: On addblock, if the palette size is increased, ensure the bit depth is correct. If bit depth is updated, recalculate
    //      the positions of each block + update all the block buffer positions (+ bit positions).

    // Blocks: Serialization
    protected Cube16Encode encodeMode;
    protected List<Block> palette;
    protected List<CompoundTag> tileEntities;
    protected ByteBuffer blockData; // length = 16x16x16x(depth/8)
    protected byte paletteBitDepth; // Should recalculate blockData every time this changes.

    protected List<CompoundTag> chunkEntityData; // Only stores the last saved/loaded state the the chunks entities.

    // -- Access --
    protected Cube16BlockBuffer primaryBlockBuffer;
    protected List<Cube16BlockBuffer> blockBuffers;
    protected boolean isImmutable;


    /**
     * Get a block from the blockData. Less efficient than direct
     * access as it required the buffer position to be set.
     * @param x x coordinate of the block.
     * @param y y coordinate of the block.
     * @param z z coordinate of the block.
     * @return the block (ID + Blockstate)
     */
    public Block getBlock(byte x, byte y, byte z) {
        //TODO: Add int method with checks + casts;
        getPrimaryBlockBuffer().prepareBlockBufferPosition(x, y, z);
        return getPrimaryBlockBuffer().getNextBlock();
    }

    /** @return The shared main block buffer for accessing blockData.*/
    public Cube16BlockBuffer getPrimaryBlockBuffer() {
        if(primaryBlockBuffer == null) primaryBlockBuffer = getBlockBuffer();
        return primaryBlockBuffer;
    }

    /** @return A new block buffer for accessing blockData.*/
    public Cube16BlockBuffer getBlockBuffer() {
        Cube16BlockBuffer buffer = new Cube16BlockBuffer(this, blockData);
        blockBuffers.add(buffer);
        return buffer;
    }



    public Cube16Encode getEncodeMode() { return encodeMode; }
    public List<Block> getPalette() { return palette; }
    public byte getPaletteBitDepth() { return paletteBitDepth; }

    public List<CompoundTag> getTileEntities() { return tileEntities; }
    public List<CompoundTag> getChunkEntityData() { return chunkEntityData; }


    public void setImmutable() { isImmutable = true; } // If something is immutable, why should it be able to be made mutable?
}
