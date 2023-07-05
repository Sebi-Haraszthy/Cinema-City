package com.Movies.Cinema.City.controller;

import com.Movies.Cinema.City.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    private TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/totalPrice/{day}")
    public Long getValueOfTicketsSoldByDay(@PathVariable("day") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate day) {
        return ticketService.getValueOfTicketsSoldByDay(day);
    }

    @GetMapping("/totalPrice/{day}/{movie_name}")
    public Long getValueOfTicketsSoldByDayAndMovieName(@PathVariable("day") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate day, @PathVariable String movie_name) {
        return ticketService.getValueOfTicketsSoldByDayAndMovieName(day, movie_name);
    }

    @GetMapping("/totalTickets/{day}")
    public Integer getNumberOfTicketsSoldByDay(@PathVariable LocalDate day) {
        return ticketService.getNumberOfTicketsSoldByDay(day);
    }

    @GetMapping("/totalTickets/{day}/{movie_name}")
    public Integer getNumberOfTicketsSoldByDayAndMovieName(@PathVariable LocalDate day, @PathVariable String movie_name) {
        return ticketService.getNumberOfTicketsSoldByDayAndMovieName(day, movie_name);
    }
}