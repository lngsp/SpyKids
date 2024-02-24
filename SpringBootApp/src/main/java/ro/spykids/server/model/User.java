package ro.spykids.server.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "phone"),
        })
public class User implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String firstName, lastName;

    @NotNull
    @jakarta.validation.constraints.NotEmpty
    private String email;

    @NotNull
    @jakarta.validation.constraints.NotEmpty
    private String password;

    @NotNull
    @jakarta.validation.constraints.NotEmpty
    private String phone;

    @NotNull
    private Integer age;

    @NotNull    @Enumerated(EnumType.STRING)
    private Role role;

    @NotEmpty
    @NotNull    @Length(max=6)    @Column(length = 6)
    private String type;

    // RELATIONSHIP

    //TOKEN
    @Builder.Default
    @OneToMany(mappedBy = "userT")
    private Set<Token> tokens = new HashSet<>();

    //LOCATIONS
    @OneToMany(mappedBy = "userL")
    private Set<Location> locations;

    //RESTRICTED ZONE
    @Builder.Default
    @ManyToMany(mappedBy = "userA")
    private Set<Area> areas = new HashSet<>();

    //USER (CHILD TO PARENT)
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "parent_child",
            joinColumns = @JoinColumn(name = "parent_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "child_id", referencedColumnName = "id"))
    private Set<User> children = new HashSet<>();

    @Builder.Default
    @ManyToMany(mappedBy = "children")
    private Set<User> parents = new HashSet<>();

    //BATTERY
    @OneToMany(mappedBy = "userB")
    private Set<Battery> batteries;


    // OVERRIDE METHODS
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword(){
        return password;
    }

//    @Override
//    public int hashCode() {
//        return Objects.hash(email);
//    }

}
