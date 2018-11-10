package MainPackage;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Vector;

import static MainPackage.Constants.*;
import static MainPackage.ThumbnailCreationController.*;
import static MainPackage.MainPageController.grid;

/**
 * Class for rendering grid of pictures.
 *
 * @variable posMap : Maps the four image boxes to positions
 *                    according to their coordinates.
 *
 * @variable position1,
 *           position2,
 *           position3,
 *           position 4 : The four boxes of images rendered on screen.
 *
 * @variable coordX,
 *           coordX : Coordinates of the firs image rendered in the
 *                    first box.
 *
 * @variable qualityLvl : The active quality level of the rendered
 *                        images.
 * @variable qualityMap : Maps the quality levels to the size types
 *                        using the {@Method makeQualityLevels}.
 *
 * @variable gridWidth,
 *           gridHeight : The width and height of the whole image grid.
 *
 * @variable screenSize : Size of the monitor screen.
 * @variable screenWidth,
 *           screenHeight : Width and height derived from
 *                          {@Variable screenSize}.
 *
 * Run flow : At first : qualityLvlManager -> removeAllRenderedImages -> renderFirstImage -> makeImage -> renderImage -> setGridPosition -> multiplePosSetter -> posSetter
 *            On input : inputHandler -> removeRenderedImage -> imageRemover
 *                                    -> makeImage -> renderImage -> setGridPosition -> multiplePosSetter -> posSetter
 */
class PictureRenderer {

    private static HashMap<Integer,ArrayList<ImageV2>> posMap = new HashMap<>();

    static ArrayList<ImageV2> position1 = new ArrayList<>();
    private static ArrayList<ImageV2> position2 = new ArrayList<>();
    private static ArrayList<ImageV2> position3 = new ArrayList<>();
    private static ArrayList<ImageV2> position4 = new ArrayList<>();

    static int coordX = 0;
    static int coordY = 0;

    static int qualityLvl = QUALITY_LEVEL_RAWIMAGE;
    static HashMap<Integer, Integer> qualityMap = new HashMap<>();

    private static int gridWidth;
    private static int gridHeight;

    private final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    final static double screenWidth = screenSize.getWidth();
    private final static double screenHeigth = screenSize.getHeight();

    /**
     * Method called when changing quality levels.
     *
     * Removes all rendered images.
     * Renders the first image boxes of the given
     * quality level.
     * @uses {@variable qualityLvl}
     */
    static void qualityLvlManager(){
        removeAllRenderedImages();
        switch (qualityLvl){
            case QUALITY_LEVEL_RAWIMAGE :
                renderFirstImage(coordX,coordY,TYPE_1X1);
                break;
            case QUALITY_LEVEL_1024 :
                renderFirstImage(coordX,coordY,TYPE_2X2);
                break;
            case QUALITY_LEVEL_512 :
                renderFirstImage(coordX,coordY,TYPE_4X4);
                break;
            case QUALITY_LEVEL_256 :
                renderFirstImage(coordX,coordY,TYPE_8X8);
                break;
            case QUALITY_LEVEL_128 :
                renderFirstImage(coordX,coordY,TYPE_16X16);
                break;
        }
    }

