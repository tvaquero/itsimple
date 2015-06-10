package src.util.websocket;
import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

public class EmptyClient extends WebSocketClient {

    public EmptyClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public EmptyClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("new connection opened");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received message: " + message);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occured:" + ex);
    }

    public static void main(String[] args) throws URISyntaxException, JSONException {      
        WebSocketClient client = new EmptyClient(new URI("ws://bicycle.csail.mit.edu:9029/itsimple"), new Draft_10());
        client.connect();
        int timeout_counter = 0;
        while (!client.isOpen() || (timeout_counter > 100000)) {
			System.out.println("connecting");
			timeout_counter += 1;
		}
        System.out.println("Sending");
        JSONObject obj = new JSONObject();
        obj.put("command", "display_rmpl");
        obj.put("data", "I got you!");
        String message = obj.toString();
        client.send(message);
        client.close();
        
    }
}