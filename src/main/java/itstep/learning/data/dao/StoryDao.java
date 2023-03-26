package itstep.learning.data.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.data.entity.Entity;
import itstep.learning.data.entity.Story;
import itstep.learning.data.entity.Task;
import itstep.learning.service.DbService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class StoryDao {
    private final DbService dbService;
    private final Logger logger;

    @Inject
    public StoryDao(DbService dbService, Logger logger) {
        this.dbService = dbService;
        this.logger = logger;
    }

    public boolean add(Story story) {
        String sql = "INSERT INTO `stories` (`id`,`id_user`,`id_task`,`id_reply`,`content`,`created_dt`) "
                + "VALUES( UUID(), ?, ?, ?, ?, CURRENT_TIMESTAMP) ";
        try (PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {
            prep.setString(1, story.getIdUser().toString());
            prep.setString(2, story.getIdTask().toString());
            UUID idReply = story.getIdReply();
            prep.setString(3, idReply == null ? null : idReply.toString());
            prep.setString(4, story.getContent());

            prep.executeUpdate();
            return true;
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return false;
        }
    }

    public Story create(Story story) {
        story.setId(UUID.randomUUID());
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC) ;
        story.setCreatedDt(Date.from( zonedDateTime.toInstant()));

        String sql = "INSERT INTO `stories` (`id`,`id_user`,`id_task`,`id_reply`,`content`,`created_dt`) "
                + "VALUES( ?, ?, ?, ?, ?, ?) ";
        try (PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {
            prep.setString(1, story.getId().toString());
            prep.setString(2, story.getIdUser().toString());
            prep.setString(3, story.getIdTask().toString());
            UUID idReply = story.getIdReply();
            prep.setString(4, idReply == null ? null : idReply.toString());
            prep.setString(5, story.getContent());
            prep.setString(6,
                    Entity.sqlDatetimeFormat.format(story.getCreatedDt()));

            prep.executeUpdate();
            return story;
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    public List<Story> getListByTask(Object task) throws IllegalArgumentException {
        UUID taskId = null;
        if (task instanceof Task) {
            taskId = ((Task) task).getId();
        } else if (task instanceof UUID) {
            taskId = (UUID) task;
        } else if (task instanceof String) {
            try {
                taskId = UUID.fromString((String) task);
            } catch (Exception ignored) {
            }
        }
        if (taskId == null) {
            throw new IllegalArgumentException("Argument 'task' should be Task or UUID or String. Given " + task);
        }
        String sql = "SELECT s.* FROM stories s WHERE s.id_task = ? ORDER BY s.created_dt DESC";
        try (PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {
            prep.setString(1, taskId.toString());
            ResultSet res = prep.executeQuery();
            List<Story> list = new ArrayList<>();
            while (res.next()) {
                list.add(new Story(res));
            }
            return list;
        } catch (Exception ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    public Story getById( Object id ) throws IllegalArgumentException {
        UUID _id = null ;
        if( id instanceof Story ) {
            _id = ( (Story) id ).getId() ;
        }
        else if( id instanceof UUID ) {
            _id = (UUID) id ;
        }
        else if( id instanceof String ) {
            try {
                _id = UUID.fromString( (String) id ) ;
            }
            catch( Exception ignored ) {
            }
        }
        if( _id == null ) {
            throw new IllegalArgumentException( "Argument 'task' should be Task or UUID or String. Given " + id );
        }
        String sql = "SELECT s.* FROM stories s WHERE s.id = ?" ;
        try( PreparedStatement prep = dbService.getConnection().prepareStatement( sql ) ) {
            prep.setString( 1, _id.toString() ) ;
            ResultSet res = prep.executeQuery() ;
            if( res.next() )
                return new Story( res ) ;
        }
        catch( Exception ex ) {
            logger.log( Level.WARNING, ex.getMessage() ) ;
        }
        return null ;
    }
}