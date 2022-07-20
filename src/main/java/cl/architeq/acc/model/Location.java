package cl.architeq.acc.model;

import cl.architeq.acc.util.Util;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table (name = "locations")
public class Location {

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private int id;

    @NotNull
    @NotEmpty
    @Column(name = "cod")
    private String cod;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    private String description;

    private String address;

    private String locality;

    private String city;

    private String country;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column (name="created", columnDefinition = "datetime2")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @Column (name="updated", columnDefinition = "datetime2")
    @JsonFormat (pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    //@ManyToMany(mappedBy = "location")
    //private Set<User> users;

    public Location() {
        //
    }

    public Location(String cod, String name, Company company, String description) {

        this.cod = cod;
        this.name = name;
        this.company = company;
        this.description = description;
        this.address = "";
        this.locality = "";
        this.city = "";
        this.country = "";
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();

    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocality() {
        return locality;
    }
    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", cod='" + cod + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", company=" + company +
                ", created=" + created.format(Util.formatDateTime) +
                ", updated=" + updated.format(Util.formatDateTime) +
                '}';
    }


}
