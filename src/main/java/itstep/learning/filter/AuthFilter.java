package itstep.learning.filter;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.service.auth.IAuthService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class AuthFilter implements Filter {
    @Inject
    IAuthService authService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
         HttpServletRequest request = (HttpServletRequest) servletRequest;
         System.out.println("Auth filter");
         if(servletRequest.getParameter("logout") != null) {
             authService.logout(request);
             HttpServletResponse response = (HttpServletResponse) servletResponse;
             ((response)).sendRedirect(request.getContextPath() + "/home");
             return;
         }
         authService.authorize( request);
         servletRequest.setAttribute("authUser", authService.getAuthUser());
         filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {}
}
