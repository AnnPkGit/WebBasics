package itstep.learning.service;

import java.io.File ;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;

public class UploadService {
    private final DiskFileItemFactory fileItemFactory ;
    private final ServletFileUpload fileUpload ;

    public UploadService() {
        fileItemFactory = new DiskFileItemFactory() ;
        fileItemFactory.setSizeThreshold( 20480 ) ;
        File tmpDir = new File( "C:/tmp" ) ;
        if( ! tmpDir.exists() ) {
            if( ! tmpDir.mkdir() ) {
                tmpDir = null ;
            }
        }
        if( tmpDir != null ) {
            fileItemFactory.setRepository( tmpDir ) ;
        }
        fileUpload = new ServletFileUpload( fileItemFactory ) ;
    }

    public Map<String, FileItem> parse( HttpServletRequest request ) throws FileUploadException {
        return fileUpload
                .parseRequest( request )
                .stream()
                .collect(
                        Collectors.toMap(
                                FileItem::getFieldName,
                                Function.identity() ) ) ;
    }
}