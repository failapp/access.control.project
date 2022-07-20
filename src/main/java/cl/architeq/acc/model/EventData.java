package cl.architeq.acc.model;

import cl.architeq.acc.util.Util;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table (name = "eventlogs")
public class EventData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private int id;

    @Column(name = "event_datetime", columnDefinition = "datetime2")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;

    @Column(name = "event_code")
    private int eventCode;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "device_id")
    private Integer deviceId;

    @Column(name = "access_type")
    private int accessType;

    @Column(name = "location_id")
    private String locationId;

    @JsonIgnore
    private int sync;


    public EventData() {
        //
    }


    public EventData(LocalDateTime ldt, int eventCode, String userId, Integer deviceId, int accessType, String locationId) {
        this.dateTime = ldt;
        this.eventCode = eventCode;
        this.userId = userId;
        this.deviceId = deviceId;
        this.accessType = accessType;
        this.locationId = locationId;
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

    public int getEventCode() {
        return eventCode;
    }

    public void setEventCode(int eventCode) {
        this.eventCode = eventCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public int getAccessType() {
        return accessType;
    }

    public void setAccessType(int accessType) {
        this.accessType = accessType;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }


    public int getSync() {
        return sync;
    }
    public void setSync(int sync) {
        this.sync = sync;
    }

    @Override
    public String toString() {
        return "EventData{" +
                "id=" + id +
                ", dateTime=" + dateTime.format(Util.formatDateTime) +
                ", eventCode=" + eventCode +
                ", userId='" + userId + '\'' +
                ", deviceId=" + deviceId +
                ", accessType=" + accessType +
                ", locationId='" + locationId + '\'' +
                '}';
    }
}
