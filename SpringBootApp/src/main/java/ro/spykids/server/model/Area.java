package ro.spykids.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "area",
        uniqueConstraints = {@UniqueConstraint(name = "uk_name_definedby", columnNames = { "name", "definedBy" })}
)
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @NotEmpty(message = "The name must not be empty!")
    private String name;

    @NotNull(message = "The type must not be null!")
    private AreaType areaType;

    @Column(columnDefinition = "VARCHAR(1000)")
    @NotEmpty(message = "The data must not be empty!")
    @NotNull
    private String data;

    @NotNull
    @JsonProperty(defaultValue = "true")
    private Boolean enable;

    @NotNull
    @NotEmpty(message = "The definedBy must not be empty!")
    private String definedBy;

    @NotNull
    @JsonProperty(defaultValue = "true")
    private Boolean safe;   //if it's true is a safe zone, so when is in that zone make a green marker


    //RELATIONSHIP
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "child_area",
            joinColumns = @JoinColumn(name = "area_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "child_id", referencedColumnName = "id"))
    private Set<User> userA = new HashSet<>();

    // OVERRIDE METHODS

}
