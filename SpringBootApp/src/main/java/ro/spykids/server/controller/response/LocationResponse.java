package ro.spykids.server.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {
    private String childEmail;
    private Double latitude;
    private Double longitude;

    private LocalDateTime arrivalTime;

    private LocalDateTime departureTime;

    private String messages;


}
