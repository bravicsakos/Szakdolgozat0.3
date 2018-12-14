package MainPackage;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

import static MainPackage.MainPageController.*;
import static MainPackage.Constant.*;
import static MainPackage.IniTools.settings;
import static MainPackage.Main.sectionGeneral;
import static MainPackage.ThumbnailCreationController.*;

/**
 * Class for rendering grid of pictures.
 *
 * variable posMap : Maps the four image boxes to positions
 *                    according to their coordinates.
 *
 * variable position1,
 *           position2,
 *           position3,
 *           position 4 : The four boxes of images rendered on screen.
 *
 * variable coordX,
 *           coordX : Coordinates of the firs image rendered in the
 *                    first box.
 *
 * variable qualityLvl : The active quality level of the rendered
 *                        images.
 * variable qualityMap : Maps the quality levels to the size types
 *                        using the {Method makeQualityLevels}.
 *
 * variable gridWidth,
 *           gridHeight : The width and height of the whole image grid.
 *
 * variable screenSize : Size of the monitor screen.
 * variable screenWidth,
 *           screenHeight : Width and height derived from
 *                          {variable screenSize}.
 *
 * Run flow : At first : qualityLvlManager - removeAllRenderedImages - renderFirstImage - makeImage - renderImage - setGridPosition - multiplePosSetter - posSetter
 *            On input : inputHandler - removeRenderedImage - imageRemover
 *                                    - makeImage - renderImage - setGridPosition - multiplePosSetter - posSetter
 */
class PictureRenderer {

    private static HashMap<Constant,ArrayList<ImageV2>> posMap = new HashMap<>();

    static ArrayList<ImageV2> position1 = new ArrayList<>();
    private static ArrayList<ImageV2> position2 = new ArrayList<>();
    private static ArrayList<ImageV2> position3 = new ArrayList<>();
    private static ArrayList<ImageV2> position4 = new ArrayList<>();

    static int coordX = 0;
    static int coordY = 0;

    static Constant qualityLvl = QUALITY_LEVEL_RAWIMAGE;
    static HashMap<Constant, Constant> qualityMap = new HashMap<>();

    private static int gridWidth;
    private static int gridHeight;

    private final static double screenHeigth = Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    private final static HashMap<Integer, Integer> idMap = makeConversionMap();
    private final static File emptyImageFolder = new File(System.getProperty("user.dir") + "\\" + sectionGeneral.getValue("EMPTY_IMAGE_FOLDER"));
    private final static File emptyImageFile = new File(emptyImageFolder.getPath() + "\\" + sectionGeneral.getValue("EMPTY_IMAGE_NAME"));
    private final static File emptyImageFile1024 = new File(emptyImageFolder.getPath() + "\\" + sectionGeneral.getValue("EMPTY_IMAGE_1024_NAME"));
    private final static File emptyImageFile512 = new File(emptyImageFolder.getPath() + "\\" + sectionGeneral.getValue("EMPTY_IMAGE_512_NAME"));
    private final static File emptyImageFile256 = new File(emptyImageFolder.getPath() + "\\" + sectionGeneral.getValue("EMPTY_IMAGE_256_NAME"));
    private final static File emptyImageFile128 = new File(emptyImageFolder.getPath() + "\\" + sectionGeneral.getValue("EMPTY_IMAGE_128_NAME"));


    /**
     * Method called when changing quality levels.
     *
     * Removes all rendered images.
     * Renders the first image boxes of the given
     * quality level.
     * uses {variable qualityLvl}
     */
    static void qualityLvlManager(){
        removeAllRenderedImages();
        MainPageController.resetMove();
        switch (qualityLvl){
            case QUALITY_LEVEL_RAWIMAGE :
                renderFirstImage(coordX,coordY,TYPE_1X1);
                MainPageController.isZoomReady = true;
                break;
            case QUALITY_LEVEL_1024 :
                renderFirstImage(coordX,coordY,TYPE_2X2);
                MainPageController.isZoomReady = false;
                break;
            case QUALITY_LEVEL_512 :
                renderFirstImage(coordX,coordY,TYPE_4X4);
                MainPageController.isZoomReady = false;
                break;
            case QUALITY_LEVEL_256 :
                renderFirstImage(coordX,coordY,TYPE_8X8);
                MainPageController.isZoomReady = false;
                break;
            case QUALITY_LEVEL_128 :
                renderFirstImage(coordX,coordY,TYPE_16X16);
                MainPageController.isZoomReady = false;
                break;
        }
        setSizeText();
    }

