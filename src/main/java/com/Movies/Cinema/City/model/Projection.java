package com.Movies.Cinema.City.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Projection {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private LocalDateTime startTime;
    @Column
    private LocalDateTime endTime;
    @ManyToOne
    @JoinColumn(name = "movie_id")
    @JsonBackReference(value = "movie-projection")
    private Movie movie;
    @OneToMany(mappedBy = "projection", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "projection-ticket")
    private List<Ticket> ticketList;

    public Projection() {
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public List<Ticket> getTicketList() {
        if (this.ticketList == null) {
            this.ticketList = new ArrayList<>();
        }

        return ticketList;
    }

    public void setTicketList(List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }

    @Override
    public String toString() {
        return "Projection: " + "id = " + id + "; startTime = " + startTime + "; endTime = " + endTime + "; movie = " + movie + "; ticketList = " + ticketList + ".";
    }
}