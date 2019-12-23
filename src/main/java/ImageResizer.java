import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
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
    private Map<Integer[], BufferedImage> resolutionImageMap;
    private BufferedImage originalImage;
    private int imageType;

    public ImageResizer(BufferedImage originalImage) {
        resolutionImageMap = new HashMap<Integer[], BufferedImage>();
        resolutionImageMap.put(new Integer[] {originalImage.getWidth(), originalImage.getHeight()}, originalImage);
        this.originalImage = originalImage;
        imageType = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
    }

    // This method searches the resolutions map to see if a resized BufferedImage was created before.
    // If a resized BufferedImage at the specified resolution was not created before then it will be created on the fly.
    // otherwise it will simply return the resized BufferedImage.
    public BufferedImage getResizedImage(int imageWidth, int imageHeight) {
        if (containsImage(imageWidth, imageHeight)) {
            Integer[] dimensions = {imageWidth, imageHeight};
            return resolutionImageMap.get(dimensions);
        } else {
            return resizeImage(imageWidth, imageHeight);
        }
    }

    // This method creates a new resized BufferedImage on the fly.
    // It is called only when a resolution that was specified does not exist in the resolution map.
    // After the resized BufferedImage is created, it is put into the resolution map.
    private BufferedImage resizeImage(int imageWidth, int imageHeight) {
        BufferedImage resizedImage = new BufferedImage(imageWidth, imageHeight, imageType);
        Graphics2D g = resizedImage.createGraphics(); // create graphics on the resized "canvas"
        g.drawImage(originalImage, 0, 0, imageWidth, imageHeight, null);
        g.dispose();

        Integer[] resizedDimensions = {imageWidth, imageHeight};
        // Keep track of the resized-BufferedImages that were created before
        resolutionImageMap.put(resizedDimensions, resizedImage);

        return resizedImage;
    }


    // This is a simple method that checks whether or not a resizing resolution has been specified before by the user.
    // Returns true if a resizing resolution has been specified before.
    // Otherwise returns false.
    private boolean containsImage(int imageWidth, int imageHeight) {
        Integer[] dimensions = {imageWidth, imageHeight};
        return resolutionImageMap.keySet().contains(dimensions);
    }


}
