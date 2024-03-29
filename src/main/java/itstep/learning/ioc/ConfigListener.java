package itstep.learning.ioc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class ConfigListener extends GuiceServletContextListener {
    @Override                              // Listener (ServletContextListener) - обработчик
    protected Injector getInjector() {     // события сервера ServletContext Create - первого
        return Guice.createInjector(       // события обработки сайта (до фильтров, до сервлетов).
                new RouterModule(),        // Внедряется через web.xml и создает Injector -
                new ServiceModule(),
                new StringModule(),
                new LoggerModule(),
                new WebSocketModule()
                                           // точку управления внедрением зависимостей.
        ) ;                                // Традиционно модули разделяют на два (и более) -
    }                                      // отдельно конфигурация фильтров/сервлетов (RouterModule),
}                                          // отдельно служб (реализаций интерфейсов) (ServiceModule)
