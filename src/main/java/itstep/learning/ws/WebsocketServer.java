package itstep.learning.ws;

import com.google.gson.Gson;
import com.google.inject.Inject;
import itstep.learning.data.DataContext;
import itstep.learning.data.entity.Story;
import itstep.learning.data.entity.User;
import itstep.learning.model.StoryViewModel;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@ServerEndpoint(value="/chat", configurator = WebSocketConfigurator.class)
public class WebsocketServer {
    private final DataContext dataContext;
    private static final Set<Session> sessions =
            Collections.synchronizedSet(
                    new HashSet<>()
            );

    private static final Gson gson = new Gson();

    @Inject
    public WebsocketServer(DataContext dataContext) {
        this.dataContext = dataContext;
    }

    @OnOpen
    public void onOpen( Session session, EndpointConfig configurator ) {
        //User authUser = (User) configurator.getUserProperties().get("authUser");
//        if(authUser == null) {
//            onClose(session);
//        }
        //session.getUserProperties().put("authUser", authUser);
        sessions.add(session);
//        sendToAll(String.format("User %s Enter chat",
//                //session.getId()));
//                authUser.getName()
//        ));
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        StoryViewModel res = this.addStory(message, session);
        if(res == null) {
            try {
                session.getBasicRemote().sendText("{ \"status\" : 400 }");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            sendToAll(gson.toJson(res));
        }
    }

    @OnClose
    public void onClose(Session session) {
        //User authUser = (User) session.getUserProperties().get("authUser");
        sessions.remove(session);
//        sendToAll(String.format("User '%s' Left chat",
//                //session.getId()
//                authUser.getName()
//        ));
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

    private StoryViewModel addStory(String message, Session session) {
        ChatMessage chatMessage = gson.fromJson(message, ChatMessage.class);
        if(chatMessage.getContent() == null
        || chatMessage.getContent().length() < 1) {
            return null;
        }
        if(chatMessage.getTaskId() == null) {
            return null;
        }
        User authUser = (User) session.getUserProperties().get("authUser");
        Story story = new Story();
        story.setIdUser(authUser.getId());
        story.setIdTask( UUID.fromString(chatMessage.getTaskId()));
        story.setContent(chatMessage.getContent());
        story = dataContext.getStoryDao().create(story);
        if(story == null) {
            return null;
        }
        StoryViewModel model = new StoryViewModel(
                story,
                dataContext.getUserDao()
                .getById( story.getIdUser() ),
                null
        ) ;
        if( story.getIdReply() != null ) {
            Story reply = dataContext.getStoryDao().getById(story.getIdReply());
            model.setReplyStory(new StoryViewModel(
                    reply,
                    dataContext.getUserDao().getById(reply.getIdUser()),
                    null
            ));
        }
        return model;
    }

    private class ChatMessage {
        String taskId ;
        String content;

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId =  taskId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

}
