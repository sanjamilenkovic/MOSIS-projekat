package rs.elfak.mosis.trafficbuddy.data;

public class Rating {
    private double rating;
    private String ratedById;
    private String ratedReportId;
    private String comment;

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getRatedById() {
        return ratedById;
    }

    public void setRatedById(String ratedById) {
        this.ratedById = ratedById;
    }

    public String getRatedReportId() {
        return ratedReportId;
    }

    public void setRatedReportId(String ratedDiscoId) {
        this.ratedReportId = ratedDiscoId;
    }
}

