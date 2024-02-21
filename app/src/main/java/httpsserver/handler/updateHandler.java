package httpsserver.handler;

import java.util.HashMap;
import java.util.Map;
import java.nio.file.Path;
import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;
import httpsserver.IO;
import httpsserver.db.services.DeviceService;
import httpsserver.parser.Toml;

/**
 * Handler for updates to boards.
 *  X-esp32-free-space=1310720,
 *  X-esp32-ap-mac=36:85:18:41:EB:78,
 *  Connection=close,
 *  X-esp32-version=0.0.1,
 *  X-esp32-sketch-size=1006448,
 *  Host=192.168.0.105:8084,
 *  User-agent=ESP32-http-Update,
 *  X-esp32-sketch-sha256=8DFFB149E1859BD81A0DF868C868F7127257065ED234C8D58247C347D416D758,
 *  X-esp32-chip-size=8388608,
 *  X-esp32-sta-mac=34:85:18:41:EB:78,
 *  X-esp32-sdk-version=v4.4.5,
 *  X-esp32-sketch-md5=d16dcda8e86f2bb80a68480a84db6460,
 *  X-esp32-mode=sketch,
 *  Cache-control=no-cache
 */
public class updateHandler extends GenericHandler{

    String[] version;
    String path;
    String hash;


    private void NO_UPDATE(HttpExchange ex) {
        
        try {
           ex.sendResponseHeaders(304, -1);
        } catch (IOException e) {
            IO.errOut("Error with header order ->", e);
        }
    }


    private void UPDATE(HttpExchange ex, String path) {
        try {
            byte[] data = IO.readFile("../" + path, "Firmware file read but buffer is empty.");
            sendResponse(ex, data, 200);
        } catch (Throwable th) {
            IO.errOut("Error during reading firmware -> ", th);
        }
    }


    private void loadConfig() throws Throwable{
        Path tomlPath = Path.of("../firmware_cfg.toml");
        //IO.serverMessage("Using config file located at: " + tomlPath.toString());

        Map<String, Map<String, Object>> tomlData = Toml.parse(tomlPath);
        Map<String, Object> config = tomlData.get("firmware");

        this.path = (String) config.get("path");
        String temp = (String) config.get("version");
        this.version = temp.split("\\.");
        this.hash = (String) config.get("sha256");
    }


    private boolean needUpdate(String incoming) {
        String[] in = incoming.split("\\.");
        boolean __ = greater(this.version, in);
        if(__) IO.serverMessage("Update Needed!");
        return __;
    }


    private boolean greater(String[] out, String[] in) {
        int diff = in.length - out.length;
        if (diff > 0) padArray(diff, out);
        else if (diff < 0) padArray(0 - diff, in);

        try {
            for (int i = 0; i < in.length; i++) if (Integer.parseInt(out[i]) > Integer.parseInt(in[i])) return true;
        } catch (Throwable __) {
            return false;
        }

        return false;
    } 

    private void layout(String[] v) {
        for(String s : v) IO.serverMessageln(s + ".");
        IO.serverMessage("");
    }

    private void version() {
        IO.serverMessageln("Updated Version: ");
        layout(this.version);
    }


    private String[] padArray(int padding, String[] arr) {
        String[] newArr = new String[padding + arr.length];
        for(int i = 0; i < padding; i++) newArr[i] = "0";
        for(int j = padding; j < padding; j++) newArr[j] = arr[j - padding];
        return newArr;
    }


    @Override
    public void handle(HttpExchange ex) {

        try {
            loadConfig();
        } catch (Throwable e) {
            IO.errOut("Couldn't read firmware config ", e);
            NO_UPDATE(ex);
            ex.close();
            return;
        }

        IO.serverMessage("\n[UPDATE]");
        IO.serverMessage("Connection from: " + ex.getRemoteAddress().getAddress());

        /**
         * Get the POST headers
         * Should use this for logging.
         * String timestamp = getTime();
         */
        HashMap<String, String> headers = getHeaders(ex);
        String macAddress = headers.get("X-esp32-sta-mac");
        String ver = headers.get("X-esp32-version");
        String sha256 = headers.get("X-esp32-sketch-sha256");
        IO.serverMessage("Board Version: " + ver);
        version();
        

        /**
         * Very simple MAC filtering. Stop processing the request.
         * TODO: log the error.
         */
        if (!DeviceService.exists(macAddress)) {
            IO.serverMessage("POST request received from unregistered device " + macAddress);
            NO_UPDATE(ex);
            ex.close();
            return;
        }

        if(needUpdate(ver)) UPDATE(ex, this.path);
        else NO_UPDATE(ex);
        ex.close();
    }
}