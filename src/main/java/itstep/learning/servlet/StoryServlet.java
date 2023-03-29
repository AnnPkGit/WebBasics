package itstep.learning.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.data.DataContext;
import itstep.learning.data.entity.Story;
import itstep.learning.model.StoryViewModel;
import itstep.learning.service.auth.IAuthService;
import itstep.learning.service.auth.SessionAuthService;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Console;
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.*;

import itstep.learning.data.entity.User;
import org.json.JSONObject;

@Singleton
public class StoryServlet extends HttpServlet {

    @Inject
    private IAuthService authService ;
    private final DataContext dataContext;

    @Inject
    public StoryServlet(DataContext dataContext) {
        this.dataContext = dataContext;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader( "Content-Type", "application/json" ) ;
        String taskId = req.getParameter("task-id");
        try {
            UUID.fromString(taskId);
        }
        catch (IllegalArgumentException ignored) {
            resp.setStatus(400);
            resp.getWriter().print("\"invalid param task-id\"");
            return;
        }

        List<Story> stories = dataContext.getStoryDao().getListByTask( taskId ) ;
        List<StoryViewModel> storyViewModels = new ArrayList<>() ;
        for( Story story : stories )
        {
            StoryViewModel model = new StoryViewModel(story,dataContext.getUserDao()
                    .getById( story.getIdUser() ),
                    null    ) ;
            if( story.getIdReply() != null )
            {
                Story reply = dataContext.getStoryDao().getById( story.getIdReply() ) ;
                model.setReplyStory( new StoryViewModel(reply, dataContext.getUserDao().getById( reply.getIdUser() ), null) ) ;
            }    storyViewModels.add( model ) ;}
        resp.getWriter().print(  //new Gson().toJson( stories ));
        new GsonBuilder().serializeNulls().create().toJson( storyViewModels )) ;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String message = this.addStory(req);
        JSONObject result = new JSONObject();
        result.put("status", "OK".equals(message) ? 200 : 400);
        result.put("message", message);
        resp.getWriter().print(result);
    }

    private String addStory(HttpServletRequest req) {
        Story story = new Story();
        User user = authService.getAuthUser();
        if(user == null) {
            return "Unauthorized";
        }
        story.setIdUser(user.getId());

        String param = req.getParameter("story-text");
        if(param == null) {
            return "Missing parameter story-text";
        }
        story.setContent(param);

        param = req.getParameter("story-id-task");
        if(param == null) {
            return "Missing parameter story-id-task";
        }
        try{
            story.setIdTask( UUID.fromString(param));
        }
        catch (IllegalArgumentException ignored) {
            return "Invalid value story-id-task";
        }

        return dataContext.getStoryDao().add(story) ? "OK" : "Inner error";
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session mailSession = Session.getInstance( prop ) ;
        mailSession.setDebug( true ) ; // log in console
        Transport transport ;
        try {
            MimeMessage message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress("blob.informer@gmail.com"));
            message.setSubject("Hello from Java");
            message.setContent("UNLINK method works", "text/plain");

            transport = mailSession.getTransport("smtp");
            transport.connect("smtp.gmail.com", "blob.informer@gmail.com", "ggusaagrihkilqkd");
            transport.sendMessage(message,InternetAddress.parse("blob.informer@gmail.com"));
            transport.close();
        } catch (MessagingException ex) {
            System.out.println(ex.getMessage());
        }

        resp.getWriter().print("PUT aoaoao");
    }
}
