package itstep.learning.model;

public class UserModel {
    private String login;
    private String pass1;
    private String pass2;
    private String name;
    private String email;

    // region getters setters
    public String getLogin() {
        return login;
    }

    public String getPass1() {
        return pass1;
    }

    public String getPass2() {
        return pass2;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPass1(String pass1) {
        this.pass1 = pass1;
    }

    public void setPass2(String pass2) {
        this.pass2 = pass2;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // endregion
}
