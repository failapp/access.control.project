package cl.architeq.acc.model;

import cl.architeq.acc.util.Util;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sync_user_device")
public class SyncUserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private int id;

    @Column(name = "user_dni")
    private String userDni;

    @Column(name = "device_cod")
    private Integer deviceCod;

    @Column(name = "operation")
    private String action;

    @Column(name = "date_request")
    private String dateRequest;

    @Column(name = "date_sync")
    private String dateSync;

    private int ack;


    public SyncUserDevice() {
        //
    }

    public SyncUserDevice(String userDni, Integer deviceCod, String action) {
        this.userDni = userDni;
        this.deviceCod = deviceCod;
        this.action = action;
        this.dateRequest = LocalDateTime.now().format(Util.formatDateTimeIDTi);
        this.dateSync = LocalDateTime.now().format(Util.formatDateTimeIDTi);
        this.ack = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserDni() {
        return userDni;
    }

    public void setUserDni(String userDni) {
        this.userDni = userDni;
    }


    public Integer getDeviceCod() {
        return deviceCod;
    }

    public void setDeviceCod(Integer deviceCod) {
        this.deviceCod = deviceCod;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDateRequest() {
        return dateRequest;
    }

    public void setDateRequest(String dateRequest) {
        this.dateRequest = dateRequest;
    }

    public String getDateSync() {
        return dateSync;
    }

    public void setDateSync(String dateSync) {
        this.dateSync = dateSync;
    }

    public int getAck() {
        return ack;
    }

    public void setAck(int ack) {
        this.ack = ack;
    }


    @Override
    public String toString() {
        return "SyncUserDevice{" +
                "id=" + id +
                ", userDni='" + userDni + '\'' +
                ", deviceCod=" + deviceCod +
                ", action='" + action + '\'' +
                ", dateRequest='" + dateRequest + '\'' +
                ", dateSync='" + dateSync + '\'' +
                ", ack=" + ack +
                '}';
    }
}
