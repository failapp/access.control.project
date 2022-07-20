package cl.architeq.acc.util;

import java.time.LocalDateTime;

public class SyncDeviceStatus {

    private int deviceId;
    private LocalDateTime dateSync;
    private String statusSync;


    public SyncDeviceStatus(int deviceId) {
        this.deviceId = deviceId;
        this.dateSync = LocalDateTime.now();
        this.statusSync = "";
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public LocalDateTime getDateSync() {
        return dateSync;
    }

    public void setDateSync(LocalDateTime dateSync) {
        this.dateSync = dateSync;
    }

    public String getStatusSync() {
        return statusSync;
    }

    public void setStatusSync(String statusSync) {
        this.statusSync = statusSync;
    }

    @Override
    public String toString() {
        return "SyncDeviceStatus{" +
                "deviceId=" + deviceId +
                ", dateSync=" + dateSync +
                ", statusSync='" + statusSync + '\'' +
                '}';
    }
}