    /**
     * Handles the input from key presses from {@Class MainPageController}
     *
     * @param input : Which arrow key is pressed.
     * @param type : What is the current quality level.
     *
     * Depending on the input removes a row or column from the other side
     * of the rendered pictures, than renders a new line on the given side.
     */
    static void inputHandler(int input, int type) {
        ArrayList<ImageV2> remove1 = new ArrayList<>();
        ArrayList<ImageV2> remove2 = new ArrayList<>();
        ArrayList<ImageV2> add1 = new ArrayList<>();
        ArrayList<ImageV2> add2 = new ArrayList<>();

        int offset = -1;
        int size = -1;

        int coordXA = -1, coordXB = -1, coordYA = -1, coordYB = -1;

        switch (type) {
            case TYPE_1X1:
                offset = 1;
                size = SIZE_1;
                break;
            case TYPE_2X2:
                offset = 2;
                size = SIZE_4;
                break;
            case TYPE_4X4:
                offset = 4;
                size = SIZE_16;
                break;
            case TYPE_8X8:
                offset = 8;
                size = SIZE_64;
                break;
            case TYPE_16X16:
                offset = 16;
                size = SIZE_256;
                break;
        }

        switch (input) {
            case INPUT_LEFT:
                remove1 = posMap.get(POS_UPRIGHT);
                remove2 = posMap.get(POS_DOWNRIGHT);
                add1 = posMap.get(POS_UPLEFT);
                add2 = posMap.get(POS_DOWNLEFT);

                coordXA = add1.get(0).getCoordX() - offset;
                coordXB = add2.get(0).getCoordX() - offset;
                coordYA = add1.get(0).getCoordY();
                coordYB = add2.get(0).getCoordY();
                break;
            case INPUT_RIGHT:
                remove1 = posMap.get(POS_UPLEFT);
                remove2 = posMap.get(POS_DOWNLEFT);
                add1 = posMap.get(POS_UPRIGHT);
                add2 = posMap.get(POS_DOWNRIGHT);

                coordXA = add1.get(0).getCoordX() + offset;
                coordXB = add2.get(0).getCoordX() + offset;
                coordYA = add1.get(0).getCoordY();
                coordYB = add2.get(0).getCoordY();
                break;
            case INPUT_UP:
                remove1 = posMap.get(POS_DOWNLEFT);
                remove2 = posMap.get(POS_DOWNRIGHT);
                add1 = posMap.get(POS_UPLEFT);
                add2 = posMap.get(POS_UPRIGHT);

                coordXA = add1.get(0).getCoordX();
                coordXB = add2.get(0).getCoordX();
                coordYA = add1.get(0).getCoordY() - offset;
                coordYB = add2.get(0).getCoordY() - offset;
                break;
            case INPUT_DOWN:
                remove1 = posMap.get(POS_UPLEFT);
                remove2 = posMap.get(POS_UPRIGHT);
                add1 = posMap.get(POS_DOWNLEFT);
                add2 = posMap.get(POS_DOWNRIGHT);

                coordXA = add1.get(0).getCoordX();
                coordXB = add2.get(0).getCoordX();
                coordYA = add1.get(0).getCoordY() + offset;
                coordYB = add2.get(0).getCoordY() + offset;
                break;
        }
        removeRenderedImage(remove1);
        removeRenderedImage(remove2);
        remove1.clear();
        remove2.clear();
        remove1.addAll(add1);
        remove2.addAll(add2);
        add1.clear();
        add2.clear();
        add1.addAll(makeImage(size, coordXA, coordYA));
        add2.addAll(makeImage(size, coordXB, coordYB));
        setGridPosition(type);

    }

    /**
     * Method used to render the images on first render.
     *
     * @param minX : The x coordinate of the left-most image.
     * @param minY : The y coordinate of the left-most image.
     * @param type : The current quality level.
     */
    private static void renderFirstImage(int minX, int minY, int type){
        position1.clear();
        position2.clear();
        position3.clear();
        position4.clear();
        int offset = -1;
        int size = -1;
        switch (type) {
            case TYPE_1X1 :
                offset = 1;
                size = SIZE_1;
                break;
            case TYPE_2X2 :
                offset = 2;
                size = SIZE_4;
                break;
            case TYPE_4X4 :
                offset = 4;
                size = SIZE_16;
                break;
            case TYPE_8X8 :
                offset = 8;
                size = SIZE_64;
                break;
            case TYPE_16X16 :
                offset = 16;
                size = SIZE_256;
                break;
        }
        position1.addAll(makeImage(size,minX,minY));
        position2.addAll(makeImage(size,minX + offset,minY));
        position3.addAll(makeImage(size,minX,minY + offset));
        position4.addAll(makeImage(size,minX + offset,minY + offset));
        setGridPosition(type);
    }

    /**
     * Renders a box of images int he given location
     *
     * @param size : The size of the current quality level.
     * @param minX : The x coordinate of the left-most image.
     * @param minY : The y coordinate of the left-most image.
     * @return : An array list of the rendered images.
     */
    private static ArrayList<ImageV2> makeImage(int size, int minX, int minY){
        ArrayList<ImageV2> newList = new ArrayList<>();
        switch (size) {
            case SIZE_1 :
                if (isRenderable(minX,minY)) {
                    newList.add(new ImageV2(Objects.requireNonNull(renderImage(minX, minY, 1, TYPE_1X1))));
                }
                break;
            case SIZE_4 :
                if (isRenderable(minX,minY)) {
                    newList.add(new ImageV2(Objects.requireNonNull(renderImage(minX, minY, 2, TYPE_2X2))));
                }
                if (isRenderable(minX + 1,minY)) {
                    newList.add(new ImageV2(Objects.requireNonNull(renderImage(minX + 1, minY, 2, TYPE_2X2))));
                }
                if (isRenderable(minX,minY + 1)) {
                    newList.add(new ImageV2(Objects.requireNonNull(renderImage(minX, minY + 1, 2, TYPE_2X2))));
                }
                if (isRenderable(minX + 1,minY + 1)) {
                    newList.add(new ImageV2(Objects.requireNonNull(renderImage(minX + 1, minY + 1, 2, TYPE_2X2))));
                }
                break;
            case SIZE_16 :
                for (int j = 0; j < 4; j++) {
                    for (int i = 0; i < 4; i++) {
                        if (isRenderable(minX + i,minY + j)) {
                            if (j == 0 && i == 0) {
                                newList.add(new ImageV2(Objects.requireNonNull(renderImage(minX + i, minY + j, 4, TYPE_4X4))));
                            } else {
                                newList.add(new ImageV2(Objects.requireNonNull(renderImage(minX + i, minY + j, 4, TYPE_4X4))));
                            }
                        }
                    }
                }
                break;
            case SIZE_64 :
                for (int j = 0; j < 8; j++) {
                    for (int i = 0; i < 8; i++) {
                        if (isRenderable(minX+ i,minY +j)) {
                            if (j == 0 && i == 0) {
                                newList.add(new ImageV2(Objects.requireNonNull(renderImage(minX + i, minY + j, 8, TYPE_8X8))));
                            } else {
                                newList.add(new ImageV2(Objects.requireNonNull(renderImage(minX + i, minY + j, 8, TYPE_8X8))));
                            }
                        }
                    }
                }
                break;
            case SIZE_256 :
                for (int j = 0; j < 16; j++) {
                    for (int i = 0; i < 16; i++) {
                        if (isRenderable(minX + i,minY + j)) {
                            if (j == 0 && i == 0) {
                                newList.add(new ImageV2(Objects.requireNonNull(renderImage(minX + i, minY + j, 16, TYPE_16X16))));
                            } else {
                                newList.add(new ImageV2(Objects.requireNonNull(renderImage(minX + i, minY + j, 16, TYPE_16X16))));
                            }
                        }
                    }
                }
                break;

        }
        return newList;
    }

