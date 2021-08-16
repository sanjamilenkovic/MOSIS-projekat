package rs.elfak.mosis.trafficbuddy.data;

public class Rating {
    private String id;
    private Integer likes=0;
    private String ratedById;
    private String ratedReportId;
    private String reportAddedBy;
    private String comment;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public String getRatedById() {
        return ratedById;
    }

    public void setRatedById(String ratedById) {
        this.ratedById = ratedById;
    }

    public String getReportAddedBy() {
        return reportAddedBy;
    }

    public void setReportAddedBy(String reportAddedBy) {
        this.reportAddedBy = reportAddedBy;
    }

    public String getRatedReportId() {
        return ratedReportId;
    }

    public void setRatedReportId(String ratedReportId) {
        this.ratedReportId = ratedReportId;
    }

    public void addLike() { this.likes = 1;}
    public void dislike() { this.likes = 0;}

}

