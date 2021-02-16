package net.cg360.nsapi.map.world.cube16;

import net.cg360.nsapi.commons.math.NSMath;
import net.cg360.nsapi.map.io.upgrade.world.Block;
import net.cg360.nsapi.map.world.cube16.util.Cube16Utility;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * A class used to interface with a Cube16Chunk's block buffer without
 * the
 */
public final class Cube16BlockBuffer {

    protected Cube16Chunk chunk;
    protected ByteBuffer blockBuffer;

    protected byte lastPaletteBitDepth;

    protected byte[] lastBlockPosition;
    protected short lastBlockIndex;
    protected byte lastBitIndex;

    protected Cube16BlockBuffer(Cube16Chunk chunk, ByteBuffer sourceBuffer) {
        this.chunk = chunk;
        this.blockBuffer = sourceBuffer.duplicate();
        this.lastPaletteBitDepth = chunk.getPaletteBitDepth();

        resetBlockBufferPosition();
    }

    /** Sets the position of the block buffer to 0 */
    public void resetBlockBufferPosition() {
        blockBuffer.clear();
        lastBlockPosition = new byte[]{0, 0, 0};
        lastBitIndex =  0;
        lastBlockIndex = 0;
    }

    /** Sets the position of the block buffer to the start of the block coords specified. */
    public void prepareBlockBufferPosition(byte x, byte y, byte z) {
        blockBuffer.clear(); // Resets position to start. Doesn't actually clear.
        short[] position = Cube16Utility.getBufferIndex(lastPaletteBitDepth, x, y, z);
        blockBuffer.position(position[0]);
        lastBitIndex = (byte) position[1]; // Its a byte unless something went wrong.
        lastBlockIndex = (byte) position[2];
        lastBlockPosition = new byte[]{ x, y, z };
    }

    /** Steps forward a number of blocks in the buffer. */
    protected void forward(int steps) {
        lastBlockIndex += steps;
        byte[] newBlock = Cube16Utility.getBlockCoords(lastBlockIndex);
        short[] newBufferPos = Cube16Utility.getBufferIndex(lastPaletteBitDepth, newBlock[0], newBlock[1], newBlock[2]);

        lastBlockPosition = newBlock;
        blockBuffer.position(newBufferPos[0]);
        lastBitIndex = (byte) newBufferPos[1];
    }

    protected void updateBitDepth() {
        // This should be called after the chunk's data has been recalculated.
        prepareBlockBufferPosition(lastBlockPosition[0], lastBlockPosition[1], lastBlockPosition[2]);
    }



    /**
     * A shortcut method that does not recalculate the block index or
     * block position. Should only be used with bulk operations or internal
     * methods followed by a call of the #forward().
     */
    protected int getNextUnsafeBlockBufferValue() {
        int value = 0;
        int n = 1;

        if(lastBitIndex > 0){
            // Re-read the byte. Could've been modified?
            blockBuffer.position(blockBuffer.position() - 1);
        }

        byte requiredByteCount = (byte) Math.ceil( ((float) lastPaletteBitDepth) / 8 );
        byte lastReadIndex = lastBitIndex;

        for(int i = 0; i < requiredByteCount; i++){
            byte singleByte = blockBuffer.get();

            // Bit position is less than the distance to the next block
            // Bit position is less than 8 bits.
            // This works as long as depths are kept to 2^n. Else it starts missing bits.
            for(byte b = lastReadIndex; (b < (lastReadIndex + lastPaletteBitDepth)) && (b < 8); b++) {
                value += (NSMath.getBit(singleByte, b) * (2 ^ n));
                n++;
                lastBitIndex++; // Update the tracker.
            }

            if(lastBitIndex >= 8){
                lastBitIndex = 0; // Reset cached bit index when a new byte is read.
            }
        }

        return value;
    }



    /** @return the next block available in the buffer. */
    public Block getNextBlock() {
        List<Block> palette = chunk.getPalette();

        if(palette.size() == 1) { // Size should always be >1
            forward(1); //Skip forward one anyway.
            return palette.get(0);
        } else {
            return palette.get(getNextBlockBufferValue());
        }
    }

    /** The palette ID of the next block in the buffer. */
    public int getNextBlockBufferValue() {
        int val = getNextUnsafeBlockBufferValue();
        forward(1);
        return val;
    }


    /** @return the next N amount blocks available in the buffer. */
    public Block[] getNextBlocks(int n) {
        List<Block> palette = chunk.getPalette();
        Block[] blocks = new Block[n];

        if(palette.size() == 1) { // Size should always be >1
            for (int i = 0; i < n; i++) blocks[i] = palette.get(0);
            forward(n); //Skip to sync up all indexes

        } else {
            int[] paletteIDs = getNextBlockBufferValues(n);
            for (int i = 0; i < n; i++) blocks[i] = palette.get(paletteIDs[i]);
            // Does not need to forward()
        }

        return blocks;
    }

    /** The palette ID of the next block in the buffer. */
    public int[] getNextBlockBufferValues(int n) {
        int[] values = new int[n];
        for (int i = 0; i < n; i++) values[i] = getNextUnsafeBlockBufferValue();
        forward(n); //Catch up
        return values;
    }


    /** @return returns the chunk this buffer accesses. */
    public Cube16Chunk getChunk() {
        return chunk;
    }

    /*
        I did some testing in python to get the above method right (Arrrrggghhh this took a bit. Smol brain)
        bitDepth = 2

        def prg(x, y, z):
            index = x + (z * 16) + (y * 256)

            scaled = index * (bitDepth / 8)
            remainder = (scaled % 1)
            subIndex = remainder * 8
            byteIndex = scaled - remainder


            print(byteIndex, ":", subIndex,"|",index)
            input("")

        prg(15, 15, 15)  # Check that it's correctly lined up

        for y in range(0, 16):
            for z in range(0, 16):
                for x in range(0, 16):
                    prg(x, y, z)
     */

}
