package cl.architeq.acc.model;


import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Integer cod;

    private String name;

    private String model;

    private Boolean enabled;

    @Column(name = "ip_addr")
    private String ipAddr;

    @Column(name = "mac_addr")
    private String macAddr;

    @Column(name = "ip_addr_wlan")
    private String ipAddrWLAN;

    @Column(name = "mac_addr_wlan")
    private String macAddrWLAN;

    @Column(name = "tcp_port_service")
    private Integer tcpPortService;

    @Column (name="created", columnDefinition = "datetime2" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @Column (name="updated", columnDefinition = "datetime2" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;



    public Device() {
        //
    }


    public Device(Integer cod, String name, String model, String ipAddr, String macAddr, Location location) {

        this.cod = cod;
        this.name = name;
        this.model = model;
        this.enabled = true;
        this.ipAddr = ipAddr;
        this.macAddr = macAddr;
        this.location = location;

        this.tcpPortService = 0;
        this.ipAddrWLAN = "";
        this.macAddrWLAN = "";

        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public String getIpAddrWLAN() {
        return ipAddrWLAN;
    }

    public void setIpAddrWLAN(String ipAddrWLAN) {
        this.ipAddrWLAN = ipAddrWLAN;
    }

    public String getMacAddrWLAN() {
        return macAddrWLAN;
    }

    public void setMacAddrWLAN(String macAddrWLAN) {
        this.macAddrWLAN = macAddrWLAN;
    }

    public Integer getTcpPortService() {
        return tcpPortService;
    }

    public void setTcpPortService(Integer tcpPortService) {
        this.tcpPortService = tcpPortService;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


    public Integer getCod() {
        return cod;
    }
    public void setCod(Integer cod) {
        this.cod = cod;
    }


    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return getCod().equals(device.getCod()) &&
                getName().equals(device.getName()) &&
                getModel().equals(device.getModel()) &&
                getIpAddr().equals(device.getIpAddr()) &&
                getMacAddr().equals(device.getMacAddr());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCod(), getName(), getModel(), getIpAddr(), getMacAddr());
    }


    @Override
    public String toString() {
        return "Device{" +
                "cod=" + cod +
                ", name='" + name + '\'' +
                ", model='" + model + '\'' +
                ", enabled=" + enabled +
                ", ipAddr='" + ipAddr + '\'' +
                ", macAddr='" + macAddr + '\'' +
                ", tcpPortService=" + tcpPortService +
                '}';
    }
}
