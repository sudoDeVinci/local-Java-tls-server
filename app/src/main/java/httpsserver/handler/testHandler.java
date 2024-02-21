package httpsserver.handler;

import java.io.IOException;
import java.util.HashMap;
import com.sun.net.httpserver.HttpExchange;
import httpsserver.IO;

import httpsserver.db.services.DeviceService;

public class testHandler extends GenericHandler{
    
    @Override
    protected boolean filterMac(HttpExchange ex, String macAddress) {
        if (DeviceService.exists(macAddress)) return false;
        
        IO.serverMessage("POST request received from unregistered device " + macAddress);
        ACK(ex);
        ex.close();
        return true;
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

        /**
         * Very simple MAC filtering. Stop processing the request.
         * TODO: log the error.
         */
        if (filterMac(ex, macAddress)) return;

        /*
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
         * Printout body
         */
        bodyParams.forEach((k, v) -> IO.serverMessage(k+": "+v));

        TIMEOUT(ex);
        ex.close();
    }
}
