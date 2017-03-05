package app.dafferianto.info.popularmovies.data;

public class ReviewTrailerJson {
    private String reviewJson;
    private String trailerJson;

    public ReviewTrailerJson() {
    }

    public ReviewTrailerJson(String reviewJson, String trailerJson) {
        this.reviewJson = reviewJson;
        this.trailerJson = trailerJson;
    }

    public String getReviewJson() {
        return reviewJson;
    }

    public void setReviewJson(String reviewJson) {
        this.reviewJson = reviewJson;
    }

    public String getTrailerJson() {
        return trailerJson;
    }

    public void setTrailerJson(String trailerJson) {
        this.trailerJson = trailerJson;
    }

}
