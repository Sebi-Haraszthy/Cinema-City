package com.Movies.Cinema.City.service;

import com.Movies.Cinema.City.DTO.AddCinemaRoomDTO;
import com.Movies.Cinema.City.DTO.ExtraPriceDTO;
import com.Movies.Cinema.City.model.*;
import com.Movies.Cinema.City.repository.CinemaRoomRepository;
import com.Movies.Cinema.City.repository.MovieRepository;
import com.Movies.Cinema.City.repository.ProjectionRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CinemaRoomService {
    private CinemaRoomRepository cinemaRoomRepository;
    private MovieRepository movieRepository;
    private ProjectionRepository projectionRepository;

    @Autowired
    public CinemaRoomService(CinemaRoomRepository cinemaRoomRepository, MovieRepository movieRepository, ProjectionRepository projectionRepository) {
        this.cinemaRoomRepository = cinemaRoomRepository;
        this.movieRepository = movieRepository;
        this.projectionRepository = projectionRepository;
    }

    public CinemaRoom addCinemaRoom(AddCinemaRoomDTO addCinemaRoomDTO) {
        CinemaRoom cinemaRoom = new CinemaRoom();
        cinemaRoom.setNumberOfRows(addCinemaRoomDTO.getNumberOfRows());
        cinemaRoom.setNumberOfColumns(addCinemaRoomDTO.getNumberOfColumns());
        generateSeatsForCinemaRoom(addCinemaRoomDTO, cinemaRoom);
        generateExtraPricesForCinemaRoom(addCinemaRoomDTO, cinemaRoom);

        return cinemaRoomRepository.save(cinemaRoom);
    }

    private void generateSeatsForCinemaRoom(AddCinemaRoomDTO addCinemaRoomDTO, CinemaRoom cinemaRoom) {
        for (int i = 0; i < addCinemaRoomDTO.getNumberOfRows(); i++) {
            for (int j = 0; j < addCinemaRoomDTO.getNumberOfColumns(); j++) {
                Seat seat = new Seat();
                seat.setSeatRow(i + 1);
                seat.setSeatColumn(j + 1);
                seat.setExtraPrice(0);
                cinemaRoom.getSeatList().add(seat);
                seat.setCinemaRoom(cinemaRoom);
            }
        }
    }

    private void generateExtraPricesForCinemaRoom(AddCinemaRoomDTO addCinemaRoomDTO, CinemaRoom cinemaRoom) {
        for (ExtraPriceDTO extraPriceDTO : addCinemaRoomDTO.getExtraPrices()) {
            for (int i = extraPriceDTO.getStartingRow(); i <= extraPriceDTO.getEndingRow(); i++) {
                for (int j = 0; j < addCinemaRoomDTO.getNumberOfColumns(); j++) {
                    Seat seat = getSeatByRowAndColumn(cinemaRoom, i, j + 1).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The seat was not found!"));
                    seat.setExtraPrice(extraPriceDTO.getExtraPrice());
                }
            }
        }
    }

    private Optional<Seat> getSeatByRowAndColumn(CinemaRoom cinemaRoom, Integer row, Integer column) {
        return cinemaRoom.getSeatList().stream()
                .filter((seat -> Objects.equals(seat.getSeatRow(), row) && Objects.equals(seat.getSeatColumn(), column)))
                .findFirst();
    }

    public List<CinemaRoom> getCinemaRooms() {
        return cinemaRoomRepository.findAll();
    }

    public CinemaRoom updateCinemaRoom(AddCinemaRoomDTO addCinemaRoomDTO, Long cinemaRoomId) {
        CinemaRoom foundCinemaRoom = cinemaRoomRepository.findById(cinemaRoomId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The cinema was not found!"));
        foundCinemaRoom.setNumberOfRows(addCinemaRoomDTO.getNumberOfRows());
        foundCinemaRoom.setNumberOfColumns(addCinemaRoomDTO.getNumberOfColumns());

        return cinemaRoomRepository.save(foundCinemaRoom);
    }

    public Double getValueOfAllTicketsSoldByMovieAndDate(String movieName, LocalDate date) {
        Movie foundMovie = movieRepository.findByMovieName(movieName).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The movie was not found!"));
        Optional<Integer> totalValueOfTickets = computeProjectionsTotalPriceByDate(date, foundMovie.getProjectionList());

        if (totalValueOfTickets.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Price could not be calculated!");
        }

        return totalValueOfTickets.get().doubleValue();
    }

    private Optional<Integer> computeProjectionsTotalPriceByDate(LocalDate date, List<Projection> projectionList) {
        return getTotalValueOfTickets(date, projectionList);
    }

    @NotNull
    private Optional<Integer> getTotalValueOfTickets(LocalDate date, List<Projection> projectionList) {
        Optional<Integer> totalValueOfTickets = projectionList.stream()
                .filter(projection -> projection.getStartTime().toLocalDate().equals(date))
                .flatMap(projection -> projection.getTicketList().stream())
                .filter(ticket -> !ticket.getAvailable())
                .map(ticket -> ticket.getSeat().getExtraPrice() + ticket.getProjection().getMovie().getPrice())
                .reduce(Integer::sum);

        return totalValueOfTickets;
    }

    public Double getValueOfTicketsSoldFromAllMoviesByDay(LocalDate date) {
        List<Projection> allProjectionList = projectionRepository.findAll();
        Optional<Integer> totalValueOfTickets = computeProjectionsTotalPriceByDay(date, allProjectionList);

        if (totalValueOfTickets.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Price could not be calculated!");
        }

        return totalValueOfTickets.get().doubleValue();
    }

    private Optional<Integer> computeProjectionsTotalPriceByDay(LocalDate date, List<Projection> projectionList) {
        return getTotalValueOfTickets(date, projectionList);
    }

    public Integer getNumberOfAllTicketsSoldByMovie(Long movieId) {
        int totalTicketsNumber = 0;
        Movie foundMovie = movieRepository.findById(movieId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The movie was not found!"));

        for (Projection projection : foundMovie.getProjectionList()) {
            for (Ticket ticket : projection.getTicketList()) {
                if (ticket.getAvailable().equals(false)) {
                    totalTicketsNumber += 1;
                }
            }
        }

        return totalTicketsNumber;
    }

    public Integer getNumbersOfAllTicketsSoldByCinemaRoom(Long cinemaRoomId) {
        int totalNumberOfTicketsSold = 0;
        CinemaRoom foundCinemaRoom = cinemaRoomRepository.findById(cinemaRoomId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The cinema was not found!"));

        for (Movie movie : foundCinemaRoom.getMovieList()) {
            for (Projection projection : movie.getProjectionList()) {
                for (Ticket ticket : projection.getTicketList()) {
                    if (ticket.getAvailable().equals(false)) {
                        totalNumberOfTicketsSold += 1;
                    }
                }
            }
        }

        return totalNumberOfTicketsSold;
    }

    public void deleteCinemaRoom(Long cinemaRoomId) {
        CinemaRoom foundCinemaRoom = cinemaRoomRepository.findById(cinemaRoomId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The cinema was not found!"));
        cinemaRoomRepository.delete(foundCinemaRoom);
    }
}