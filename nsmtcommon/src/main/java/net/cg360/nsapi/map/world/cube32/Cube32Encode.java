package net.cg360.nsapi.map.world.cube32;

public enum Cube32Encode {

    DEFAULT(0),
    RUN_LENGTH(1);

    private final int encodeTypeID;

    Cube32Encode(int encodeTypeID){
        this.encodeTypeID = encodeTypeID;
    }

    public int getEncodeTypeID() {
        return encodeTypeID;
    }


    public static Cube32Encode getFromID(int id){
        switch (id){
            case 1:
                return RUN_LENGTH;

            case 0:
            default:
                return DEFAULT;
        }
    }
}
