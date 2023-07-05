package com.Movies.Cinema.City.service;

import com.Movies.Cinema.City.model.Movie;
import com.Movies.Cinema.City.model.Projection;
import com.Movies.Cinema.City.model.Ticket;
import com.Movies.Cinema.City.repository.MovieRepository;
import com.Movies.Cinema.City.repository.ProjectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketService {
    private MovieRepository movieRepository;
    private ProjectionRepository projectionRepository;

    @Autowired
    public TicketService(MovieRepository movieRepository, ProjectionRepository projectionRepository) {
        this.movieRepository = movieRepository;
        this.projectionRepository = projectionRepository;
    }

    public Long getValueOfTicketsSoldByDay(LocalDate certainDay) {
        List<Projection> allProjections = projectionRepository.findAll();
        Optional<Integer> valueOfTicketsSoldByDay = computeProjectionsTotalPriceByDay(certainDay, allProjections);

        if (valueOfTicketsSoldByDay.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Price could not be calculated!");
        }

        return valueOfTicketsSoldByDay.get().longValue();
    }

    private Optional<Integer> computeProjectionsTotalPriceByDay(LocalDate certainDay, List<Projection> projectionList) {
        List<Ticket> soldTickets = getSoldTicketsForProjectionsByDay(certainDay, projectionList);

        return soldTickets.stream().
                map(this::computeTicketPrice).
                reduce(Integer::sum);
    }

    private int computeTicketPrice(Ticket ticket) {
        return ticket.getSeat().getExtraPrice() + ticket.getProjection().getMovie().getPrice();
    }

    private List<Ticket> getSoldTicketsForProjectionsByDay(LocalDate certainDay, List<Projection> projectionList) {
        return projectionList.stream()
                .filter(projection -> projection.getStartTime().toLocalDate().equals(certainDay))
                .flatMap(projection -> projection.getTicketList().stream())
                .filter(ticket -> !ticket.getAvailable())
                .collect(Collectors.toList());
    }

    public Long getValueOfTicketsSoldByDayAndMovieName(LocalDate certainDay, String movieName) {
        Movie foundMovie = movieRepository.findByMovieName(movieName).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The movie was not found!"));
        Optional<Integer> valueOfTicketsSoldByDayAndMovieName = computeProjectionsTotalPriceByDay(certainDay, foundMovie.getProjectionList());

        if (valueOfTicketsSoldByDayAndMovieName.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Price could not be calculated!");
        }

        return valueOfTicketsSoldByDayAndMovieName.get().longValue();
    }

    private Long computeProjectionsTotalTicketsByDay(LocalDate certainDay, List<Projection> allProjections) {
        return (long) getSoldTicketsForProjectionsByDay(certainDay, allProjections).size();
    }

    public Integer getNumberOfTicketsSoldByDay(LocalDate certainDay) {
        List<Projection> allProjections = projectionRepository.findAll();

        return computeProjectionsTotalTicketsByDay(certainDay, allProjections).intValue();
    }

    public Integer getNumberOfTicketsSoldByDayAndMovieName(LocalDate certainDay, String movieName) {
        Movie foundMovie = movieRepository.findByMovieName(movieName).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The movie was not found!"));

        return computeProjectionsTotalTicketsByDay(certainDay, foundMovie.getProjectionList()).intValue();
    }
}