    /**
     * Handles the input from key presses from {Class MainPageController}
     *
     * @param input : Which arrow key is pressed.
     * @param type : What is the current quality level.
     *
     * Depending on the input removes a row or column from the other side
     * of the rendered pictures, than renders a new line on the given side.
     */
    static void inputHandler(Constant input, @NotNull Constant type) {
        ArrayList<ImageV2> remove1 = new ArrayList<>();
        ArrayList<ImageV2> remove2 = new ArrayList<>();
        ArrayList<ImageV2> add1 = new ArrayList<>();
        ArrayList<ImageV2> add2 = new ArrayList<>();

        int offset = getOffset(type);
        Constant size = getSize(type);

        int coordXA = -1, coordXB = -1, coordYA = -1, coordYB = -1;


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
        // TODO : Optimization
        if (calculateOffset(coordXA, offset, true) >0
                || calculateOffset(coordXB, offset, true) >0
                || calculateOffset(coordYA, offset, false) >0
                || calculateOffset(coordYB, offset, false) >0){
            removeAllRenderedImages();
            renderFirstImage(add1.get(0).getCoordX(), add1.get(0).getCoordY(),type);
        }
        else if (calculateOffset(coordXA, offset, true) <0
                || calculateOffset(coordXB, offset, true) <0){
            removeAllRenderedImages();
            renderFirstImage(0, add1.get(0).getCoordY(),type);
        }
        else if (calculateOffset(coordYA, offset, false) <0
                    || calculateOffset(coordYB, offset, false) <0){
            removeAllRenderedImages();
            renderFirstImage(add1.get(0).getCoordX(), 0,type);
        }
        else {
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
            setCoordText();
        }

    }

    /**
     * Method used to render the images on first render.
     *
     * @param minX : The x coordinate of the left-most image.
     * @param minY : The y coordinate of the left-most image.
     * @param type : The current quality level.
     */
    private static void renderFirstImage(int minX, int minY, @NotNull Constant type){
        position1.clear();
        position2.clear();
        position3.clear();
        position4.clear();
        int offsetX, offsetY;
        int offset = getOffset(type);
        Constant size = getSize(type);

        offsetX = calculateOffset(minX + offset, offset, true);
        offsetY = calculateOffset(minY + offset, offset, true);
        position1.addAll(makeImage(size,minX           - offsetX,minY           - offsetY));
        position2.addAll(makeImage(size,minX + offset  - offsetX,minY           - offsetY));
        position3.addAll(makeImage(size,minX           - offsetX,minY + offset  - offsetY));
        position4.addAll(makeImage(size,minX + offset  - offsetX,minY + offset  - offsetY));
        setGridPosition(type);
        setCoordText();
    }

    /**
     * Renders a box of images int he given location
     *
     * @param size : The size of the current quality level.
     * @param minX : The x coordinate of the left-most image.
     * @param minY : The y coordinate of the left-most image.
     * @return : An array list of the rendered images.
     */
    private static ArrayList<ImageV2> makeImage(@NotNull Constant size, int minX, int minY){
        ArrayList<ImageV2> newList = new ArrayList<>();
        switch (size) {
            case SIZE_1 :
                newList.add(new ImageV2(Objects.requireNonNull(renderImage(minX, minY, 1, TYPE_1X1))));
                break;
            case SIZE_4 :
                newList.add(new ImageV2(Objects.requireNonNull(renderImage(minX      , minY , 2, TYPE_2X2))));
                newList.add(new ImageV2(Objects.requireNonNull(renderImage(minX + 1 , minY , 2, TYPE_2X2))));
                newList.add(new ImageV2(Objects.requireNonNull(renderImage(minX  , minY + 1, 2, TYPE_2X2))));
                newList.add(new ImageV2(Objects.requireNonNull(renderImage(minX + 1 , minY + 1 , 2, TYPE_2X2))));
                break;
            case SIZE_16 :
                for (int j = 0; j < 4; j++) {
                    for (int i = 0; i < 4; i++) {
                        newList.add(new ImageV2(Objects.requireNonNull(renderImage(minX + i , minY + j , 4, TYPE_4X4))));
                    }
                }
                break;
            case SIZE_64 :
                for (int j = 0; j < 8; j++) {
                    for (int i = 0; i < 8; i++) {
                        newList.add(new ImageV2(Objects.requireNonNull(renderImage(minX + i, minY + j, 8, TYPE_8X8))));
                    }
                }
                break;
            case SIZE_256 :
                for (int j = 0; j < 16; j++) {
                    for (int i = 0; i < 16; i++) {
                        newList.add(new ImageV2(Objects.requireNonNull(renderImage(minX + i, minY + j, 16, TYPE_16X16))));
                    }
                }
                break;

        }
        return newList;
    }

