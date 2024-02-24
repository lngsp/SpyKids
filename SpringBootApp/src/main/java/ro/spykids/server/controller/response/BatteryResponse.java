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
public class BatteryResponse {
    private Integer percent;
    private String message;
    private LocalDateTime time;
    private String childEmail;
}
