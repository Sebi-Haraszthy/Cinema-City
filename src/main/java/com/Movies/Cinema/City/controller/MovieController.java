package com.Movies.Cinema.City.controller;

import com.Movies.Cinema.City.DTO.AddMovieDTO;
import com.Movies.Cinema.City.DTO.UpdateMovieDTO;
import com.Movies.Cinema.City.model.Movie;
import com.Movies.Cinema.City.model.Projection;
import com.Movies.Cinema.City.service.MovieService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movie")
public class MovieController {
    private MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping("/add")
    public Movie addMovie(@RequestBody AddMovieDTO addMovieDTO) throws JsonProcessingException {
        return movieService.addMovie(addMovieDTO);
    }

    @GetMapping("/projections-available/{movie_id}")
    public List<Projection> getAllProjectionsAvailable(@PathVariable Long movie_id) {
        return movieService.getAllProjectionsAvailable(movie_id);
    }

    @PutMapping("/update/{movie_id}")
    public Movie updateMovie(@RequestBody UpdateMovieDTO updateMovieDTO, @PathVariable Long movie_id) {
        return movieService.updateMovie(updateMovieDTO, movie_id);
    }

    @DeleteMapping("/delete/{movie_id}")
    public void deleteMovie(@PathVariable Long movie_id) {
        movieService.deleteMovie(movie_id);
    }

    @GetMapping("/recommendations")
    public List<String> getAllProjectionsAvailable() {
        return movieService.getMovieRecommendations();
    }
}