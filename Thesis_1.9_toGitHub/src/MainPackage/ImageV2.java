package MainPackage;

import javafx.scene.image.Image;

/**Modified javafx.scene.image.Image class
 *
 * Added information for grid based rendering.
 * variable id : Key for the image used.
 * variable coordX,
 *           coordy : Place of the picture in the grid system.
 */
public class ImageV2 extends Image {

    private int id;

    private int coordX;
    private int coordY;

    /**
     * Standard constructor with the added functionality
     *
     * @param url : Image URL, inherited from super.
     * @param id : Set ID.
     * @param coordX ,
     * @param coordY : Set coordinates.
     * */
    ImageV2(String url, int id, int coordX, int coordY) {
        super(url);
        this.id = id;
        this.coordX = coordX;
        this.coordY = coordY;
    }

    /**
     * Copy constructor with the added funtionality
     *
     * @param image : Image to be copied.
     * */
    ImageV2(ImageV2 image){
        super(image.impl_getUrl());
        this.id = image.getId();
        this.coordX = image.getCoordX();
        this.coordY = image.getCoordY();
    }

    /**
     * Standard inherited constructor
     *
     * @param url : Image URL.
     * */
    public ImageV2(String url) {
        super(url);
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
