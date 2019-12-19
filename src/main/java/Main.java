import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        int[] intArgs = getArgs(args);
        uploadImageToBucket();
        getResizedImageFromBucket(intArgs[0], intArgs[1]);
    }

    private static int[] getArgs(String[] args) {
        int[] intArgs = new int[2];
        if (args.length == 2) { // parse command-line arguments
            intArgs[0] = Integer.parseInt(args[0]);
            intArgs[1] = Integer.parseInt(args[1]);

        } else {
            Scanner s = new Scanner(System.in);
            System.out.println("Input resized width: ");
            intArgs[0] = s.nextInt();
            System.out.println("Input resized height: ");
            intArgs[1] = s.nextInt();
        }
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
