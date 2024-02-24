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
@Table(name = "battery")
public class Battery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private Integer percent;

    @NotNull
    private String message;

    @NotNull(message = "Time at must not be null")
    @Column(name = "time", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime time;

    // RELATIONSHIP

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId",   referencedColumnName = "id")
    public User userB;

}
