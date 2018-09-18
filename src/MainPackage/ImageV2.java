package MainPackage;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.ArrayList;

public class ImageV2 extends Image {

    private int id;
    private int coordX;
    private int coordY;
    private static String genericName;
    private static String format;

    public ImageV2(String url, int id, int coordX, int coordY) {
        super(url);
        this.id = id;
        this.coordX = coordX;
        this.coordY = coordY;
    }

    public ImageV2(ImageV2 image){
        super(image.getUrl());
        this.id = image.getId();
        this.coordX = image.getCoordX();
        this.coordY = image.getCoordY();
    }

    public ImageV2(String url) {
        super(url);
    }

    public ImageV2(String url, boolean backgroundLoading) {
        super(url, backgroundLoading);
    }

    public ImageV2(String url, double requestedWidth, double requestedHeight, boolean preserveRatio, boolean smooth) {
        super(url, requestedWidth, requestedHeight, preserveRatio, smooth);
    }

    public ImageV2(String url, double requestedWidth, double requestedHeight, boolean preserveRatio, boolean smooth, boolean backgroundLoading) {
        super(url, requestedWidth, requestedHeight, preserveRatio, smooth, backgroundLoading);
    }

    public ImageV2(InputStream is) {
        super(is);
    }

    public ImageV2(InputStream is, double requestedWidth, double requestedHeight, boolean preserveRatio, boolean smooth) {
        super(is, requestedWidth, requestedHeight, preserveRatio, smooth);
    }

    public static void setGenericName(String newName){
        genericName = newName;
    }
    public static void setFormat(String newFormat){
        format = newFormat;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCoordX(int coordX) {
        this.coordX = coordX;
    }

    public void setCoordY(int coordY) {
        this.coordY = coordY;
    }

    public int getId() {
        return id;
    }
    public int getCoordX() {
        return coordX;
    }
    public int getCoordY() {
        return coordY;
    }
    public static String getGenericName() {
        return genericName;
    }
    public static String getFormat() {
        return format;
    }

    public static ImageV2 createEmpty(){
        return new ImageV2("",-1,-1,-1);
    }

    public void makeEmpty(){
        this.id = -1;
        this.coordX = -1;
        this.coordY = -1;
    }

    public boolean exists(){

        return this.getCoordX() != -1 && this.getCoordY() != -1 && this.getId() != -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageV2 imageV2 = (ImageV2) o;

        if (id != imageV2.id) return false;
        if (coordX != imageV2.coordX) return false;
        return coordY == imageV2.coordY;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + coordX;
        result = 31 * result + coordY;
        return result;
    }

}
