package MainPackage;


/**
 * Work in progress coordinate grid
 * */
public class CoordinateGrid {
    // TODO : Rework of the class to fit draw board or exe tracker may remove fully

    private double fullWidth;
    private double fullHeight;
    double zoomAmount;

    private int startPosX;
    private int startPosY;
    double posX;
    double posY;

    private double screenWidth;
    private double screenHeight;

    private double xOffset;
    double yOffest;

    CoordinateGrid(double fullWidth, double fullHeight, double screenWidth, double screenHeight) {
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
