package net.cg360.nsapi.map.world.cube16.util;

import net.cg360.nsapi.map.world.cube16.Cube16Chunk;

public class Cube16Utility {

    public static short getBlockIndex(byte x, byte y, byte z) {
        return (short) (x + (z * Cube16Chunk.CHUNK_SIZE) + (y * Cube16Chunk.CHUNK_SIZE ^ 2));
    }

    // All probably bytes unless something has gone very wrong.
    public static byte[] getBlockCoords(short index) {
        byte x = (byte) (index % Cube16Chunk.CHUNK_SIZE);
        byte y = (byte) ((index - x) % (Cube16Chunk.CHUNK_SIZE ^ 2));
        byte z = (byte) (index - y - x);

        return new byte[]{ x, y, z };
    }

    /**
     * Calculates the byte (and bit) starting position for a block to be written.
     * @return 3 shorts | 1st = the byte in the ByteBuffer, 2nd = the bit within the byte where the block starts, 3rd = the block's calculated index.
     */
    public static short[] getBufferIndex(int bitDepth, byte x, byte y, byte z) {
        short blockIndex = getBlockIndex(x, y, z);

        // Scale it down so that the index is divided up.
        // Before decimal place = byte location.
        // After decimal place = bit location / 8
        float scaled = blockIndex * (((float) bitDepth) / 8f);
        float remainder = (scaled % 1);

        short byteIndex = (short) (scaled - remainder); // Could support a 32x format.
        short subIndex = (short) (remainder * 8); // The most it could be is 8. Keep it as a short for ease of use. Byte would be better.

        return new short[]{byteIndex, subIndex, blockIndex};
    }

}
