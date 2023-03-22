package itstep.learning.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.data.dao.IUserDao;
import itstep.learning.data.entity.User;
import itstep.learning.service.HashService;
import itstep.learning.service.UploadService;
import itstep.learning.service.auth.IAuthService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class UserProfileServlet extends HttpServlet {
    @Inject
    private IUserDao userDao;

    @Inject
    IAuthService authService;

    @Inject
    private Logger logger;

    @Inject
    private UploadService uploadService;

    @Inject
    private HashService hashService;

    @Inject @Named("AvatarFolder")
    private String avatarFolder;

    @Override
    protected void service( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
        switch( req.getMethod().toUpperCase() ) {
            case "GET"     :
            case "POST"    :
            case "HEAD"    :
            case "OPTIONS" :
            case "DELETE"  :
            case "TRACE"   :
                super.service( req, resp ) ;
                break ;
            case "PATCH" :
                doPatch( req, resp ) ;
                break ;
            case "PUT"     :
                doPut( req, resp ); ;
                break ;
        }
    }

    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
        String profileUserLogin = req.getPathInfo() ;  //   /user
        User profileUser = null ;
        String viewName = "profile-404" ;  // профиль не найден
        if( profileUserLogin != null && profileUserLogin.length() > 1 ) {
            profileUserLogin = profileUserLogin.substring( 1 ) ;   // user
            profileUser = userDao.getUserProfile( profileUserLogin ) ;
            if( profileUser != null ) {
                User authUser = authService.getAuthUser() ;
                if( authUser != null && authUser.getId().equals( profileUser.getId() ) ) {
                    // свой профиль
                    viewName = "profile-my" ;
                }
                else {  // чужой профиль
                    req.setAttribute( "profileUser", profileUser ) ;
                    viewName = "profile" ;
                }
            }
        }

        req.setAttribute( "viewName", viewName ) ;
        req.getRequestDispatcher( "../WEB-INF/_layout.jsp" ).forward( req, resp ) ;
    }

    @Override
    protected void doPut( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
        String body ;
        try( InputStream bodyStream = req.getInputStream() ) {
            byte[] buf = new byte[1024] ;
            ByteArrayOutputStream arr = new ByteArrayOutputStream() ;
            int len ;
            while( ( len = bodyStream.read( buf ) ) != -1 ) {
                arr.write( buf, 0 , len ) ;
            }
            body = arr.toString( "UTF-8" ) ;
            arr.close() ;
        }
        catch( Exception ex ) {
            logger.log( Level.SEVERE, ex.getMessage() ) ;
            resp.getWriter().print( "Error" ) ;
            return ;
        }

        try {
            JSONObject obj = new JSONObject( body ) ;
            if( obj.has( "userName" ) ) {
                // запрос на изменение имени
                String userName = obj.optString( "userName" ) ;
                // TODO: валидировать имя

                // проверяем авторизацию
                User user = authService.getAuthUser() ;
                if( user == null ) {
                    resp.setStatus( 401 ) ;
                    resp.getWriter().print( "Unauthorized" ) ;
                    return ;
                }
                user.setName( userName ) ;
                if( userDao.updateName( user ) )
                    body = "OK" ;
                else
                    body = "500" ;
            }
        }
        catch( JSONException ex ) {
            logger.log( Level.WARNING, ex.getMessage() ) ;
            body = "Error" ;
        }
        resp.getWriter().print( body ) ;
    }

    protected void doPatch( HttpServletRequest req, HttpServletResponse resp ) throws IOException {
        User user = authService.getAuthUser() ;
        if( user == null ) {
            resp.setStatus( 401 ) ;
            resp.getWriter().print( "Unauthorized" ) ;
            return ;
        }
        try {
            Map<String, FileItem> params = uploadService.parse( req ) ;
            FileItem avatarItem = params.get( "userAvatar" ) ;
            if( avatarItem == null ) {
                resp.setStatus( 400 ) ;
                resp.getWriter().print( "Missed required field 'userAvatar'" ) ;
                return ;
            }
            String path = req.getServletContext().getRealPath( "/" ) + avatarFolder ;
            String newAvatar = this.saveAvatar( avatarItem, path ) ;
            if( newAvatar == null ) {
                resp.setStatus( 500 ) ;
                resp.getWriter().print( "Error during upload" ) ;
                return ;
            }
            // загрузка успешная, удаляем старый аватар
            String oldAvatar = user.getAvatar() ;
            if( oldAvatar != null ) {
                File file = new File( path, oldAvatar ) ;
                file.delete() ;
            }

        }
        catch( FileUploadException ex ) {
            resp.getWriter().print( ex.getMessage() ) ;
            return ;
        }
        resp.getWriter().print( "OK" ) ;
    }

    private String saveAvatar( FileItem avatar, String path ) {
        if( !avatar.isFormField() ) {
            if( avatar.getSize() > 0 ) {
                String filename = avatar.getName();
                int dotPosition = filename.lastIndexOf( '.' );
                String extension = filename.substring( dotPosition );
                // TODO: проверить на допустимое расширение файла
                // Имя файла сохранять опасно - генерируем случайное имя и проверяем на существование файла
                String savedName = "";
                File file;
                do {
                    savedName = hashService.getHexHash( savedName + System.nanoTime() ) + extension;
                    file = new File( path, savedName );
                } while( file.exists() );
                try { avatar.write( file ) ;  }
                catch( Exception ex ) { return null ; }
                return savedName;
            }
        }
        return null;
    }
    /*
Команды: пользователи могут состоять в разных командах
Задачи: задача ставится любым участником команды, вся команда является исполнителем и "видит" чат
Чат: по кажой задаче свой чат, писать и просматривать могут только участники
CREATE TABLE `teams` (
    `id` char(36) NOT NULL COMMENT 'UUID',
    `name` varchar(64) NOT NULL,
     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
CREATE TABLE `teams_users` (
    `id_team` char(36) NOT NULL,
    `id_user` char(36) NOT NULL,
     PRIMARY KEY (`id_team`, `id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
CREATE TABLE `tasks` (
    `id` char(36) NOT NULL COMMENT 'UUID',
    `name` varchar(64) NOT NULL,
    `status` int DEFAULT 0,
    `id_user` char(36) NOT NULL COMMENT 'Author',
    `id_team` char(36) NOT NULL,
    `created_dt` datetime DEFAULT CURRENT_TIMESTAMP,
    `deadline` datetime,
    `priority` TINYINT default 0,
     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
CREATE TABLE `stories` (
    `id` char(36) NOT NULL COMMENT 'UUID',
    `id_user` char(36) NOT NULL COMMENT 'Author',
    `id_task` char(36) NOT NULL,
    `id_reply` char(36) NULL  COMMENT 'Other story id',
    `content` TEXT NOT NULL,
    `created_dt` datetime DEFAULT CURRENT_TIMESTAMP,
     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

INSERT INTO `teams` VALUES( 'd5a875eb-c1c2-11ed-b458-14857fd97497', 'Frontend' ) ;
INSERT INTO `teams` VALUES( '07094a33-c1c3-11ed-b458-14857fd97497', 'Backend' ) ;
INSERT INTO `teams` VALUES( '16e22c6e-c1c3-11ed-b458-14857fd97497', 'Design' ) ;
INSERT INTO `teams_users` VALUES ( 'd5a875eb-c1c2-11ed-b458-14857fd97497', '6c6fe99f-bc3c-11ed-a657-7c10c98d53d5' ) ;
INSERT INTO `teams_users` VALUES ( '07094a33-c1c3-11ed-b458-14857fd97497', '0aec36d2-b858-11ed-9335-7c10c98d53d5' ) ;
INSERT INTO `teams_users` VALUES ( '16e22c6e-c1c3-11ed-b458-14857fd97497', '24fd29f1-b6c0-11ed-9335-7c10c98d53d5' ) ;

INSERT INTO `teams` VALUES( '93f372cb-c692-4e5e-95ef-4d212be4ca31', 'DevOps' ) ;
INSERT INTO `teams` VALUES( 'a59c40fb-bf5f-4075-a118-be14e32f7665', 'HR' ) ;
INSERT INTO `teams` VALUES( '8a1d9676-a44f-41a3-929c-40cc834c0195', 'Law Department' ) ;

INSERT INTO `teams_users` VALUES ( '93f372cb-c692-4e5e-95ef-4d212be4ca31', '6c6fe99f-bc3c-11ed-a657-7c10c98d53d5' ) ;
INSERT INTO `teams_users` VALUES ( 'a59c40fb-bf5f-4075-a118-be14e32f7665', '6599eda6-6f48-11ed-aa83-7c10c98d53d5' ) ;
INSERT INTO `teams_users` VALUES ( '8a1d9676-a44f-41a3-929c-40cc834c0195', '356643c2-7a28-11ed-87dc-7c10c98d53d5' ) ;

 */
}
