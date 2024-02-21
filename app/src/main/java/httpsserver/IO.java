package httpsserver;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class to handle IO, including FileIO
 */
public class IO {

    public static final String[] headers = {"image size","name", "temperature", "humidity", "pressure", "dewpoint"};
    public static final String[] units = {"bytes","","\u00B0C","% Rh","hPA","\u00B0C"};
    public static final String separator = "------------------------------";

    /**
     * 
     */
    public String[] getHeaders() {
        return headers;
    }

    /**
     * Print a stylized usage message. 
    */
    public static void printUsage() {
        System.err.println("Usage: java Server <port>");
    }


    /**
     * Ensure a folder for readings/images exists
     * @param path Path to folder
     */
    public static void ensureFolderExists(String path) {
        Path folder = Paths.get(path);
        if (Files.exists(folder) && Files.isDirectory(folder)) {
           return;
        }

        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            errOut("Error creating folder: ", e);
        }
    }

    /**
     * Write jpeg image from buffer. Return true/false.
     * @param imageLength Lenght of image buffer in bytes
     * @param imageName Path to image
     * @param buffer Image byte buffer
     * @return
     */
    public static void writeImage(int imageLength, String imageName, byte[] buffer) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(imageName);
            fileOutputStream.write(buffer);
            fileOutputStream.close();
            serverMessage("Image saved as: " + imageName);
        } catch (Throwable err) {
            errOut("Couldn't save image " + imageName, err);
            // TODO: write to error logs.
        }
    }

    /**
     * Write the rrror to the logs for today with a timestamp.
     * @param num
     */
    private static void writeError(String datestamp, String timestamp, String err) {
        // TODO
    }


    /**
     * Validate string is an int with given bounds.
     * Alias given to number for printout.
     * @param num
     * @param lb lower bound
     * @param ub upper bound
     * @param alias alias to call string in printouts
     * @return
     */
    public static int stringToBoundedInt(String num, int lb, int ub, String alias) {
        int port;
        try {
            port = Integer.parseInt(num);
            if (port < lb || port > ub) {
                throw new IllegalArgumentException(" ");
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid " + alias + " number: " + num);
            port = -1;
        } catch (IllegalArgumentException e) {
            System.err.println(alias + " number must be between " + lb + " and " + ub);
            port = -1;
        }    

        return port;
    }

    /**
     * Convert a string to a positive integer.
     * If negative, or not an int, return -1
     * @param str String to convert
     * @return -1 or integer.
     */
    public static int stringToInt(String str) {
        int num;
        try {
            num = Integer.parseInt(str);
            if (num < 0) {
                num = -1;
            }
        } catch (NumberFormatException e) {
            // System.err.println("Invalid number: " + str);
            num = -1;
        }
        return num;
    }

    /**
     * Convert a string to a positive floating point.
     * If negative, or not a float, return -1
     * @param str String to convert
     * @return -1 or float.
     */
    public static float stringToFloat(String str) {
        float num;
        try {
            num = Float.parseFloat(str);
            if ( num < 0) {
                num = -1;
            }
        } catch (NumberFormatException e) {
            //System.err.println("Invalid number: " + str);
            num = -1;
        }
        return num;
    }

    /**
     * Print stylized server event message.
     * @param messageMessage to print
     */
    public static void serverMessage(String message) {
        System.out.println(message);
    }

    /**
     * Print stylized server event message.
     * @param messageMessage to print
     */
    public static void serverMessageln(String message) {
        System.out.print(message);
    }


    /**
     * Print stylized errors to user.
     * @param err Error to stylize.
     */
    public static void loggedErrOut(String message, Throwable err, String datestamp, String timestamp) {
        System.out.print(message);
        System.out.println(err.getMessage());
        //TODO: write error.
    }

    
     /**
     * Print stylized errors to user.
     * @param err Error to stylize.
     */
    public static void errOut(String message, Throwable err) {
        System.out.print(message);
        System.out.println(err.getMessage());
    }

    public static File locateFileRelativeToClass(String relativePath, Class<?> clazz) {
    // Use the class's class loader to get the resource URL
    URL url = clazz.getResource(relativePath);

    if (url != null) {
        try {
            // Convert the URL to a file path
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    // Return null if the file cannot be located
    return null; 
    }

    public static String py() {
        String osName = System.getProperty("os.name").toLowerCase();

        String KEYWORD = 
            osName.contains("windows") ? "python" :
            osName.contains("mac") ? "python3" :
            osName.contains("nix") || osName.contains("nux") ? "python3" :
            "python";

        return KEYWORD;
        
    }

    public static byte[] readFile(String path, String emptyMsg) throws Throwable {
        String err;
        try {
            File file = new File(path);
            byte[] data = Files.readAllBytes(file.toPath());
            if (data.length == 0) throw new Throwable(emptyMsg);
            return data;
        } catch (FileNotFoundException e) {
            err = "Couldn't find file:-> " + path;
        } catch (IOException ex) {
            err = "Couldn't open file:-> " + ex.getMessage();
        }

        throw new Throwable(err);
    }
}


