package MainPackage;

/**
 * Class for the saving an image.
 *
 * variable sequenceID : ID of the image, one more than the previous image.
 * variable flagPLACEHOLDER : Place holder variable for flagging a file (i.e. Healthy/Bad)
 * variable minX,
 *           minY,
 *           maxX,
 *           maxY : Top left and bottom right coordinates of the image respectively.
 */
public class SnappedImage {

    private String sequenceID;
    private String flag;
    private double minX,minY;
    private double maxX,maxY;

    SnappedImage(int id, double minX, double minY, double maxX, double maxY, String flag) {
        this.sequenceID = convertSequenceID(id);
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.flag = flag;
    }

    public String getId() {
        return sequenceID;
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

    String getFlag(){
        return flag;
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
