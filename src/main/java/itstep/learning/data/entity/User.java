package itstep.learning.data.entity;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class User extends Entity{ //ORM
    private UUID id;
    private String login;
    private String name;
    private String salt;
    private String pass;
    private String email;
    private String confirm;
    private String avatar;
    private Date regDt;
    private Date deleteDt;
    private String roleId;

    // region  getters setters
    public UUID getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getSalt() {
        return salt;
    }

    public String getPass() {
        return pass;
    }

    public String getEmail() {
        return email;
    }

    public String getConfirm() {
        return confirm;
    }

    public String getAvatar() {
        return avatar;
    }

    public Date getRegDt() {
        return regDt;
    }

    public Date getDeleteDt() {
        return deleteDt;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setRegDt(Date regDt) {
        this.regDt = regDt;
    }

    public void setDeleteDt(Date deleteDt) {
        this.deleteDt = deleteDt;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    //endregion

    // region constructors

    public User() {
    }

    public User(ResultSet res) throws RuntimeException {
        try {
            setId(UUID.fromString(res.getString("id")));
            setLogin(res.getString("login"));
            setName(res.getString("name"));
            setSalt(res.getString("salt"));
            setPass(res.getString("pass"));
            setEmail(res.getString("email"));
            setConfirm(res.getString("confirm"));
            setAvatar(res.getString("avatar"));
            setRoleId(res.getString("role_Id"));
            setRegDt(sqlDatetimeFormat.parse(res.getString("reg_dt")));
            String deleteDtString = res.getString("delete_dt");
            if(deleteDtString != null) {
                setDeleteDt(sqlDatetimeFormat.parse(deleteDtString));
            }
        }
        catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
