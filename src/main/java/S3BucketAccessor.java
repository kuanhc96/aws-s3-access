
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;



import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class S3BucketAccessor {

    public static final int[] HD = {1920, 1080};
    public static final int[] SMALL_RESOLUTION = {640, 480};
    public static final int[] MEDIUM_RESOLUTION = {1024, 780};
    public static final int[] LARGE_RESOLUTION = {1600, 1200};

    private static final String BUCKET_NAME = "kuanhc96-images"; // name of the target bucket on AWS

    private String imageName; // name of the image that will be accessed from the bucket

    //private BasicAWSCredentials awsCredentials;
    private final AmazonS3 s3Client;

    private ImageResizer imageResizer; // The same resizer is kept so that past data is retained

    public S3BucketAccessor() {
        imageName = "";
        //awsCredentials = new BasicAWSCredentials(Credentials.access_key_id, Credentials.secret_access_key);
        s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
        imageResizer = null;
    }

    // This method converts the BufferedImage that was specified by the user into a resized-BufferedImage.
    // The parameters specified determines the output image resolution.
    // TODO: Think about the location at which the new images are saved. Will the user be able to view them?
    public void resizeAndSaveImage(int width, int height) throws IOException{

        // The new imageResizer keeps track of a Map of resolutions and their corresponding images
        BufferedImage bufferedImage = getImage();
        imageResizer = new ImageResizer(bufferedImage);
        BufferedImage newImage = imageResizer.getResizedImage(width, height);
        try {
            //File resizedDirectory = new File("./images-from-bucket/resized");
            //resizedDirectory.mkdir();
            File newImageFile = new File("resized-" + imageName);

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
        BufferedImage bf = null;
        // The imageName that is specified is guaranteed to have a suffix of .png and .jpg
        System.out.format("Downloading %s from S3 bucket %s...\n", imageName, BUCKET_NAME);
        try {
            S3Object o = s3Client.getObject(BUCKET_NAME, imageName);
            S3ObjectInputStream s3is = o.getObjectContent();
            bf = ImageIO.read(s3is); // return BufferedImage

            // keep copy of original file from bucket
            //File resizedDirectory = new File("./images-from-bucket/originals");
            //resizedDirectory.mkdir();
            File originalImage = new File("original-size-" + imageName);
            try {
                if (imageName.endsWith(".png")) {
                    ImageIO.write(bf, "png", originalImage);

                } else {
                    ImageIO.write(bf, "jpg", originalImage);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        /*
        GetObjectRequest request = new GetObjectRequest(BUCKET_NAME, imageName);
        S3Object fullObject = s3Client.getObject(request);
        InputStream objectStream = fullObject.getObjectContent(); // convert object fetched from bucket into InputStream
        BufferedImage image = ImageIO.read(objectStream); // convert InputStream into BufferedImage
        return image;

         */
        return bf;

    }
}
