package ro.spykids.clientapp.ui.model;

public class AllAreasModel {
    private String name;
    private String areaType;
    private String safe;
    private String enable;
    private String data;

    public AllAreasModel(){}

    public AllAreasModel(String name, String areaType, String safe, String enable, String data) {
        this.name = name;
        this.areaType = areaType;
        this.safe = safe;
        this.enable = enable;
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSafe() {
        return safe;
    }

    public void setSafe(String safe) {
        this.safe = safe;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }
}
