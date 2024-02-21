package httpsserver;

import com.sun.net.httpserver.*;

import httpsserver.db.ConfigManager;
import httpsserver.db.DatabaseManager;
import httpsserver.handler.imageHandler;
import httpsserver.handler.readingHandler;
import httpsserver.handler.registerHandler;
import httpsserver.handler.statusHandler;
import httpsserver.handler.testHandler;
import httpsserver.handler.updateHandler;
import httpsserver.parser.Toml;

import javax.net.ssl.*;

import java.io.FileInputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    final String READING = "/reading";
    final String REGISTER = "/register";
    final String STATUS = "/status";
    final String IMAGE = "/images";
    final String UPDATE = "/updates";
    final String TEST = "/test";
    final int MAINPORT = 8080;
    final int ALTPORT = 8085; 
    final String ADDR = getLocalIP();

    Map<String, HttpHandler> handlerMap = new HashMap<>();

    public Server() {

        handlerMap.put(READING, new readingHandler());
        handlerMap.put(REGISTER, new registerHandler());
        handlerMap.put(STATUS, new statusHandler());
        handlerMap.put(IMAGE, new imageHandler());
        handlerMap.put(UPDATE, new updateHandler());
        handlerMap.put(TEST, new testHandler());
    }


    /**
     * A reliable way to get the current Network interface IPv4 address.
     * This solution is windows-only.
     * @return The ip address of the machine.
     */
    private String getLocalIP() { 
        String ip = "";
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        } catch(Exception e) {
            IO.errOut("Couldn't get local ip: ", e);
        }
        return ip;
    }

    /**
     * Wrapper for server config info during boot.
     * TODO: Things like the type and algorithm REALLY should be in an enum. This is just to get a working copy for now.
     */
    private class ServerConf {
        public String name, pass, type, algorithm;

        public void load() {
            try {
                Path tomlPath = Path.of("../keys_cfg.toml");
                IO.serverMessage("Using config file located at: " + tomlPath.toString());
    
                Map<String, Map<String, Object>> tomlData = Toml.parse(tomlPath);
                Map<String, Object> mysqlConfig = tomlData.get("keystore");
                // Extract and initialize the static fields
                this.name = (String) mysqlConfig.get("name") == null ? "" : (String) mysqlConfig.get("name");
                this.pass = (String) mysqlConfig.get("pass") == null ? "" : (String) mysqlConfig.get("pass");
                this.type = (String) mysqlConfig.get("type") == null ? "" : (String) mysqlConfig.get("type");
                this.algorithm = (String) mysqlConfig.get("algorithm") == null ? "" : (String) mysqlConfig.get("algorithm");
            } catch (Exception e) {
                throw new RuntimeException("Couldn't read schema config.");
            }
        }
    }


    /**
     * Start servers on main and aternate ports.
     */
    public void start() {
        start(ADDR, MAINPORT);
        start(ADDR, ALTPORT);
    }

    /**
     * @param port
     * @param IP
     * @param handler
     */
    private void start(String IP, int PORT) {
        try {

            // Load the server config info.
            ServerConf config = new ServerConf();
            config.load();

            // setup the socket address
            InetSocketAddress address = new InetSocketAddress(IP, PORT);

            // initialise the HTTPS server
            HttpsServer httpsServer = HttpsServer.create(address, 0);
            SSLContext sslContext = SSLContext.getInstance("TLS");

            // initialise the keystore
            char[] password = config.pass.toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream(Path.of("../"+config.name).toAbsolutePath().toString());
            ks.load(fis, password);

            // setup the key manager factory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(config.algorithm);
            kmf.init(ks, password);

            // setup the trust manager factory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(config.algorithm);
            tmf.init(ks);

            // setup the HTTPS context and parameters
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                public void configure(HttpsParameters params) {
                    try {
                        // initialise the SSL context
                        SSLContext context = getSSLContext();
                        SSLEngine engine = context.createSSLEngine();
                        params.setNeedClientAuth(false);
                        params.setCipherSuites(engine.getEnabledCipherSuites());
                        params.setProtocols(engine.getEnabledProtocols());

                        // Set the SSL parameters
                        SSLParameters sslParameters = context.getSupportedSSLParameters();
                        params.setSSLParameters(sslParameters);

                    } catch (Exception ex) {
                        System.out.println("Failed to create HTTPS context");
                    }
                }
            });

            for(String route: this.handlerMap.keySet()) {
                HttpContext con = httpsServer.createContext(route);
                con.setHandler(this.handlerMap.get(route));
            }

            httpsServer.setExecutor(null);
            httpsServer.start();
            IO.serverMessage("Created HTTPS server on " + IP + " : " + PORT + "\n");

        } catch (Throwable e) {
            IO.errOut("Failed to create HTTPS server on " + IP + " : " + PORT + "\n", e);
        }
    }

     
    public static void main(String[] args) {
        
        List<String> argList = Arrays.asList(args);
        ConfigManager.load();

        IO.ensureFolderExists("incoming/");
        IO.ensureFolderExists("incoming/images/");

        try {
            DatabaseManager.connect(argList.contains("drop"));

        } catch(RuntimeException ex) {
            IO.errOut("Error starting server", ex);
        }

        try {
            Server server = new Server();
            server.start();
        } catch (Throwable e) {
            IO.errOut("Error Starting the server:-> ", e);
        }
        
    }
}


