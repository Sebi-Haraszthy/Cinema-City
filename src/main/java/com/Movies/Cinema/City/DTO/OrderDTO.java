package com.Movies.Cinema.City.DTO;

import java.util.ArrayList;
import java.util.List;

public class OrderDTO {
    private long projection_id;
    private List<SeatDTO> seats;

    public OrderDTO(long projection_id, List<SeatDTO> seats) {
        this.projection_id = projection_id;
        this.seats = seats;
    }

    public long getProjection_id() {
        return projection_id;
    }

    public void setProjection_id(long projection_id) {
        this.projection_id = projection_id;
    }

    public List<SeatDTO> getSeats() {
        if (this.seats == null) {
            this.seats = new ArrayList<>();
        }

        return seats;
    }

    public void setSeats(List<SeatDTO> seats) {
        this.seats = seats;
    }
}