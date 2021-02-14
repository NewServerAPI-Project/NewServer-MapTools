package net.cg360.nsapi.map.world.cube16;

public enum Cube16Encode {

    DEFAULT(0),
    RUN_LENGTH(1);

    private final int encodeTypeID;

    Cube16Encode(int encodeTypeID){
        this.encodeTypeID = encodeTypeID;
    }

    public int getEncodeTypeID() {
        return encodeTypeID;
    }


    public static Cube16Encode getFromID(int id){
        switch (id){
            case 1:
                return RUN_LENGTH;

            case 0:
            default:
                return DEFAULT;
        }
    }
}
