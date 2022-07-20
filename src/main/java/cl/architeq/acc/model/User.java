package cl.architeq.acc.model;

import cl.architeq.acc.util.Util;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private int id;

    private String dni;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    private Boolean enabled;

    @Column(name="validity", columnDefinition = "date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate validity;

    @Column(name = "type_user")
    private String type;

    private String proximity;
    private Boolean antiPassback;

    @Column(name="created", columnDefinition = "datetime2")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @Column(name="updated", columnDefinition = "datetime2")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    private Boolean sync;

    public User() {

        this.dni = "";
        this.firstName = "";
        this.lastName = "";
        this.enabled = true;
        this.antiPassback = true;
        this.type = "user";
        this.proximity = "";

        this.validity = LocalDate.now().plusYears(20);
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();

        this.sync = false;

    }

    public User(String dni, String firstName, String lastName, boolean antiPassback) {

        this.dni = dni;
        this.firstName = firstName;
        this.lastName = lastName;
        this.antiPassback = antiPassback;
        this.enabled = true;

        this.type = "user";
        this.proximity = "";

        this.validity = LocalDate.now().plusYears(20);
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();

        this.sync = false;
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }
    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDate getValidity() {
        return validity;
    }
    public void setValidity(LocalDate validity) {
        this.validity = validity;
    }

    public Boolean getAntiPassback() {
        return antiPassback;
    }
    public void setAntiPassback(Boolean antiPassback) {
        this.antiPassback = antiPassback;
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

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getProximity() {
        return proximity;
    }

    public void setProximity(String proximity) {
        this.proximity = proximity;
    }

    public Boolean getSync() {
        return sync;
    }

    public void setSync(Boolean sync) {
        this.sync = sync;
    }


    @Override
    public String toString() {
        return "User{" +
                "dni='" + dni + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", enabled=" + enabled +
                ", validity=" + validity +
                ", type='" + type + '\'' +
                ", proximity='" + proximity + '\'' +
                ", antiPassback=" + antiPassback +
                ", created=" + created.format(Util.formatDateTime) +
                ", updated=" + updated.format(Util.formatDateTime) +
                ", sync=" + sync +
                '}';
    }

}