    /**
     * Renders a single image in the given location.
     *
     * @param minX : The x coordinate of the left-most image.
     * @param minY : The y coordinate of the left-most image.
     * @param imageSize : The size of the image needed to be rendered.
     * @param type : Current quality level.
     * @return : The rendered image
     */
    private static ImageV2 renderImage(int minX, int minY, int imageSize, int type){
        // TODO : Speed up image rendering process ( maybe cache? )
        int imageID = computeImageID(minX, minY);
        File newImageFile = null;
        if (imageID == -1) {
            System.err.println("No picture with those coordinates PictureRenderer/renderOneImage");
            return null;
        }
        switch (type){
            case TYPE_1X1 :
                newImageFile = imageFiles.get(imageID);
                break;
            case TYPE_2X2 :
                newImageFile = thumbnailFiles1024X1024.get(imageID);
                break;
            case TYPE_4X4 :
                newImageFile = thumbnailFiles512X512.get(imageID);
                break;
            case TYPE_8X8 :
                newImageFile = thumbnailFiles256X256.get(imageID);
                break;
            case TYPE_16X16 :
                newImageFile = thumbnailFiles128X128.get(imageID);
                break;
        }
        try {
            assert newImageFile != null;
            ImageV2 newImage = new ImageV2(newImageFile.toURI().toURL().toString(), imageID, minX, minY);
            ImageView newImgView = new ImageView(newImage);
            newImgView.setFitHeight((screenHeigth - 100)/ (2*imageSize));
            newImgView.setPreserveRatio(true);
            grid.getChildren().add(newImgView);
            return newImage;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Temp method to create fix size grid
     *
     * @param width : Width of the grid created.
     * @param heigth : Height of the grid created.
     */
    private static void makeGrid(int width, int heigth){
        // TODO : Implement specific image based grid creation
        gridWidth = width;
        gridHeight = heigth;
    }

    /**
     * Transforms the given coordinates into image ID.
     *
     * @param minX : X coordinate of the image.
     * @param minY : Y coordinate of the image
     * @return : The ID of the image.
     */
    private static int computeImageID(int minX, int minY){
        if (minX < 0 || minY < 0){
            return -1;
        }
        if (minX > gridWidth || minY > gridHeight){
            return -1;
        }
        return gridHeight - minY - 1 + minX*gridHeight;
    }

    /**
     * Removes all of the images inside the given image list.
     *
     * @param imageList : List of images to be removed.
     */
    private static void removeRenderedImage(ArrayList<ImageV2> imageList){
        Vector<Integer> ids = new Vector<>();
        for (ImageV2 img: imageList) {
            ids.add(img.getId());
        }
        imageRemover(ids);
    }

    /**
     * Removes all currently rendered images.
     */
    private static void removeAllRenderedImages(){
        if (grid.getChildren().size() > 0) {
            grid.getChildren().subList(0, grid.getChildren().size()).clear();
        }
    }

    /**
     * Sets the position of all the image boxes in the grid.
     *
     * @param type : Current quality level.
     */
    private static void setGridPosition(int type){
        int offset = 0;
        switch (type){
            case TYPE_1X1 :
                offset = 1;
                break;
            case TYPE_2X2 :
                offset = 2;
                break;
            case TYPE_4X4 :
                offset = 4;
                break;
            case TYPE_8X8 :
                offset = 8;
                break;
            case TYPE_16X16 :
                offset = 16;
                break;
        }
        multiplePosSetter(0,0,position1);
        multiplePosSetter(offset,0,position2);
        multiplePosSetter(0,offset,position3);
        multiplePosSetter(offset,offset,position4);
    }

    /**
     * Sets the position of a specific image in the grid.
     *
     * @param column : The column where the image should be placed.
     * @param row : The row where the image should be placed.
     * @param image : The image which should be placed.
     */
    private static void posSetter(int column, int row, ImageV2 image){
        int pos = findInGrid(image);
        GridPane.setColumnIndex(grid.getChildren().get(pos),column);
        GridPane.setRowIndex(grid.getChildren().get(pos),row);
    }

    /**
     * Sets the position of a whole list of images in the grid.
     *
     * @param startColumn : First column of the images.
     * @param startRow : First row of the images.
     * @param imageList : List of the images.
     */
    private static void multiplePosSetter(int startColumn, int startRow, ArrayList<ImageV2> imageList){
        switch (imageList.size()){
            case SIZE_1 :
                posSetter(startColumn,startRow,imageList.get(0));
                break;
            case SIZE_4 :
                posSetter(startColumn,startRow,imageList.get(0));
                posSetter(startColumn+1,startRow,imageList.get(1));
                posSetter(startColumn,startRow+1,imageList.get(2));
                posSetter(startColumn+1,startRow+1,imageList.get(3));
                break;
            case SIZE_16 :
                for (int j = 0; j < 4; j++) {
                    for (int i = 0; i < 4; i++) {
                        posSetter(startColumn + i, startRow + j, imageList.get(j*4 + i));
                    }

                }
                break;
            case SIZE_64 :
                for (int j = 0; j < 8; j++) {
                    for (int i = 0; i < 8; i++) {
                        posSetter(startColumn + i, startRow + j, imageList.get(j*8 + i));
                    }

                }
                break;
            case SIZE_256 :
                for (int j = 0; j < 16; j++) {
                    for (int i = 0; i < 16; i++) {
                        posSetter(startColumn + i, startRow + j, imageList.get(j*16 + i));
                    }

                }
                break;

        }
    }

    /**
     * Find the position of specified image in the list of images in the grid.
     *
     * @param img : The image to find.
     * @return : The place in the list.
     */
    private static int findInGrid(ImageV2 img){
        for (int i = 0; i < grid.getChildren().size(); i++) {
            if (grid.getChildren().get(i) instanceof ImageView){
                if (((ImageView)grid.getChildren().get(i)).getImage().equals(img)){
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Removes the images with the specific location in the grid list.
     *
     * @param ids : locations of images to be removed.
     */
    private static void imageRemover(Vector<Integer> ids){
        for (int i = 0; i < grid.getChildren().size(); i++) {
            if (grid.getChildren().get(i) instanceof ImageView) {
                for (Integer j: ids) {
                    if (((ImageV2)(((ImageView) grid.getChildren().get(i)).getImage())).getId() == j ) {
                        grid.getChildren().remove(i);
                        if (j.equals(ids.get(ids.size() - 1))){
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates the {@variable qualityMap}.
     */
    private static void makeQualityLevels(){
        qualityMap = new HashMap<>();
        qualityMap.put(QUALITY_LEVEL_RAWIMAGE, TYPE_1X1);
        qualityMap.put(QUALITY_LEVEL_1024, TYPE_2X2);
        qualityMap.put(QUALITY_LEVEL_512, TYPE_4X4);
        qualityMap.put(QUALITY_LEVEL_256, TYPE_8X8);
        qualityMap.put(QUALITY_LEVEL_128, TYPE_16X16);
    }

    /**
     * Start method that runs when the grid render is called.
     *
     * Makes the base grid and loads the first images into the image boxes.
     */
    static void run(){

        makeGrid(100, 100);

        posMap.put(POS_UPLEFT,position1);
        posMap.put(POS_UPRIGHT,position2);
        posMap.put(POS_DOWNLEFT,position3);
        posMap.put(POS_DOWNRIGHT,position4);
        makeQualityLevels();
        qualityLvl = QUALITY_LEVEL_128;
        PictureRenderer.qualityLvlManager();

    }

    /**
     * Decides if the given coordinates are part of the list of images to be rendered.
     *
     * @param coordinateX : X coordinate of specific location.
     * @param coordinateY : Y coordinate of specific location.
     * @return : Whether there is an image to be rendered at the given coordinates.
     */
    private static boolean isRenderable(int coordinateX, int coordinateY){
        int id = PictureRenderer.computeImageID(coordinateX,coordinateY);
        return id >= 0 && id < imageListSize;
    }
}
