package sample;

public class SnappedImage {

    private int id;
    private String name;
    private double minX,minY;
    private double maxX,maxY;

    public SnappedImage(int id, double minX, double minY, double maxX, double maxY) {
        this.id = id;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.name = "SnappedImage_" + id + ".png";
    }

    public int getId() {
        return id;
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
}
