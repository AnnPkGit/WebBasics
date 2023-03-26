package itstep.learning.ws;

import com.google.inject.Inject;
import com.google.inject.Injector;
import itstep.learning.data.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.lang.reflect.Field;

public class WebSocketConfigurator extends ServerEndpointConfig.Configurator {
    @Inject
    private static Injector injector;
    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        return injector.getInstance(endpointClass);
    }

    @Override
    public void modifyHandshake(
            ServerEndpointConfig sec,
            HandshakeRequest request,
            HandshakeResponse response) {
        super.modifyHandshake(sec, request, response);

        HttpServletRequest httpServletRequest = null;
        try {
            for(Field field :
                    request.getClass().getDeclaredFields()) {
                if(HttpServletRequest.class.isAssignableFrom(field.getType()) ) {
                    field.setAccessible(true);
                    httpServletRequest = (HttpServletRequest) field.get(request);
                }
                //Field requestField = request.getClass().getDeclaredField("request");
            }
        } catch (IllegalAccessException ex) {
            System.err.println("modifyHandshake" + ex.getMessage());
        }
        User authUser = null;
        if( httpServletRequest != null) {
            authUser = (User) httpServletRequest.getAttribute("authUser");
        }
        if(authUser != null) {
            sec.getUserProperties().put("authUser", authUser);
        }
        else {
            throw  new RuntimeException("Unauthorized User in websocket");
        }
    }
}

