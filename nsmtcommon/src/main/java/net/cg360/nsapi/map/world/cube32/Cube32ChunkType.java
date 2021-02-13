package net.cg360.nsapi.map.world.cube32;

public enum Cube32ChunkType {

    DEFAULT(0),
    FULL(1),
    EMPTY(2);

    private final int chunkTypeID;

    Cube32ChunkType(int chunkTypeID){
        this.chunkTypeID = chunkTypeID;
    }

    public int getChunkyTypeID() {
        return chunkTypeID;
    }


}
