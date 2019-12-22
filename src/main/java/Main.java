import java.io.IOException;
import java.util.*;

public class Main {

    public static final int[] HD = {1920, 1080};
    private static final int[] SMALL_RESOLUTION = {640, 480};
    private static final int[] MEDIUM_RESOLUTION = {1024, 780};
    private static final int[] LARGE_RESOLUTION = {1600, 1200};

    private static final S3BucketPlacer PLACER = new S3BucketPlacer();
    private static final S3BucketAccessor ACCESSOR = new S3BucketAccessor();

    public static void main(String[] args) throws IOException {
        mainConsole();
    }

    private static void mainConsole() throws IOException {
        Scanner s = new Scanner(System.in);

        String yesNo = "y";
        System.out.println("Welcome! This is a tool that uses the AWS S3 service to perform image decompression tasks");
        while (yesNo.toLowerCase().startsWith("y")) {
            int serviceOption = 5;
            while (serviceOption > 4 || serviceOption < 1) {
                System.out.println("Please choose between the following options: (1 - 3)");
                System.out.println("(1) Upload an image to cloud \n" +
                        "(2) Download an image from cloud \n" +
                        "(3) Decompress an image from local drive \n" +
                        "(4) List objects that are currently in bucket");
                serviceOption = s.nextInt();
            }

            if (serviceOption == 1) { // upload image
                uploadImageToBucket();
            } else if (serviceOption == 2) { // download image
                getDownloadSizing();
            } else if (serviceOption == 3) { // decompress image
                String name = uploadImageToBucket();
                getDownloadSizing(name);
            } else {
                listObjectsInBucket();
            }

            System.out.println("Would you like to continue? (Y/N)");
            yesNo = s.next();
        }

        System.out.println("Thanks for trying out my tool!");

    }

    private static void getDownloadSizing(String name) throws IOException {
        int imageOption = getUserImagingOptions();

        if (imageOption == 1) {
            getResizedImageFromBucket(0, 0, name);
        } else if (imageOption == 2) {
            getResizedImageFromBucket(SMALL_RESOLUTION[0], SMALL_RESOLUTION[1], name);
        } else if (imageOption == 3) {
            getResizedImageFromBucket(MEDIUM_RESOLUTION[0], MEDIUM_RESOLUTION[1], name);
        } else if (imageOption == 4) {
            getResizedImageFromBucket(LARGE_RESOLUTION[0], LARGE_RESOLUTION[1], name);
        } else {
            int[] args = getArgs();
            getResizedImageFromBucket(args[0], args[1], name);
        }
    }

    private static void getDownloadSizing() throws IOException {
        int imageOption = getUserImagingOptions();

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

    private static int getUserImagingOptions() {
        Scanner s = new Scanner(System.in);
        int imageOption = 6;
        while (imageOption > 5 || imageOption < 1) {
            System.out.println("Please choose from the following download options: (1 - 5)");
            System.out.println("(1) Original (2) 640x480 (3) 1024x780 (4) 1600x1200 (5) Customize");
            imageOption = s.nextInt();
        }
        return imageOption;
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

    private static String uploadImageToBucket() throws IOException {
        PLACER.addImageToBucket();
        return PLACER.getImageName();
    }

    private static void getResizedImageFromBucket(int width, int height) throws IOException {
        ACCESSOR.processImage(width, height);
    }

    private static void getResizedImageFromBucket(int width, int height, String name) throws IOException {
        ACCESSOR.processImage(width, height, name);
    }

    private static void listObjectsInBucket() {
        ACCESSOR.listObjectsInBucket();
    }

}
