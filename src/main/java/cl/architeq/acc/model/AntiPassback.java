package cl.architeq.acc.model;

import cl.architeq.acc.util.Util;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table (name = "antipassback")
public class AntiPassback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "event_datetime", columnDefinition = "datetime2")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_status")
    private int userStatus;

    @JsonIgnore
    private Integer sync;

    public AntiPassback() {
        // ..
    }


    public AntiPassback(String userId, int userStatus) {

        this.dateTime = LocalDateTime.now();
        this.userId = userId;
        this.userStatus = userStatus;
        this.sync = 0;

    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }


    public Integer getSync() {
        return sync;
    }

    public void setSync(Integer sync) {
        this.sync = sync;
    }

    @Override
    public String toString() {
        return "AntiPassback{" +
                "id=" + id +
                ", dateTime=" + dateTime.format(Util.formatDateTime) +
                ", userId='" + userId + '\'' +
                ", userStatus=" + userStatus +
                ", sync=" + sync +
                '}';
    }
}
