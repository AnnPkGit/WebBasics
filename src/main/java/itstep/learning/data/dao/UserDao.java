package itstep.learning.data.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.data.entity.User;
import itstep.learning.model.TaskModel;
import itstep.learning.model.UserModel;
import itstep.learning.service.DbService;
import itstep.learning.service.HashService;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class UserDao implements IUserDao { // Data Access Object for entity.User
    private final DbService dbService;
    private final HashService hashService;
    private final Logger logger;

    @Inject
    public UserDao(DbService dbService, HashService hashService, Logger logger) {
        this.dbService = dbService;
        this.hashService = hashService;
        this.logger = logger;
    }

    public List<User> getAll() {
        try(Statement statement = dbService.getConnection().createStatement()) {
            ResultSet res =  statement.executeQuery("SELECT u.* FROM Users u");
            List<User> users = new ArrayList<>();
            while(res.next())
                users.add(new User(res));
            return users;
        }
        catch (SQLException ex) {
            System.err.println("USerDao::getAll " + ex.getMessage());
            return null;
        }
    }

    public boolean add(@Nonnull UserModel model) {
        String salt = hashService.getHexHash( System.nanoTime() + "");
        String hash = getPassHash(model.getPass1(), salt);
        String sql = "INSERT INTO users(`id`, `login`, `name`, `salt`, `pass`, `email`, `avatar`)"
                + " VALUES(UUID(), ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {
            prep.setString(1, model.getLogin());
            prep.setString(2, model.getName());
            prep.setString(3, salt);
            prep.setString(4, hash);
            prep.setString(5, model.getEmail());
            prep.setString(6, model.getAvatar());
            prep.execute();
            return true;
        }
        catch (Exception ex) {
            System.err.println("UserDao::add" + ex.getMessage());
            return false;
        }
    }

    public User getUserByCresentials(String login, String password) {
        String sql = "SELECT * FROM users WHERE login = ?"; //AND pass = ?";
        try(PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {
            prep.setString(1, login);
            //prep.setString(2, password);
            ResultSet res =  prep.executeQuery();
            if(res.next()) {
                User user = new User(res);
                if(getPassHash(password, user.getSalt()).equals(user.getPass())) {
                    return user;
                }
            }

        }
        catch (Exception ex) {
            System.err.println("UserDao::add" + ex.getMessage());
        }
        return null;
    }

    @Override
    public User getUserByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?"; //AND pass = ?";
        try(PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {
            prep.setString(1, login);
            ResultSet res =  prep.executeQuery();
            if(res.next()) {
                return new User(res);
            }
        }
        catch (Exception ex) {
            System.err.println("UserDao::getUserByLogin" + ex.getMessage());
        }
        return null;
    }

    @Override
    public User getUserProfile(String login) {
        User user = this.getUserByLogin(login);
        if(user != null) {
            user.setPass("");
            user.setSalt("");
        }
        return user;
    }

    @Override
    public boolean updateName(User user) {
        String sql = "UPDATE users u SET u.`name` = ? WHERE u.`id` = ?"; //AND pass = ?";
        try(PreparedStatement prep = dbService.getConnection().prepareStatement(sql)) {
            prep.setString(1, user.getName());
            prep.setString(1, user.getId().toString());
            prep.executeUpdate();
            return true;
        }
        catch (Exception ex) {
            logger.log(Level.WARNING, ex.getMessage());
        }
        return false;
    }

    public User getById( Object id )  throws IllegalArgumentException {
        return _getById( id, false ) ;}
    private User _getById( Object id, boolean includeCredentials ) throws IllegalArgumentException {    UUID _id = null ;    if( id instanceof User ) {        _id = ( (User) id ).getId() ;    }    else if( id instanceof UUID ) {        _id = (UUID) id ;    }    else if( id instanceof String ) {        try {            _id = UUID.fromString( (String) id ) ;        }        catch(Exception ignored ) {        }    }    if( _id == null ) {        throw new IllegalArgumentException( "Argument 'task' should be Task or UUID or String. Given " + id );    }    String sql = "SELECT u.* FROM users u WHERE u.id = ?" ;    try(PreparedStatement prep = dbService.getConnection().prepareStatement( sql ) ) {        prep.setString( 1, _id.toString() ) ;        ResultSet res = prep.executeQuery() ;        if( res.next() ) {            User user = new User( res ) ;            if( ! includeCredentials ) {                user.setPass( null ) ;                user.setSalt( null ); ;            }            return user ;        }    }    catch(Exception ex )
    {        logger.log( Level.WARNING, ex.getMessage() ) ;
    }    return null ;}

    private String getPassHash( String password, String salt) {
        return hashService.getHexHash( salt + password );
    }
}
