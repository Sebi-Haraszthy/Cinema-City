package com.Movies.Cinema.City.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Seat {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private Integer seatRow;
    @Column
    private Integer seatColumn;
    @Column
    private Integer extraPrice;
    @ManyToOne
    @JoinColumn(name = "cinema_room_id")
    @JsonBackReference(value = "cinema-seat")
    private CinemaRoom cinemaRoom;
    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "seat-ticket")
    private List<Ticket> ticketList;

    public Seat() {
    }

    public Long getId() {
        return id;
    }

    public Integer getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(Integer seatRow) {
        this.seatRow = seatRow;
    }

    public Integer getSeatColumn() {
        return seatColumn;
    }

    public void setSeatColumn(Integer seatColumn) {
        this.seatColumn = seatColumn;
    }

    public Integer getExtraPrice() {
        return extraPrice;
    }

    public void setExtraPrice(Integer extraPrice) {
        this.extraPrice = extraPrice;
    }

    public CinemaRoom getCinemaRoom() {
        return cinemaRoom;
    }

    public void setCinemaRoom(CinemaRoom cinemaRoom) {
        this.cinemaRoom = cinemaRoom;
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
        return "Seat: " + "id = " + id + "; seatRow = " + seatRow + "; seatColumn = " + seatColumn + "; extraPrice = " + extraPrice + "; cinemaRoom = " + cinemaRoom + "; ticketList = " + ticketList + ".";
    }
}