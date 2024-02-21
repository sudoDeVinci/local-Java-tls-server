package httpsserver.handler;

import java.sql.Timestamp;
import java.util.HashMap;
import com.sun.net.httpserver.HttpExchange;
import httpsserver.IO;
import httpsserver.db.services.DeviceService;
import httpsserver.db.services.SensorSuiteService;

/** Expecting a POST request in the form:
 * 
 * HEADER:
 * -------------------------------------
 * POST /<READINGPORT>  HTTP/1.1
 * Host: <IP>
 * Content-Type: application/x-www-form-urlencoded
 * Connection: close
 * Connection-Length: <LENGTH>
 * MAC-address: <MAC>
 * Timestamp: <Timestamp>
 * 
 * BODY:
 * 
 * sht=<bool>&bmp=<bool>&cam=<bool>
 */
public class statusHandler extends GenericHandler{
    
    @Override
    public void handle(HttpExchange ex) {
        
        IO.serverMessage("\n[STATUS]");
        IO.serverMessage("Connection from: " + ex.getRemoteAddress().getAddress());
       
        /**
         * Get the POST headers
         */
        HashMap<String, String> headers = getHeaders(ex);
        String macAddress = headers.get("Mac-address");
        String stamp = headers.get("Timestamp");
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
           // ACK(ex);
            ex.close();
            return;
        }
    
        /**
         * Get the POST body.
         */
        HashMap<String, String> bodyParams = getBody(ex);
        if(bodyParams.isEmpty()) {
            IO.serverMessage("Couldn't read POST request.");
            ex.close();
            /**
             * TODO: LOG errors.
             */
            return;
        }

        /**
         * Printout
         * headers.forEach((k, v) -> IO.serverMessage(k+": "+v));
         */
        bodyParams.forEach((k, v) -> IO.serverMessage(k+": "+v));

        /**
         * Send an ACK
         */
        ACK(ex);
        ex.close();
    }
}