package itstep.learning.ioc;

import com.google.inject.servlet.ServletModule;
import itstep.learning.filter.CharsetFilter;
import itstep.learning.filter.DbCheckFilter;
import itstep.learning.filter.FormsFilter;
import itstep.learning.servlet.AboutServlet;
import itstep.learning.servlet.FormsServlet;
import itstep.learning.servlet.HomeServlet;
import itstep.learning.servlet.UserRegisterServlet;

public class RouterModule extends ServletModule {
    @Override
    protected void configureServlets() {
        // задаем фильтры
        filter( "/*" ).through( CharsetFilter.class ) ;
        filter( "/*" ).through( FormsFilter.class ) ;
        filter( "/*" ).through( DbCheckFilter.class ) ;

        // и сервлеты
        serve( "/home"  ).with( HomeServlet.class  ) ;
        serve( "/forms" ).with( FormsServlet.class ) ;
        serve( "/about" ).with( AboutServlet.class ) ;
        serve( "/register" ).with( UserRegisterServlet.class ) ;
    }
}
