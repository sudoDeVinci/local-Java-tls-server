package httpsserver.handler;

import java.sql.Timestamp;
import java.util.HashMap;
import com.sun.net.httpserver.HttpExchange;
import httpsserver.IO;
import httpsserver.db.services.DeviceService;
import httpsserver.db.services.ReadingService;


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
 * temperature=<temp>&humidity=<hum>&pressure=<pres>&dewpoint=<dew>
 */
public class readingHandler extends GenericHandler{
    
    @Override
    public void handle(HttpExchange ex) {
        String humidity = "";
        String temperature = "";
        String pressure = "";
        String dewpoint = "";
        IO.serverMessage("\n[READING]");
        IO.serverMessage("Connection from: " + ex.getRemoteAddress().getAddress());
        
        /**
         * Get the POST headers
         */
        HashMap<String, String> headers = getHeaders(ex);
        //String method = headers.get("Method");
        //String uri = headers.get("URI");
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
            ACK(ex);
            ex.close();
            return;
        }
        

        /**
         * Get the POST body.
         */
        HashMap<String, String> bodyParams = getBody(ex);
        if(bodyParams.isEmpty()) {
            IO.serverMessage("Couldn't read POST request.");
            //ACK(ex);
            ex.close();
            return;
        }

        bodyParams.forEach((k, v) -> IO.serverMessage(k+": "+v));
        
        ACK(ex);
        ex.close();
    }
}

