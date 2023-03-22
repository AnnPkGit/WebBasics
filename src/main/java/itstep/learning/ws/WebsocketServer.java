package itstep.learning.ws;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint(value="/chat")
public class WebsocketServer {
    private static final Set<Session> sessions =
            Collections.synchronizedSet(
                    new HashSet<>()
            );
    @OnOpen
    public void onOpen( Session session ) {
        sessions.add(session);
        sendToAll(String.format("User id=%s Enter chat",
                session.getId()));
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        sendToAll(String.format("User id=%s Sent a story: %s",
                session.getId(), message));
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        sendToAll(String.format("User id=%s Left chat",
                session.getId()));
    }

    @OnError
    public void onError (Throwable ex, Session session) {
        ex.printStackTrace();
    }

    private void sendToAll( String message ) {
        for(Session session : sessions) {
            try {
                session.getBasicRemote().sendText(message);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
