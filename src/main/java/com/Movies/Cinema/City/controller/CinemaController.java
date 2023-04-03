package com.Movies.Cinema.City.controller;

import com.Movies.Cinema.City.DTO.AddCinemaRoomDTO;
import com.Movies.Cinema.City.model.CinemaRoom;
import com.Movies.Cinema.City.service.CinemaRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/cinema")
public class CinemaController {
    private CinemaRoomService cinemaRoomService;

    @Autowired
    public CinemaController(CinemaRoomService cinemaRoomService) {
        this.cinemaRoomService = cinemaRoomService;
    }

    @PostMapping("/add")
    public CinemaRoom addCinemaRoom(@RequestBody AddCinemaRoomDTO addCinemaRoomDTO) {
        return cinemaRoomService.addCinemaRoom(addCinemaRoomDTO);
    }

    @GetMapping("/")
    public List<CinemaRoom> getCinemaRooms() {
        return cinemaRoomService.getCinemaRooms();
    }

    @PutMapping("/update/{cinemaRoomId}")
    public CinemaRoom updateCinemaRoom(@RequestBody AddCinemaRoomDTO addCinemaRoomDTO, @PathVariable Long cinemaRoomId) {
        return cinemaRoomService.updateCinemaRoom(addCinemaRoomDTO, cinemaRoomId);
    }

    @GetMapping("/getValueOfAllTicketsSoldByMovieAndDate/date")
    public Double getValueOfAllTicketsSoldByMovieAndDate(@RequestBody String movieName, @RequestBody LocalDate date) {
        return cinemaRoomService.getValueOfAllTicketsSoldByMovieAndDate(movieName, date);
    }

    @GetMapping("/getValueOfTicketsSoldFromAllMoviesByDay/{cinemaRoomId}/date")
    public Double getValueOfTicketsSoldFromAllMoviesByDay(@RequestBody LocalDate date) {
        return cinemaRoomService.getValueOfTicketsSoldFromAllMoviesByDay(date);
    }

    @GetMapping("/getNumberOfAllTicketsSoldByMovie/{movieId}")
    public Integer getNumberOfAllTicketsSoldByMovie(@PathVariable Long movieId) {
        return cinemaRoomService.getNumberOfAllTicketsSoldByMovie(movieId);
    }

    @GetMapping("/getNumbersOfAllTicketsSoldByCinemaRoom/{cinemaRoomId}")
    public Integer getNumbersOfAllTicketsSoldByCinemaRoom(@PathVariable Long cinemaRoomId) {
        return cinemaRoomService.getNumbersOfAllTicketsSoldByCinemaRoom(cinemaRoomId);
    }

    @DeleteMapping("/delete/{cinemaRoomId}")
    public void deleteCinemaRoom(@PathVariable Long cinemaRoomId) {
        cinemaRoomService.deleteCinemaRoom(cinemaRoomId);
    }
}