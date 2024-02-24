package ro.spykids.server.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity
@Table(name = "token", uniqueConstraints = {
        @UniqueConstraint(columnNames = "value") })
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;


    @NotNull
    public String value;


    @NotNull
    public String type;

    @NotNull
    public boolean revoked;

    @NotNull
    public boolean expired;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId",  referencedColumnName = "id")
    public User userT;
}
