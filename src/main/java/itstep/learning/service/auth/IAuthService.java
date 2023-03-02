package itstep.learning.service.auth;

import itstep.learning.data.entity.User;

import javax.servlet.http.HttpServletRequest;

public interface IAuthService {
    User getAuthUser();
    void authorize(HttpServletRequest request);
    void logout(HttpServletRequest request);
}
