package ro.spykids.server.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IsInAreaResponse {
    private Boolean isInArea;
    private String areaName;
    private Boolean safe;
}
