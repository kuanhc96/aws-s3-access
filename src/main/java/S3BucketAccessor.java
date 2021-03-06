
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;



import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3BucketAccessor {

    public static final int[] HD = {1920, 1080};


    private static final String BUCKET_NAME = "kuanhc96-images"; // name of the target bucket on AWS

    private String imageName; // name of the image that will be accessed from the bucket

    private final AmazonS3 s3Client;

    private ImageResizer imageResizer; // The same resizer is kept so that past data is retained

    // Constructor: sets up the object to connect to bucket
    public S3BucketAccessor() {
        imageName = "";
        s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
        imageResizer = null;
    }

    // get the requested image from the bucket and resizes it into different resolutions.
    // One of two overloading methods;
    // Parameters needed: int width, int height -> dimensions of the resized image.
    public void processImage(int width, int height) throws IOException {
        BufferedImage bufferedImage = getImage();
        resizeAndSaveImage(width, height, bufferedImage);
    }

    // get the requested image from the bucket and resizes it into different resolutions.
    // One of two overloading methods; this method is only called when the user requests to resize images immediately
    // on the fly so that the image is uploaded to the bucket and then immediately downloaded locally.
    // Parameters needed: int width, int height -> dimensions of the resized image.
    //                    String name -> name of the file being downloaded.
    //                This field is provided through console input when the user specifies which file is to be processed
    public void processImage(int width, int height, String name) {
        BufferedImage bufferedImage = getImageFromCloud(name);
        imageName = name;
        resizeAndSaveImage(width, height, bufferedImage);
    }

    // Lists all the objects in the bucket as console output
    public void listObjectsInBucket() {
        System.out.format("Objects in S3 bucket %s:\n", BUCKET_NAME);
        ListObjectsV2Result result = s3Client.listObjectsV2(BUCKET_NAME);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        for (S3ObjectSummary os : objects) {
            System.out.println("* " + os.getKey());
        }

    }

    // This method converts the BufferedImage that was specified by the user into a resized-BufferedImage.
    // The parameters specified determines the output image resolution.
    private void resizeAndSaveImage(int width, int height, BufferedImage bufferedImage) {

        // if the parameters are 0, the user only requested original image.
        // therefore there is no need to resize.
        if (width != 0 || height != 0) {
            // The new imageResizer keeps track of a Map of resolutions and their corresponding images
            imageResizer = new ImageResizer(bufferedImage);
            BufferedImage newImage = imageResizer.getResizedImage(width, height);
            try {

                File newImageFile = new File("resized-" + width + "x" + height + "-" + imageName);

                if (imageName.endsWith(".png")) {
                    ImageIO.write(newImage, "png", newImageFile);
                } else {
                    ImageIO.write(newImage, "jpg", newImageFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    // This method takes user input and fetches the image specified from the bucket.
    // The file that is fetched from the bucket is returned as a BufferedImage.
    private BufferedImage getImage() {
        Scanner s = new Scanner(System.in);

        boolean illegalFile = true;
        while (illegalFile) {
            System.out.println("Input image key (.jpg or .png): ");
            imageName = s.next();
            if (imageName.endsWith(".jpg") || imageName.endsWith(".png")) {
                illegalFile = false;
            }
        }

        return getImageFromCloud(imageName);
    }

    // This method fetches the image specified as the parameter.
    // It is called only when the user specifies to process an image so that an image is uploaded and then
    // downloaded immediately
    private BufferedImage getImageFromCloud(String name) {
        BufferedImage bf = null;
        // The imageName that is specified is guaranteed to have a suffix of .png and .jpg
        System.out.format("Downloading %s from S3 bucket %s...\n", name, BUCKET_NAME);
        try {
            S3Object o = s3Client.getObject(BUCKET_NAME, name);
            S3ObjectInputStream s3is = o.getObjectContent();
            bf = ImageIO.read(s3is); // return BufferedImage

            File originalImage = new File("original-size-" + name);
            try {
                if (name.endsWith(".png")) {
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

        return bf;
    }

}
