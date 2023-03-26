package itstep.learning.ioc;

import com.google.inject.AbstractModule;
import itstep.learning.ws.WebSocketConfigurator;

public class WebSocketModule extends AbstractModule {
    @Override
    protected void configure() {
        requestStaticInjection(WebSocketConfigurator.class);
    }
}
