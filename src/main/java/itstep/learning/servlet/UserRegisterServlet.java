package itstep.learning.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.data.dao.IUserDao;
import itstep.learning.data.dao.UserDao;
import itstep.learning.model.UserModel;
import itstep.learning.service.HashService;
import itstep.learning.service.UploadService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Singleton
public class UserRegisterServlet extends HttpServlet {

    @Inject
    UploadService uploadService;

    @Inject
    HashService hashService;

    private IUserDao userDao;

    @Inject
    public UserRegisterServlet(IUserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        req.getRequestDispatcher("WEB-INF/reg-user.jsp")
//                .forward(req, resp);

        req.setAttribute("viewName", "reg-user");
        HttpSession session = req.getSession();
        String regMessage = (String) session.getAttribute("reg-message");
        if(regMessage != null) {
            req.setAttribute("reg-message", regMessage);
            session.removeAttribute("reg-message");
        }
        req.getRequestDispatcher("WEB-INF/_layout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String regMessage;
        try {
            UserModel model = parseModel(req);
            validateModel(model);
            if(userDao.add(model)){
                regMessage = "OK";
                System.out.println(regMessage);
            }
            else {
                regMessage = "Add fail";
                System.err.println(regMessage);
            }
        }
        catch (IllegalArgumentException ex) {
            regMessage = ex.getMessage();
            System.err.println(ex.getMessage());
        }
        HttpSession session = req.getSession();
        session.setAttribute("reg-message", regMessage);
        resp.sendRedirect(req.getRequestURI());
    }

    private UserModel parseModel(HttpServletRequest req) {
        try {
            Map<String, FileItem> parameters = uploadService.parse( req ) ;
            UserModel model = new UserModel();
            model.setLogin( parameters.get( "user-login" ).getString() ) ;
            model.setPass1( parameters.get( "user-pass1" ).getString() ) ;
            model.setPass2( parameters.get( "user-pass2" ).getString() ) ;
            model.setName(  parameters.get( "user-name"  ).getString() ) ;
            model.setEmail( parameters.get( "user-email" ).getString() ) ;
            FileItem avatar = parameters.get("user-avatar");
            if(!avatar.isFormField()) {
                if(avatar.getSize() > 0) {
                    String filename = avatar.getName();
                    int dotPosition = filename.lastIndexOf('.');
                    String extension = filename.substring(dotPosition);
                    String savedName = hashService.getHexHash("" + System.nanoTime()) + extension;
                    String path = req.getServletContext().getRealPath("/") + "../Avatars/";
                    File file;
                    do {
                        savedName = hashService.getHexHash(savedName + System.nanoTime() + extension);
                        file = new File(path, savedName);
                    }while(file.exists());
                    avatar.write(file);
                    model.setAvatar(savedName);
                }
            }
            return model;
//            for( String key : parameters.keySet() ) {
//                FileItem item = parameters.get(key);
//                System.out.println(key + " " +
//                        (item.isFormField() ?  "form" + item.getString()
//                                :"file" + item.getName()));
//            }
        }
        catch (FileUploadException ex) {
            System.err.println(ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void validateModel(UserModel model) throws IllegalArgumentException {
        String message = null;
        if(model == null)  message = "Missing all parameters" ;
        else if(model.getLogin() == null) message = "Missing parameter: user-login" ;
        else {
            String login = model.getLogin();
            if("".equals(login)) message = "login should not be empty";
            else {
                // if its in use check
            }
        }
        if(message == null) {
            String pass1 = model.getPass1();
            String pass2 = model.getPass2();
            if( pass1 == null || pass2 == null) {
                message = "Missing parameter: user-passwords";
            }
            else {
                if(pass1.length() < 3) message = "Password too short" ;
                if(!pass1.equals(pass2)) message = "Passwords mismatch" ;
            }
        }
        if(message != null)
            throw new IllegalArgumentException(message);
    }
}
