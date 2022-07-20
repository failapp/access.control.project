package cl.architeq.acc.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "auth")
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private int id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "pwd")
    private String password;

    private boolean enabled;
    private boolean connected;

    @Column(name = "token")
    private String token;

    @Column (name="created", columnDefinition = "datetime2" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @Column ( name="updated", columnDefinition = "datetime2" )
    @JsonFormat ( pattern = "yyyy-MM-dd HH:mm:ss" )
    private LocalDateTime updated;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "rel_auth_role",
               joinColumns = @JoinColumn( name = "auth_id", nullable = false ),
               inverseJoinColumns = @JoinColumn( name = "role_id", nullable = false ))
    private Set<Role> roles;


    public Auth() {
        // ..
    }

    public Auth(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.enabled = true;
        this.connected = false;
        this.token = "";
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }

    public void addRole(Role role) {
        if (this.roles == null)
            this.roles = new HashSet<>();

        this.roles.add(role);
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Auth{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", connected=" + connected +
                ", roles=" + roles +
                '}';
    }
}
