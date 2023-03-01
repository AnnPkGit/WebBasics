package itstep.learning.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.data.dao.IUserDao;
import itstep.learning.data.entity.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class AuthServlet extends HttpServlet {
    private IUserDao userDao;

    @Inject public AuthServlet(IUserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String authLogin = req.getParameter("auth-login");
        String authPass = req.getParameter("auth-pass");

        if(authPass == null || authLogin == null ||
        authPass.equals("")|| authLogin.equals("")){
            resp.getWriter().print("NO");
        }
        else {
            resp.getWriter().print("OK");
            User user = userDao.getUser("bob", "f29bc698a9bc0810e1248686d55a1803");
            System.out.println(user.toString());
        }
    }
}
