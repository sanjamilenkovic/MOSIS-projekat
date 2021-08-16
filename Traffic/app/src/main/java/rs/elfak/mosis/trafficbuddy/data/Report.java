package rs.elfak.mosis.trafficbuddy.data;

public class Report {

    private String id;
    private String title;
    private double lat;
    private double lon;
    private String description;
    private String reportedById;
    private Integer icon;
    private String iconTitle;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public String getTitle() { return title; }

    public void setTitle(String t) {
        this.title = t;
    }

    public String getIconTitle() { return iconTitle; }

    public void setIconTitle(String t) {
        this.iconTitle = t;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getDescription() { return description; }

    public void setDescription(String d) {
        this.description = d;
    }

    public String getReportedById() { return reportedById; }

    public void setReportedById(String idr) {
        this.reportedById = idr;
    }
}
