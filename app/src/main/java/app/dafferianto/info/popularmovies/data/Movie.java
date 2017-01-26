package app.dafferianto.info.popularmovies.data;

import java.io.Serializable;

public class Movie implements Serializable {
    private int id;
    private String posterPath;
    private String title;
    private String releaseDate;
    private double voteAverage;
    private String overview;

    public Movie(int id, String posterPath, String title, String releaseDate, double voteAverage, String overview) {
        this.id = id;
        this.posterPath = posterPath;
        this.title = title;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.overview = overview;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

}
