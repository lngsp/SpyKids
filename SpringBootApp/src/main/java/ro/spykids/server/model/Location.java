package ro.spykids.server.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @NotNull(message = "Created at must not be null")
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @NotNull(message = "Arrival time at must not be null")
    @Column(name = "arrival_time", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime arrivalTime;

    @Column(name = "departure_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime departureTime;


    // RELATIONSHIP

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId",   referencedColumnName = "id")
    public User userL;


    // OVERRIDE METHODS

}
