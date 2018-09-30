package MainPackage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static MainPackage.PictureChooserController.projectFolder;
import static MainPackage.ThumbnailCreationController.sectionThumbnail;

public class ThumbnailCreater {

    public static void makeThumbnail(Image picture, int orgSize, String fileName){
        BufferedImage fullImg = SwingFXUtils.fromFXImage(picture,null);

        Runnable imgDownscale_1024X1024 = () -> {
            BufferedImage thumbnail_1024X1024 = Scalr.resize(fullImg, Scalr.Method.SPEED,orgSize/2);
            File thumbFile_1024X1024 = new File(projectFolder + "\\" + sectionThumbnail.getValue("THUMBNAIL_FOLDER_1024X1024_NAME") + "\\" + fileName);
            try{
                ImageIO.write(thumbnail_1024X1024,"jpg",thumbFile_1024X1024);
            }
            catch (IOException ex){
                System.err.println("IO exception at saving Thumbnail!");
            }
        };
        Runnable imgDownscale_512X512 = () -> {
            BufferedImage thumbnail_512X512 = Scalr.resize(fullImg, Scalr.Method.SPEED,orgSize/4);

            File thumbFile_512X512 = new File(projectFolder + "\\" + sectionThumbnail.getValue("THUMBNAIL_FOLDER_512X512_NAME") + "\\" + fileName);

            try{
                ImageIO.write(thumbnail_512X512,"jpg",thumbFile_512X512);
            }
            catch (IOException ex){
                System.err.println("IO exception at saving Thumbnail!");
            }
        };
        Runnable imgDownscale_256X256 = () -> {
            BufferedImage thumbnail_256X256 = Scalr.resize(fullImg, Scalr.Method.SPEED,orgSize/8);

            File thumbFile_256X256 = new File(projectFolder + "\\" + sectionThumbnail.getValue("THUMBNAIL_FOLDER_256X256_NAME") + "\\" + fileName);
            try{
                ImageIO.write(thumbnail_256X256,"jpg",thumbFile_256X256);
            }
            catch (IOException ex){
                System.err.println("IO exception at saving Thumbnail!");
            }
        };
        Runnable imgDownscale_128X128 = () -> {
            BufferedImage thumbnail_128X128 = Scalr.resize(fullImg, Scalr.Method.SPEED,orgSize/16);
            File thumbFile_128X128 = new File(projectFolder + "\\" + sectionThumbnail.getValue("THUMBNAIL_FOLDER_128X128_NAME") + "\\" + fileName);

            try{
                ImageIO.write(thumbnail_128X128,"jpg",thumbFile_128X128);
            }
            catch (IOException ex){
                System.err.println("IO exception at saving Thumbnail!");
            }
        };

        new Thread(imgDownscale_1024X1024).start();
        new Thread(imgDownscale_512X512).start();
        new Thread(imgDownscale_256X256).start();
        new Thread(imgDownscale_128X128).start();



    }
}
