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

    @PutMapping("/update/{cinema_room_id}")
    public CinemaRoom updateCinemaRoom(@RequestBody AddCinemaRoomDTO addCinemaRoomDTO, @PathVariable Long cinema_room_id) {
        return cinemaRoomService.updateCinemaRoom(addCinemaRoomDTO, cinema_room_id);
    }

    @GetMapping("/getValueOfAllTicketsSoldByMovieAndDate/date")
    public Double getValueOfAllTicketsSoldByMovieAndDate(@RequestBody String movieName, @RequestBody LocalDate date) {
        return cinemaRoomService.getValueOfAllTicketsSoldByMovieAndDate(movieName, date);
    }

    @GetMapping("/getValueOfTicketsSoldFromAllMoviesByDay/date")
    public Double getValueOfTicketsSoldFromAllMoviesByDay(@RequestBody LocalDate date) {
        return cinemaRoomService.getValueOfTicketsSoldFromAllMoviesByDay(date);
    }

    @GetMapping("/getNumberOfAllTicketsSoldByMovie/{movie_id}")
    public Integer getNumberOfAllTicketsSoldByMovie(@PathVariable Long movie_id) {
        return cinemaRoomService.getNumberOfAllTicketsSoldByMovie(movie_id);
    }

    @GetMapping("/getNumbersOfAllTicketsSoldByCinemaRoom/{cinema_room_id}")
    public Integer getNumbersOfAllTicketsSoldByCinemaRoom(@PathVariable Long cinema_room_id) {
        return cinemaRoomService.getNumbersOfAllTicketsSoldByCinemaRoom(cinema_room_id);
    }

    @DeleteMapping("/delete/{cinema_room_id}")
    public void deleteCinemaRoom(@PathVariable Long cinema_room_id) {
        cinemaRoomService.deleteCinemaRoom(cinema_room_id);
    }
}