package itstep.learning.filter;

import com.google.inject.Singleton;
import step.learning.model.FormsModel;
import javax.servlet.*;
import java.io.IOException;

@Singleton
public class FormsFilter implements Filter {
    private FilterConfig filterConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig ;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        FormsModel model = (FormsModel) servletRequest.getAttribute("formsModel");
        if(model != null && model.getText() != null) {
            servletRequest
                    .getRequestDispatcher( "/formsprocessor" )
                    .forward( servletRequest, servletResponse ) ;
        }
        filterChain.doFilter( servletRequest, servletResponse ) ;
    }

    @Override
    public void destroy() { filterConfig = null ; }
}
