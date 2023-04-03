package com.Movies.Cinema.City.repository;

import com.Movies.Cinema.City.model.CinemaRoom;
import com.Movies.Cinema.City.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    Seat findBySeatRowAndSeatColumnAndCinemaRoom(Integer row, Integer column, CinemaRoom cinemaRoom);
}