package ro.spykids.server.controller.response;

import ro.spykids.server.model.AreaType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AreaResponse {
    private String name;
    private AreaType areaType;
    private String data;
    private Boolean enable;

    private Boolean safe;

}
