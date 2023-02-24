package itstep.learning.ioc;

import com.google.inject.AbstractModule;
import itstep.learning.data.dao.IUserDao;
import itstep.learning.data.dao.UserDao;
import itstep.learning.service.DbService;
import itstep.learning.service.HashService;
import itstep.learning.service.LocalDbService;
import itstep.learning.service.MD5HashService;

public class ServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind( DbService.class ).to( LocalDbService.class ) ;
        bind( HashService.class ).to( MD5HashService.class ) ;
        bind( IUserDao.class ).to( UserDao.class ) ;
    }
}
