import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class S3BucketPlacer {
    private static final String BUCKET_NAME = "kuanhc96-images"; // name of the target bucket on AWS

    private String imageName; // name of the image that will be specified by the user later

    private final AmazonS3 s3Client;

    // Constructor: setting up the tools needed to access the bucket
    public S3BucketPlacer() {
        imageName = "";
        s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
    }

    // This method will add the File image that was specified by the user to the (unique) bucket kuanhc96-images
    public void addImageToBucket() throws IOException {
        File image = getImage();


        System.out.format("Uploading %s to S3 bucket %s...\n", imageName, BUCKET_NAME);
        try {
            BufferedImage bImage = ImageIO.read(image);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            if (imageName.endsWith(".png")) {
                ImageIO.write(bImage, "png", os);
            } else {
                ImageIO.write(bImage, "jpg", os);
            }

            InputStream fis = new ByteArrayInputStream(os.toByteArray());

            byte[] contents = IOUtils.toByteArray(fis);
            InputStream stream = new ByteArrayInputStream(contents);

            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(contents.length);
            meta.setContentType("image/png");

            pruneImageName();

            s3Client.putObject(new PutObjectRequest(
                    BUCKET_NAME, imageName, stream, meta)
                    .withCannedAcl(CannedAccessControlList.Private));

            fis.close();

        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }

    public String getImageName() {
        return imageName;
    }

    // This method is used to find the image that is specified by the user.
    // It will read user input the name of a jpg or png and attempt to find the file.
    // If the file is found, a file object is created and returned.
    private File getImage() {
        Scanner s = new Scanner(System.in);

        boolean illegalFile = true;
        while (illegalFile) { // prompt user to input legal file name. Otherwise loop continues
            System.out.println("Input image directory (.jpg or .png): ");
            imageName = s.next();
            if (imageName.endsWith(".jpg") || imageName.endsWith(".png")) { // legal file names include either jpg or png
                illegalFile = false;
            }
        }

        File file = new File(imageName);
        return file;
    }

    // parse image name so that it appears as a file with no encapsulating folders outside of it in the bucket
    private void pruneImageName() {
        int i = imageName.length() - 1; // last character of the string
        String pruned = "";

        // delete everything before the '/' in the file path.
        // If the file path is simply the file name, then it will return the file name.
        while (imageName.charAt(i) != '/' && i >= 0) {
            pruned = imageName.charAt(i) + pruned;
            i--;
        }
        imageName = pruned;
    }

}
