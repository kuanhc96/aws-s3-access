import com.amazonaws.auth.BasicAWSCredentials;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class S3BucketPlacer {
    private static final String BUCKET_NAME = "demo-primary-kuanhc96"; // name of the target bucket on AWS

    private String imageName; // name of the image that will be specified by the user later

    private BasicAWSCredentials awsCredentials;
    private AmazonS3Client s3Client;

    public S3BucketPlacer() {
        imageName = "";
        awsCredentials = new BasicAWSCredentials(Credentials.access_key_id, Credentials.secret_access_key);
        s3Client = new AmazonS3Client(awsCredentials);
    }

    // This method will add the File image that was specified by the user to the (unique) bucket demo-primary-kuanhc96
    public void addImageToBucket() throws IOException {
        File image = getImage();
        PutObjectRequest putRequest = new PutObjectRequest(BUCKET_NAME, imageName, image);
        PutObjectResult response = s3Client.putObject(putRequest);
        System.out.println("Uploaded object encryption status is " +
                response.getSSEAlgorithm());

    }

    // This method is used to find the image that is specified by the user.
    // It will read user input the name of a jpg or png and attempt to find the file.
    // If the file is found, a file object is created and returned.
    // TODO: What happens if the file specified is not found?
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

}
