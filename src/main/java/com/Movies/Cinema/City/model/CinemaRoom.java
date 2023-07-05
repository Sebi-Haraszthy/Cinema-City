package com.Movies.Cinema.City.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CinemaRoom {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private int numberOfRows;
    @Column
    private int numberOfColumns;
    @OneToMany(mappedBy = "cinemaRoom", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "cinema-movie")
    private List<Movie> movieList;
    @OneToMany(mappedBy = "cinemaRoom", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "cinema-seat")
    private List<Seat> seatList;

    public CinemaRoom() {
    }

    public Long getId() {
        return id;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }

    public List<Movie> getMovieList() {
        if (this.movieList == null) {
            this.movieList = new ArrayList<>();
        }

        return movieList;
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
    }

    public List<Seat> getSeatList() {
        if (this.seatList == null) {
            this.seatList = new ArrayList<>();
        }

        return seatList;
    }

    public void setSeatList(List<Seat> seatList) {
        this.seatList = seatList;
    }

    @Override
    public String toString() {
        return "CinemaRoom: " + "id = " + id + "; numberOfRows = " + numberOfRows + "; numberOfColumns = " + numberOfColumns + "; movieList = " + movieList + "; seatList = " + seatList + ".";
    }
}