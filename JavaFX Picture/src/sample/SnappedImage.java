package sample;

/**
 * Class for the saving an image.
 *
 * @variable sequenceID : ID of the image, one more than the previous image.
 * @variable name : Name of the snapped image // TODO : might be deprecated
 * @variable flagPLACEHOLDER : Place holder variable for flagging a file (i.e. Healthy/Bad)
 * @variable minX,
 *           minY,
 *           maxX,
 *           maxY : Top left and bottom right coordinates of the image respectively.
 */
public class SnappedImage {

    private String sequenceID;
    private String name;
    private String flagPLACEHOLDER; // TODO : add flags to the saved pictures
    private double minX,minY;
    private double maxX,maxY;

    SnappedImage(int id, double minX, double minY, double maxX, double maxY) {
        this.sequenceID = convertSequenceID(id);
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.name = "SnappedImage_" + id + ".png";
        this.flagPLACEHOLDER = "PLACEHOLDER";
    }

    public String getId() {
        return sequenceID;
    }

    public String getName() {
        return name;
    }

    double getMinX() {
        return minX;
    }

    double getMinY() {
        return minY;
    }

    double getMaxX() {
        return maxX;
    }

    double getMaxY() {
        return maxY;
    }

    /**
     * Converts a number into the sequence ID format
     *
     * @param id : Base number.
     * @return : Returns with the created sequence ID.
     */
    private static String convertSequenceID(int id){
        int maxNumber = 9;
        int thisNumber;
        String stringID = String.valueOf(id);
        StringBuilder sequenceID;
        thisNumber = maxNumber - stringID.length();
        if (thisNumber > 0){
            sequenceID = new StringBuilder("0");
            for (int i = 0; i < thisNumber-1; i++) {
                sequenceID.append("0");
            }
            sequenceID.append(id);
        }
        else {
            sequenceID = new StringBuilder(stringID);
        }
        return sequenceID.toString();

    }
}
