package ro.spykids.server.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddChildToRestrictedAreaRequest {
    private String parentEmail;
    private String childEmail;
    private String areaName;
}
