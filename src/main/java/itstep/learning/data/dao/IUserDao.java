package itstep.learning.data.dao;

import itstep.learning.data.entity.User;
import itstep.learning.model.UserModel;

import javax.annotation.Nonnull;
import java.util.List;

public interface IUserDao {
    List<User> getAll();
    boolean add(@Nonnull UserModel model);
}
