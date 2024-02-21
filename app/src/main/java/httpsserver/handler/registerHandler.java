package httpsserver.handler;

import com.sun.net.httpserver.HttpExchange;
import httpsserver.IO;

/** Expected a Registration request in the form:
 * 
 * HEADER:
 * -------------------------------------
 * 
 *          [NOT IMPLEMENTED]
 * 
 */
public class registerHandler extends GenericHandler{
    @Override
    public void handle(HttpExchange exchange) {
        IO.serverMessage("\n[REGISTER]");
    }
}