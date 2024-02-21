package httpsserver.handler;

import com.sun.net.httpserver.HttpExchange;
import httpsserver.IO;
import httpsserver.db.services.DeviceService;
import httpsserver.db.services.ReadingService;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
  * Connection from: 192.168.0.102/192.168.0.102
    Content-type : image/jpeg
    Mac-address : 34:85:18:40:CD:8C
    Connection : close
    Host : 192.168.0.101
    Content-length : 21672
    Timestamp : 2023-12-27 11:03:49
 */


public class imageHandler extends GenericHandler{

    /**
     * Parse the timestamp to a filename for images.
     * @param ts
     * @return
     */
    public String ParseTimestamp(Timestamp ts) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        return ts.toLocalDateTime().format(formatter);   
    }

    @Override
    public void handle(HttpExchange ex) {
        IO.serverMessage("\n[IMAGE]");
        IO.serverMessage("Connection from: " + ex.getRemoteAddress().getAddress());

         /**
         * Get the POST headers
         */
        HashMap<String, String> headers = getHeaders(ex);
        headers.forEach((k, v) -> IO.serverMessage(k + " : " + v));
        String macAddress = headers.get("Mac-address");
        String stamp = headers.get("Timestamp");
        int size = getContentLength(headers);

        /**
         * TODO: Timestamp validation through length and separators.
         */
        Timestamp timestamp = Timestamp.valueOf(stamp);

        /**
         * Very simple MAC filtering. Stop processing the request.
         * TODO: log the error.
         */
        if (!DeviceService.exists(macAddress)) {
            IO.serverMessage("POST request received from unregistered device " + macAddress);
            ACK(ex);
            ex.close();
            return;
        }

        /**
         * Get the POST body.
         */
        IO.serverMessage(">> Getting Image packets. ");
        byte[] image = getImageBody(ex, size);
        if(image.length == 0) {
            IO.serverMessage("Couldn't read POST request.");
            EXPFAILED(ex);
        } else {
            IO.serverMessage(">> Got Image packets : -> " + String.valueOf(image.length) + " bytes.");
            ACK(ex);
             /**
             * Write image to file.
             */
            String filepath = "incoming/images/"+ParseTimestamp(timestamp) +".jpg";
            IO.writeImage(image.length, filepath, image);
        }

        ex.close();
    }
}