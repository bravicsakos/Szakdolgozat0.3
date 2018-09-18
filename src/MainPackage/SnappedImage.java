package MainPackage;

public class SnappedImage {

    private String sequenceID;
    private String name;
    private String flagPLACEHOLDER;
    private double minX,minY;
    private double maxX,maxY;

    public SnappedImage(int id, double minX, double minY, double maxX, double maxY) {
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

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

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