    @Contract(pure = true)
    private static int calculateOffset(int minCoord, int size, boolean X){
        int max = X ? gridWidth : gridHeight;
        if (minCoord + size - 1 < max){
            if (minCoord >= 0) {
                return 0;
            }
        }
        else if (minCoord + size - 1 >= max){
            return (minCoord + size) - max;
        }

        return minCoord;
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
    @Nullable
    private static ImageV2 renderImage(int minX, int minY, int imageSize, Constant type){
        int imageID = computeImageID(minX, minY);
        File newImageFile = null;
        if (imageID < 0){
            switch (type) {
                case TYPE_1X1:
                    newImageFile = emptyImageFile;
                    break;
                case TYPE_2X2:
                    newImageFile = emptyImageFile1024;
                    break;
                case TYPE_4X4:
                    newImageFile = emptyImageFile512;
                    break;
                case TYPE_8X8:
                    newImageFile = emptyImageFile256;
                    break;
                case TYPE_16X16:
                    newImageFile = emptyImageFile128;
                    break;
            }
        }
        else {
            switch (type) {
                case TYPE_1X1:
                    newImageFile = imageFiles.get(imageID);
                    break;
                case TYPE_2X2:
                    newImageFile = thumbnailFiles1024X1024.get(imageID);
                    break;
                case TYPE_4X4:
                    newImageFile = thumbnailFiles512X512.get(imageID);
                    break;
                case TYPE_8X8:
                    newImageFile = thumbnailFiles256X256.get(imageID);
                    break;
                case TYPE_16X16:
                    newImageFile = thumbnailFiles128X128.get(imageID);
                    break;
            }
        }
        try {
            assert newImageFile != null;
            ImageV2 newImage = new ImageV2(newImageFile.toURI().toURL().toString(), imageID, minX, minY);
            ImageView newImgView = new ImageView(newImage);
            newImgView.setCache(true);
            newImgView.setFitWidth((screenHeigth - 100) / (2 * imageSize));
            newImgView.setPreserveRatio(true);
            grid.getChildren().add(newImgView);
            return newImage;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Transforms the given coordinates into image ID.
     *
     * @param minX : X coordinate of the image.
     * @param minY : Y coordinate of the image
     * @return : The ID of the image.
     */
    private static int computeImageID(int minX, int minY){
        if (idMap.get(gridWidth * minY + minX/*gridWidth - minY - 1 + minX*gridWidth*/) == null){
            return -(gridWidth * minY + minX);
        }
        return idMap.get(gridWidth * minY + minX/*gridWidth - minY - 1 + minX*gridWidth*/);
    }

    /**
     * Removes all of the images inside the given image list.
     *
     * @param imageList : List of images to be removed.
     */
    private static void removeRenderedImage(@NotNull ArrayList<ImageV2> imageList){
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
    private static void setGridPosition(@NotNull Constant type){
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
    private static void multiplePosSetter(int startColumn, int startRow, @NotNull ArrayList<ImageV2> imageList){
        switch (imageList.size()){
            case 1 :
                posSetter(startColumn,startRow,imageList.get(0));
                break;
            case 4 :
                posSetter(startColumn,startRow,imageList.get(0));
                posSetter(startColumn+1,startRow,imageList.get(1));
                posSetter(startColumn,startRow+1,imageList.get(2));
                posSetter(startColumn+1,startRow+1,imageList.get(3));
                break;
            case 16 :
                for (int j = 0; j < 4; j++) {
                    for (int i = 0; i < 4; i++) {
                        posSetter(startColumn + i, startRow + j, imageList.get(j*4 + i));
                    }

                }
                break;
            case 64 :
                for (int j = 0; j < 8; j++) {
                    for (int i = 0; i < 8; i++) {
                        posSetter(startColumn + i, startRow + j, imageList.get(j*8 + i));
                    }

                }
                break;
            case 256 :
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
     * Creates the {variable qualityMap}.
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

        posMap.put(POS_UPLEFT,position1);
        posMap.put(POS_UPRIGHT,position2);
        posMap.put(POS_DOWNLEFT,position3);
        posMap.put(POS_DOWNRIGHT,position4);
        makeQualityLevels();
        qualityLvl = QUALITY_LEVEL_128;
        PictureRenderer.qualityLvlManager();

    }


    private static HashMap<Integer, Integer> makeConversionMap(){
        File xmlFile = new File(projectFolder  + "\\" + settings.get("ROBOT_FUNCTIONS","RAW_IMAGE_FOLDER_FULL_NAME") + "\\_meta.xml");
        HashMap<Integer, Vector<Integer>> posMap = new HashMap<>();
        int index = 0;
        Vector<Integer> pos = null;
        try {
            String line;
            BufferedReader br = new BufferedReader(new FileReader(xmlFile));
            while ((line = br.readLine()) != null){
                if (line.matches(".+<p.+")){
                    index = Integer.parseInt(line.substring(line.indexOf('<') + 2,line.indexOf('>')));
                }
                else if (line.matches(".+<V0.+")){
                    pos = new Vector<>();
                    pos.add((int)Double.parseDouble(line.substring(line.indexOf('>') + 1,line.indexOf('<',line.indexOf('<') + 1))));
                }
                else if (line.matches(".+<V1.+")){
                    assert pos != null;
                    pos.add((int)Double.parseDouble(line.substring(line.indexOf('>') + 1,line.indexOf('<',line.indexOf('<') + 1))));
                }
                posMap.put(index, pos);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return calculateRealID(posMap);
    }

    private static HashMap<Integer, Integer> calculateRealID(@NotNull HashMap<Integer, Vector<Integer>> posMap){
        int row = 0;
        int column = 0;
        ArrayList<Integer> columnValue = new ArrayList<>();
        HashMap<Integer, Integer> columnMap = new HashMap<>();
        ArrayList<Integer> rowValue = new ArrayList<>();
        HashMap<Integer, Integer> rowMap = new HashMap<>();



        HashMap<Integer, Integer> idMap = new HashMap<>();

        for (int i = 0; i < posMap.size(); i++) {
            if (!columnValue.contains(posMap.get(i).get(0))){
                columnValue.add(posMap.get(i).get(0));
            }
            if (!rowValue.contains(posMap.get(i).get(1))){
                rowValue.add(posMap.get(i).get(1));
            }
        }

        Collections.sort(columnValue);
        Collections.sort(rowValue);

        for (Integer i: columnValue) {
            columnMap.put(i,column);
            column++;
        }
        gridWidth = column;


        for (Integer i: rowValue) {
            rowMap.put(i,row);
            row++;
        }
        gridHeight = row;

        for (int i = 0; i < posMap.size(); i++) {
            row = rowMap.get(posMap.get(i).get(1));
            column = columnMap.get(posMap.get(i).get(0));
            int id = gridWidth * row + column;/*gridWidth - row - 1 + column*gridWidth;*/
            idMap.put(id,i);
        }



        return idMap;
    }

    @Contract(pure = true)
    private static int getOffset(@NotNull Constant type){
        int offset;
        switch (type) {
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
            default:
                offset = 16;
                break;
        }
        return offset;
    }

    @Contract(pure = true)
    private static Constant getSize(@NotNull Constant type){
        Constant size;
        switch (type) {
            case TYPE_1X1 :
                size = SIZE_1;
                break;
            case TYPE_2X2 :
                size = SIZE_4;
                break;
            case TYPE_4X4 :
                size = SIZE_16;
                break;
            case TYPE_8X8 :
                size = SIZE_64;
                break;
            default:
                size = SIZE_256;
                break;
        }
        return size;
    }
}
