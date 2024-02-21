package httpsserver.handler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.sun.net.httpserver.HttpExchange;

import httpsserver.IO;
import httpsserver.db.services.DeviceService;

public class GenericHandler implements com.sun.net.httpserver.HttpHandler{

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void EXPFAILED(HttpExchange ex) {
        sendResponse(ex, "EXPECTATION FAILED", 417);
    }

    public void TIMEOUT(HttpExchange ex) {
        sendResponse(ex, "TIMEOUT", 408);
    }

    public void PRECONDITION(HttpExchange ex) {
        sendResponse(ex, "PRECONDITION", 411);
    }

    public void ACK(HttpExchange ex) {
        sendResponse(ex, "ACK", 200);
    }
    public void UNAUTHORIZED(HttpExchange ex) {
        sendResponse(ex, "UNAUTH", 401);
    }

    protected void sendResponse(HttpExchange ex, String data, int statusCode) {
        sendResponse(ex, data.getBytes(), statusCode);
    }

    protected void sendResponse(HttpExchange ex, byte[] data, int statusCode) {
        try (OutputStream os = ex.getResponseBody()) {
            ex.sendResponseHeaders(statusCode, data.length);
            os.write(data);
            os.flush();
        } catch (Throwable th) {
            IO.errOut("Couldn't send response -> ", th);
        }
    }

    public HashMap<String, String> getHeaders(HttpExchange ex) {
        HashMap<String, String> headers = new HashMap<>();
        ex.getRequestHeaders().forEach((k, v) -> headers.put(k, v.get(0)));
        return headers;
    }

    public int getContentLength(HashMap<String, String> headers) {
        String imageSize = headers.get("Content-length");
        int size = 0;
        try {
            size = Integer.parseInt(imageSize);
        } catch (Throwable th) {
            IO.errOut("Couldn't get Content-length", th);
            size = 0;
        }

        return size;
    }

    public HashMap<String, String> getBody(HttpExchange ex) {
        HashMap<String, String> bodyParams = new HashMap<>();
        try {
            InputStreamReader isr = new InputStreamReader(ex.getRequestBody(), "utf-8");
            BufferedReader reader = new BufferedReader(isr);
            String line;
            StringBuilder bodyContent = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                bodyContent.append(line);
            }
            // Parse body content
            String[] params = bodyContent.toString().split("&");
            bodyParams = new HashMap<>();
            for (String param : params) {
                String[] parts = param.split("=");
                if (parts.length == 2) {
                    bodyParams.put(parts[0], parts[1]);
                }
            }
            return bodyParams;
        } catch (UnsupportedEncodingException e) {
            IO.errOut("Couldn't read POST request -> ", e);
            return bodyParams;
        } catch(IOException err) {
            IO.errOut("Error trying to close reader for POST request -> ", err);
            return bodyParams;
        }
    }

    public String getTime() {
        LocalDateTime now = LocalDateTime.now();
        String formattedTimestamp = now.format(FORMATTER);
        return formattedTimestamp;
    }

    protected boolean filterMac(HttpExchange ex, String macAddress) {
        if (DeviceService.exists(macAddress)) return false;
        
        IO.serverMessage("POST request received from unregistered device " + macAddress);
        ACK(ex);
        ex.close();
        return true;
    }

    public byte[] getImageBody(HttpExchange ex, int size) {
        byte[] buffer = new byte[size];
        int got = 0;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
        InputStream is = ex.getRequestBody()){
            int len;
            
            while (true) {
                IO.serverMessage("There's currently: " + is.available() + " bytes in the buffer");
                sleep(500);
            }

        } catch (Throwable e) {
            IO.errOut("Couldn't get image", e);
        }

        IO.serverMessage("Read " + got + " bytes from client");
        return buffer;
    }

    protected void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            IO.errOut("SLEEP GOT INTERRUPTED", e);
        }
    }


    @Override
    public void handle(HttpExchange ex) {
        return;
    }
}
