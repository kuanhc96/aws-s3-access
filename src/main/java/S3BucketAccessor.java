import com.amazonaws.auth.BasicAWSCredentials;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class S3BucketAccessor {

    public static final int[] HD = {1920, 1080};
    public static final int[] SMALL_RESOLUTION = {640, 480};
    public static final int[] MEDIUM_RESOLUTION = {1024, 780};
    public static final int[] LARGE_RESOLUTION = {1600, 1200};

    private static final String BUCKET_NAME = "demo-primary-kuanhc96"; // name of the target bucket on AWS

    private String imageName; // name of the image that will be accessed from the bucket

    private BasicAWSCredentials awsCredentials;
    private AmazonS3Client s3Client;

    private ImageResizer imageResizer; // The same resizer is kept so that past data is retained

    public S3BucketAccessor() {
        imageName = "";
        awsCredentials = new BasicAWSCredentials(Credentials.access_key_id, Credentials.secret_access_key);
        s3Client = new AmazonS3Client(awsCredentials);
        imageResizer = null;
    }

    // This method converts the BufferedImage that was specified by the user into a resized-BufferedImage.
    // The parameters specified determines the output image resolution.
    // TODO: Think about the location at which the new images are saved. Will the user be able to view them?
    public void resizeAndSaveImage(int width, int height) throws IOException{

        // The new imageResizer keeps track of a Map of resolutions and their corresponding images
        imageResizer = new ImageResizer(getImage());
        BufferedImage newImage = imageResizer.getResizedImage(width, height);
        try {
            File newImageFile = new File(imageName);
            if (imageName.endsWith(".png")) {
                ImageIO.write(newImage, "png", newImageFile);
            } else {
                ImageIO.write(newImage, "jpg", newImageFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: Overload the method so that the final resolution can be defined by a constant that represents a resolution
    //public void resizeAndSaveImage()

    // This method takes user input and fetches the image specified from the bucket.
    // The file that is fetched from the bucket is returned as a BufferedImage.
    private BufferedImage getImage() throws IOException {
        Scanner s = new Scanner(System.in);

        boolean illegalFile = true;
        while (illegalFile) {
            System.out.println("Input image key (.jpg or .png): ");
            imageName = s.next();
            if (imageName.endsWith(".jpg") || imageName.endsWith(".png")) {
                illegalFile = false;
            }
        }

        // The imageName that is specified is guaranteed to have a suffix of .png and .jpg

        GetObjectRequest request = new GetObjectRequest(BUCKET_NAME, imageName);
        S3Object fullObject = s3Client.getObject(request);
        InputStream objectStream = fullObject.getObjectContent(); // convert object fetched from bucket into InputStream
        BufferedImage image = ImageIO.read(objectStream); // convert InputStream into BufferedImage
        return image;
    }
}
