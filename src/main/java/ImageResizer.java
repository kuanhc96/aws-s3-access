import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.*;

// Images are obtained from an S3 bucket.
// Each ImageResizer object is associated with exactly one image.
// The original image will be stored as a class constant in this class,
// as well as the resized images of various sizes.
// The original image can be accessed through accessor methods,
// whereas the resize images will be left as null objects until that specific size is needed
// i.e., images of other resolutions are created "on the fly".

public class ImageResizer {
    // minimum resolutions needed: 640 x 480, 1024 x 768 and 1600 x 1200. Original HD resolution: 1920 x 1080

    private static final int[] HD = {1920, 1080};
    private static final int[] SMALL_RESOLUTION = {640, 480};
    private static final int[] MEDIUM_RESOLUTION = {1024, 780};
    private static final int[] LARGE_RESOLUTION = {1600, 1200};

    private Map<Integer[], BufferedImage> resolutionImageMap;
    private BufferedImage originalImage;
    private int[] originalResolution;
    private int[] requestedResolution;
    private BufferedImage requestedImage;
    private int imageType;

    public ImageResizer(BufferedImage originalImage) {
        resolutionImageMap.put(new Integer[] {originalImage.getWidth(), originalImage.getHeight()}, originalImage);
        this.originalImage = originalImage;
        requestedResolution = null;
        requestedImage = null;
        imageType = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
        originalResolution = new int[] {originalImage.getWidth(), originalImage.getHeight()};
    }

    private BufferedImage resizeImage(int imageWidth, int imageHeight) {
        BufferedImage resizedImage = new BufferedImage(imageWidth, imageHeight, imageType);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, imageWidth, imageHeight, null);
        g.dispose();
        return resizedImage;
    }
}
