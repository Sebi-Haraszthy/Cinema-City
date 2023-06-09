package com.Movies.Cinema.City.service;

import com.Movies.Cinema.City.DTO.OrderDTO;
import com.Movies.Cinema.City.DTO.SeatDTO;
import com.Movies.Cinema.City.model.*;
import com.Movies.Cinema.City.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Service
@Transactional
public class OrderService {
    public static final String ORDER_MAIL_SUBJECT = "Your tickets to the movie";
    private final UserRepository userRepository;
    private final ProjectionRepository projectionRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final OrderRepository orderRepository;
    private final MailService mailService;

    @Autowired
    public OrderService(UserRepository userRepository, ProjectionRepository projectionRepository, SeatRepository seatRepository, TicketRepository ticketRepository, OrderRepository orderRepository, MailService mailService) {
        this.userRepository = userRepository;
        this.projectionRepository = projectionRepository;
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
        this.orderRepository = orderRepository;
        this.mailService = mailService;
    }

    public Order buyTicket(OrderDTO orderDTO) throws Throwable {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User foundUser = userRepository.findUserByUsername(userDetails.getUsername()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The user was not found!"));
        Order newOrder = new Order();
        newOrder.setCreatedDate(new Date());
        newOrder.setUser(foundUser);
        double totalPriceOrder = 0.0;
        Projection foundProjection = projectionRepository.findById(orderDTO.getProjection_id()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The projection was not found!"));

        for (SeatDTO seatDTO : orderDTO.getSeats()) {
            Seat foundSeat = seatRepository.findBySeatRowAndSeatColumnAndCinemaRoom(seatDTO.getRow(), seatDTO.getColumn(), foundProjection.getMovie().getCinemaRoom());
            Ticket foundTicket = ticketRepository.findByProjectionAndSeat(foundProjection, foundSeat);

            if (!foundTicket.getAvailable()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The seat at: " + foundSeat.getSeatRow() + " " + foundSeat.getSeatColumn() + " is not available!");
            }

            totalPriceOrder += foundProjection.getMovie().getPrice() + foundSeat.getExtraPrice();
            foundTicket.setAvailable(false);
            newOrder.getTicketList().add(foundTicket);
            foundTicket.setOrder(newOrder);
        }

        newOrder.setTotalPrice(totalPriceOrder);
        mailService.sendOrderConfirmationMessage(foundUser.getEmail(), newOrder);

        return orderRepository.save(newOrder);
    }
}