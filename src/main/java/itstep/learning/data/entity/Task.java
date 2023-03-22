package itstep.learning.data.entity;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Task extends Entity{
    private UUID id;
    private String name;
    private UUID idTeam;
    private int status;
    private UUID idUser;
    private Date deadline;
    private Date createdDt;
    private byte priority;

    public Task() {
    }

    public Task( ResultSet res ) {
        try {
            setId( UUID.fromString( res.getString( "id" ) ) ) ;
            setName(    res.getString( "name" ) ) ;
            setStatus(  res.getInt( "status" ) ); ;
            setIdUser( UUID.fromString( res.getString( "id_user" ) ) ) ;
            setIdTeam( UUID.fromString( res.getString( "id_team" ) ) ) ;
            setCreatedDt( sqlDatetimeFormat.parse( res.getString( "created_dt" ) ) ) ;
            setDeadline( sqlDatetimeFormat.parse( res.getString( "deadline" ) ) ); ;
            setPriority( res.getByte( "priority" ) );    }
        catch( Exception ex ) {
            throw new RuntimeException( ex.getMessage() ) ;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getIdTeam() {
        return idTeam;
    }

    public void setIdTeam(UUID idTeam) {
        this.idTeam = idTeam;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public UUID getIdUser() {
        return idUser;
    }

    public void setIdUser(UUID idUser) {
        this.idUser = idUser;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(Date createDt) {
        this.createdDt = createDt;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }
}
