package itstep.learning.model;

import itstep.learning.data.entity.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class TaskModel {
    private String name;
    private UUID idTeam;
    private Date deadline;
    private byte priority;
    private User author;

    private static final SimpleDateFormat formFormat
            = new SimpleDateFormat("yyyy-mmm-dd");

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getIdTeam() {
        return idTeam;
    }

    public void setIdTeam(String idTeam) throws IllegalArgumentException {
        this.idTeam = UUID.fromString(idTeam);
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) throws ParseException {
        this.deadline =  formFormat.parse(deadline);
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
