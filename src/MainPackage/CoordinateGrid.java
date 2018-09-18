package MainPackage;

public class CoordinateGrid {

    private double fullWidth;
    private double fullHeight;
    protected double zoomAmount;

    private int startPosX;
    private int startPosY;
    protected double posX;
    protected double posY;

    private double screenWidth;
    private double screenHeight;

    protected double xOffset;
    protected double yOffest;

    public CoordinateGrid(double fullWidth, double fullHeight, double screenWidth, double screenHeight) {
        this.fullWidth = fullWidth;
        this.fullHeight = fullHeight;
        this.zoomAmount = 1;

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.startPosX = 0;
        this.startPosY = 0;
        this.posY = 0;
        this.posY = 0;

        this.xOffset = 0;
        this.yOffest = 0;
    }


    public CoordinateGrid(int fullWidth, int fullHeight, double screenWidth, double screenHeight, int startPosX, int startPosY) {
        this.fullWidth = fullWidth;
        this.fullHeight = fullHeight;
        this.zoomAmount = 1;

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.startPosX = startPosX;
        this.startPosY = startPosY;
        this.posX = startPosX;
        this.posY = startPosY;

        this.xOffset = 0;
        this.yOffest = 0;
    }

    public double getFullWidth() {
        return fullWidth;
    }
    public double getFullHeight() {
        return fullHeight;
    }
    public int getStartPosX() {
        return startPosX;
    }
    public int getStartPosY() {
        return startPosY;
    }

    public void moveGridPos(double volumeX, double volumeY, double translateX, double translateY){
        double mousePosComponentX = volumeX/this.zoomAmount;
        double mousePosComponentY = volumeY/zoomAmount;

        double scrollComponentX = this.screenWidth*(this.zoomAmount-1)/(2*this.zoomAmount);
        double scrollComponentY = this.screenHeight*(this.zoomAmount-1)/(2*this.zoomAmount);

        double dragComponentX = translateX*(1/this.zoomAmount);
        double dragComponentY = translateY*(1/this.zoomAmount);

        int newPosX = (int) (mousePosComponentX + scrollComponentX - dragComponentX - xOffset);
        int newPosY = (int) (mousePosComponentY + scrollComponentY - dragComponentY - yOffest);

        if (newPosX <= fullWidth && newPosX > 0) {
            this.posX = newPosX;
        }
        else if (newPosX > fullWidth){
            this.posX = fullWidth;
        }
        else {
            this.posX = 0;
        }
        if (newPosY<= fullHeight && newPosY > 0) {
            this.posY = newPosY;
        }
        else if (newPosY > fullHeight){
            this.posY = fullHeight;
        }
        else {
            this.posY = 0;
        }
    }
}
