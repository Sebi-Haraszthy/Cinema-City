package com.Movies.Cinema.City.model;

import com.Movies.Cinema.City.config.StringListConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Movie {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String movieName;
    @Column
    private Integer price;
    @Column
    private String language;
    @Column
    private LocalDate releaseDate;
    @Column
    private Double voteAverage;
    @ManyToOne
    @JoinColumn(name = "cinema_room_id")
    @JsonBackReference(value = "cinema-movie")
    private CinemaRoom cinemaRoom;
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "movie-projection")
    private List<Projection> projectionList;
    @Convert(converter = StringListConverter.class)
    private List<String> genres;

    public Movie() {
    }

    public Long getId() {
        return id;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public CinemaRoom getCinemaRoom() {
        return cinemaRoom;
    }

    public void setCinemaRoom(CinemaRoom cinemaRoom) {
        this.cinemaRoom = cinemaRoom;
    }

    public List<Projection> getProjectionList() {
        if (this.projectionList == null) {
            this.projectionList = new ArrayList<>();
        }

        return projectionList;
    }

    public void setProjectionList(List<Projection> projectionList) {
        this.projectionList = projectionList;
    }

    public List<String> getGenres() {
        if (this.genres == null) {
            this.genres = new ArrayList<>();
        }

        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    @Override
    public String toString() {
        return "Movie: " + "id = " + id + "; movieName = " + movieName + "; price = " + price + "; language = " + language + "; releaseDate = " + releaseDate + "; voteAverage = " + voteAverage + "; cinemaRoom = " + cinemaRoom + "; projectionList = " + projectionList + "; genres = " + genres + ".";
    }
}