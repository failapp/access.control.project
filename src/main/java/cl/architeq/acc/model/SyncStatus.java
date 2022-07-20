package cl.architeq.acc.model;

import cl.architeq.acc.util.EventCode;
import cl.architeq.acc.util.Util;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.json.JSONObject;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table (name = "sync_status")
public class SyncStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private int id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "device_id")
    private Integer deviceId;

    @Column(name = "date_sync", columnDefinition = "datetime2")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateSync;

    @Column(name = "user_sync")
    private String userSync;  // JSON ..

    @Column(name = "event_sync")
    private String eventSync; // JSON ..

    @Column(name = "device_sync")
    private String deviceSync; // JSON ..

    @JsonIgnore
    private Integer sync;

    public SyncStatus() {
        // ..
    }

    public SyncStatus(User user, EventData event, AntiPassback apb, Device device) {

        this.userId = user.getDni();
        this.deviceId = device.getCod();
        this.dateSync = LocalDateTime.now();
        this.sync = 0;

        JSONObject jsonUser = new JSONObject();
        JSONObject jsonUserDetails = new JSONObject();

        jsonUserDetails.put("userid", user.getDni());
        jsonUserDetails.put("created", user.getCreated().format(Util.formatDateTime));
        jsonUserDetails.put("updated", user.getUpdated().format(Util.formatDateTime));
        jsonUserDetails.put("enabled", user.getEnabled());

        jsonUser.put("user", jsonUserDetails);
        this.userSync = jsonUser.toString();

        //System.out.println(userSync);


        JSONObject jsonEvent = new JSONObject();
        JSONObject jsonEventDetails = new JSONObject();
        String accessType = (event.getAccessType() == 1) ? "IN" : "OUT";
        jsonEventDetails.put("last_event", event.getDateTime().format(Util.formatDateTime));
        jsonEventDetails.put("deviceId", event.getDeviceId().toString());
        jsonEventDetails.put("eventCode", EventCode.getName(event.getEventCode()) );
        jsonEventDetails.put("accessType", accessType );

        jsonEvent.put("eventdata", jsonEventDetails);
        this.eventSync = jsonEvent.toString();

        //System.out.println(eventSync);

        JSONObject jsonSync = new JSONObject();
        JSONObject jsonSyncDetails = new JSONObject();

        jsonSyncDetails.put("deviceId", device.getCod());
        jsonSyncDetails.put("deviceName", device.getName());
        jsonSyncDetails.put("dateSync", this.dateSync.format(Util.formatDateTime));
        jsonSyncDetails.put("locationId", event.getLocationId());

        jsonSync.put("device", jsonSyncDetails);

        if (user.getAntiPassback()) {
            if (apb != null) {

                JSONObject jsonAPB = new JSONObject();

                int apbIN = (apb.getUserStatus() == 1 ) ? 1 : 0;
                int apbOUT = (apb.getUserStatus() == 0 ) ? 1 : 0;

                jsonAPB.put("statusIn", apbIN );
                jsonAPB.put("statusOut", apbOUT );

                jsonSyncDetails.put("antipassback", jsonAPB);
            }
        }

        this.deviceSync = jsonSync.toString();

        //System.out.println(deviceSync);

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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


    public LocalDateTime getDateSync() {
        return dateSync;
    }

    public void setDateSync(LocalDateTime dateSync) {
        this.dateSync = dateSync;
    }

    public String getUserSync() {
        return userSync;
    }

    public void setUserSync(String userSync) {
        this.userSync = userSync;
    }

    public String getEventSync() {
        return eventSync;
    }

    public void setEventSync(String eventSync) {
        this.eventSync = eventSync;
    }

    public String getDeviceSync() {
        return deviceSync;
    }

    public void setDeviceSync(String deviceSync) {
        this.deviceSync = deviceSync;
    }

    public Integer getSync() {
        return sync;
    }
    public void setSync(Integer sync) {
        this.sync = sync;
    }

    @Override
    public String toString() {
        return "SyncStatus{" +
                "userId='" + userId + '\'' +
                ", dateSync=" + dateSync.format(Util.formatDateTime) +
                ", userSync='" + userSync + '\'' +
                ", eventSync='" + eventSync + '\'' +
                ", deviceSync='" + deviceSync + '\'' +
                '}';
    }



}
