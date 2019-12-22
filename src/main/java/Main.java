import java.io.IOException;
import java.util.*;

public class Main {

    public static final int[] HD = {1920, 1080};
    public static final int[] SMALL_RESOLUTION = {640, 480};
    public static final int[] MEDIUM_RESOLUTION = {1024, 780};
    public static final int[] LARGE_RESOLUTION = {1600, 1200};

    public static void main(String[] args) throws IOException {
        mainConsole();
    }

    private static void mainConsole() throws IOException {
        Scanner s = new Scanner(System.in);

        String yesNo = "y";
        while (yesNo.toLowerCase().startsWith("y")) {
            System.out.println("Welcome! This is a tool that uses the AWS S3 service to perform image decompression tasks");
            int serviceOption = 4;
            while (serviceOption > 3 || serviceOption < 1) {
                System.out.println("Please choose between the following options (1 - 3): ");
                System.out.println("(1) Upload an image to cloud " +
                        "(2) Download an image from cloud " +
                        "(3) Decompress an image from local drive");
                serviceOption = s.nextInt();
            }

            if (serviceOption == 1) { // upload image
                uploadImageToBucket();
            } else if (serviceOption == 2) { // download image
                getDownloadSizing();
            } else { // decompress image
                uploadImageToBucket();
                getDownloadSizing();
            }

            System.out.println("Would you like to continue? (Y/N)");
            yesNo = s.nextLine();
        }

        System.out.println("Thanks for trying out my tool!");

    }

    private static void getDownloadSizing() throws IOException {
        Scanner s = new Scanner(System.in);
        int imageOption = 6;
        while (imageOption > 5 || imageOption < 1) {
            System.out.println("Please choose from the following download options:");
            System.out.println("(1) Original (2) 640x480 (3) 1024x780 (4) 1600x1200 (5) Customize");
            imageOption = s.nextInt();
        }

        if (imageOption == 1) {
            getResizedImageFromBucket(0, 0);
        } else if (imageOption == 2) {
            getResizedImageFromBucket(SMALL_RESOLUTION[0], SMALL_RESOLUTION[1]);
        } else if (imageOption == 3) {
            getResizedImageFromBucket(MEDIUM_RESOLUTION[0], MEDIUM_RESOLUTION[1]);
        } else if (imageOption == 4) {
            getResizedImageFromBucket(LARGE_RESOLUTION[0], LARGE_RESOLUTION[1]);
        } else {
            int[] args = getArgs();
            getResizedImageFromBucket(args[0], args[1]);
        }
    }


    private static int[] getArgs() {
        int[] intArgs = new int[2];

        Scanner s = new Scanner(System.in);
        System.out.println("Input resized width: ");
        intArgs[0] = s.nextInt();
        System.out.println("Input resized height: ");
        intArgs[1] = s.nextInt();

        return intArgs;
    }

    private static void uploadImageToBucket() throws IOException {
        S3BucketPlacer placer = new S3BucketPlacer();
        placer.addImageToBucket();
    }

    private static void getResizedImageFromBucket(int width, int height) throws IOException {
        S3BucketAccessor accessor = new S3BucketAccessor();
        accessor.resizeAndSaveImage(width, height);
    }
}
