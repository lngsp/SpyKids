package ro.spykids.clientapp.pojo;

public class Area {

    private String name;

    private AreaType areaType;

    private String data;

    private Boolean enable;

    private Boolean safe;   //if it's true is a safe zone, so when is in that zone make a green marker

    public Area() {
    }

    public Area(String name, AreaType areaType, String data, Boolean enable,  Boolean safe) {
        this.name = name;
        this.areaType = areaType;
        this.data = data;
        this.enable = enable;
        this.safe = safe;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AreaType getAreaType() {
        return areaType;
    }

    public void setAreaType(AreaType areaType) {
        this.areaType = areaType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean getSafe() {
        return safe;
    }

    public void setSafe(Boolean safe) {
        this.safe = safe;
    }
}
