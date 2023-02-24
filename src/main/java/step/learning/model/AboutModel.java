package step.learning.model;

import java.util.Date;

public class AboutModel {
    private String message;
    private Date moment ;

    public String getMessage() {
        return message;
    }

    public void setMoment(Date date) {
        moment = date;
    }

    public void setMessage(String mes) {
        message = mes;
    }

    public Date getMoment() {
        return moment;
    }
}
