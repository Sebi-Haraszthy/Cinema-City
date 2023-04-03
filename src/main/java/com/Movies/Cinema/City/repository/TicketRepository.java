package com.Movies.Cinema.City.repository;

import com.Movies.Cinema.City.model.Projection;
import com.Movies.Cinema.City.model.Seat;
import com.Movies.Cinema.City.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Ticket findByProjectionAndSeat(Projection projection, Seat seat);
}