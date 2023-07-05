package com.Movies.Cinema.City.service;

import com.Movies.Cinema.City.DTO.AddMovieDTO;
import com.Movies.Cinema.City.DTO.ProjectionsDTO;
import com.Movies.Cinema.City.DTO.UpdateMovieDTO;
import com.Movies.Cinema.City.model.*;
import com.Movies.Cinema.City.repository.CinemaRoomRepository;
import com.Movies.Cinema.City.repository.MovieRepository;
import com.Movies.Cinema.City.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private MovieRepository movieRepository;
    private CinemaRoomRepository cinemaRoomRepository;
    private RestTemplate restTemplate;
    private UserService userService;
    private OrderRepository orderRepository;
    private OpenAiService openAiService;
    private static final String FIND_MOVIE_BY_NAME_URL = "https://api.themoviedb.org/3/search/movie?api_key={APIkey}&language=en-US&query={movie_name}&page=1&include_adult=false";
    private static final String FIND_MOVIE_DETAILS_BY_ID_URL = "https://api.themoviedb.org/3/movie/{movie_id}?api_key={APIkey}&language=en-US";
    @Value("${api.themoviedb.key}")
    private String apiKey;

    @Autowired
    public MovieService(MovieRepository movieRepository, CinemaRoomRepository cinemaRoomRepository, RestTemplate restTemplate, UserService userService, OrderRepository orderRepository, OpenAiService openAiService) {
        this.movieRepository = movieRepository;
        this.cinemaRoomRepository = cinemaRoomRepository;
        this.restTemplate = restTemplate;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.openAiService = openAiService;
    }

    public Movie addMovie(AddMovieDTO addMovieDTO) throws JsonProcessingException {
        CinemaRoom foundCinemaRoom = cinemaRoomRepository.findById(addMovieDTO.getCinemaRoomId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The cinema was not found!"));
        Optional<Movie> foundMovie = movieRepository.findByMovieName(addMovieDTO.getMovieName());

        if (foundMovie.isPresent()) {
            throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED, "This movie already exists.");
        }

        Movie movieToBeAdded = new Movie();
        movieToBeAdded.setPrice(addMovieDTO.getPrice());
        movieToBeAdded.setCinemaRoom(foundCinemaRoom);
        JsonNode responseMovieBody = getResponseBodyJson(FIND_MOVIE_BY_NAME_URL, addMovieDTO.getMovieName());
        addMovieDetails(movieToBeAdded, responseMovieBody);
        JsonNode responseMovieDetailsBody = getResponseBodyJson(FIND_MOVIE_DETAILS_BY_ID_URL, String.valueOf(responseMovieBody.path("results").get(0).path("id").asInt()));
        addMovieGenres(movieToBeAdded, responseMovieDetailsBody);
        generateProjections(addMovieDTO, foundCinemaRoom, movieToBeAdded);

        return movieRepository.save(movieToBeAdded);
    }

    public List<String> getMovieRecommendations() {
        User loggedInUser = userService.findLoggedInUser();
        List<Order> userOrders = orderRepository.findOrderByUserId(loggedInUser.getId());
        List<String> userGenres = getUserGenresBy(userOrders);
        Map<String, Integer> genreFrequencies = getGenreFrequenciesBy(userGenres);
        String favoriteGenre = getFavoriteGenre(genreFrequencies);

        return getCompletion(favoriteGenre);
    }

    private List<String> getUserGenresBy(List<Order> userOrders) {
        return userOrders.stream()
                .flatMap(order -> order.getTicketList().stream())
                .map(Ticket::getProjection)
                .map((Projection::getMovie))
                .flatMap((movie -> movie.getGenres().stream()))
                .collect(Collectors.toList());
    }

    private Map<String, Integer> getGenreFrequenciesBy(List<String> userGenres) {
        Map<String, Integer> genreFrequencies = new HashMap<>();

        for (String userGenre : userGenres) {
            if (genreFrequencies.containsKey(userGenre)) {
                genreFrequencies.put(userGenre, genreFrequencies.get(userGenre) + 1);
            } else {
                genreFrequencies.put(userGenre, 1);
            }
        }

        return genreFrequencies;
    }

    private String getFavoriteGenre(Map<String, Integer> genreFrequencies) {
        Integer max = 0;
        String favoriteGenre = "";

        for (Map.Entry<String, Integer> genreFrequency : genreFrequencies.entrySet()) {
            if (genreFrequency.getValue() > max) {
                favoriteGenre = genreFrequency.getKey();
            }
        }

        return favoriteGenre;
    }

    public List<String> getCompletion(String genre) {
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt("Give me 10 movie recommendations for genre: " + genre)
                .model("text-davinci-003")
                .echo(true)
                .build();

        return openAiService.createCompletion(completionRequest).getChoices().stream().map(CompletionChoice::getText).collect(Collectors.toList());
    }

    private void generateProjections(AddMovieDTO addMovieDTO, CinemaRoom foundCinemaRoom, Movie movieToBeAdded) {
        addMovieDTO.getDates().forEach(projectionsDTO -> {
            Optional<Projection> interferingProjection = canProjectionBeAdded(foundCinemaRoom, projectionsDTO);

            if (interferingProjection.isPresent()) {
                throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED, "There is already a projection between the following dates: " + " " + interferingProjection.get().getStartTime() + "-" + interferingProjection.get().getEndTime());
            }

            Projection projection = new Projection();
            projection.setStartTime(projectionsDTO.getStartTime());
            projection.setEndTime(projectionsDTO.getEndTime());
            projection.setMovie(movieToBeAdded);
            movieToBeAdded.getProjectionList().add(projection);
            generateTicketsForProjection(foundCinemaRoom, projection);
        });
    }

    private void generateTicketsForProjection(CinemaRoom foundCinemaRoom, Projection projection) {
        for (Seat seat : foundCinemaRoom.getSeatList()) {
            Ticket ticket = new Ticket();
            ticket.setAvailable(true);
            ticket.setProjection(projection);
            projection.getTicketList().add(ticket);
            ticket.setSeat(seat);
        }
    }

    private Optional<Projection> canProjectionBeAdded(CinemaRoom foundCinemaRoom, ProjectionsDTO projectionsDTO) {
        for (Movie movie : foundCinemaRoom.getMovieList()) {
            for (Projection existingProjection : movie.getProjectionList()) {
                if (!(projectionsDTO.getEndTime().isBefore(existingProjection.getStartTime()) || projectionsDTO.getStartTime().isAfter(existingProjection.getEndTime()))) {
                    return Optional.of(existingProjection);
                }
            }
        }

        return Optional.empty();
    }

    private void addMovieGenres(Movie movieToBeAdded, JsonNode responseMovieDetailsBody) {
        List<String> genres = new ArrayList<>();
        ArrayNode genresJson = (ArrayNode) responseMovieDetailsBody.path("genres");

        for (JsonNode genreJson : genresJson) {
            genres.add(genreJson.path("name").asText());
        }

        movieToBeAdded.setGenres(genres);
    }

    private void addMovieDetails(Movie movieToBeAdded, JsonNode responseMovieBody) {
        if (responseMovieBody.path("results").isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot add a movie with this name.");
        }

        JsonNode firstResult = responseMovieBody.path("results").get(0);
        String releaseDateText = firstResult.path("release_date").asText();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate releaseDate = LocalDate.parse(releaseDateText, formatter);
        movieToBeAdded.setLanguage(firstResult.path("original_language").asText());
        movieToBeAdded.setVoteAverage(firstResult.path("vote_average").asDouble());
        movieToBeAdded.setReleaseDate(releaseDate);
        movieToBeAdded.setMovieName(firstResult.path("original_title").asText());
    }

    public JsonNode getResponseBodyJson(String requestBaseUrl, String movieName) throws JsonProcessingException {
        URI uri = new UriTemplate(requestBaseUrl).expand(apiKey, movieName);

        return makeAPICall(uri);
    }

    public JsonNode getResponseBodyJson(String requestBaseUrl, Integer movie_id) throws JsonProcessingException {
        URI uri = new UriTemplate(requestBaseUrl).expand(apiKey, movie_id);

        return makeAPICall(uri);
    }

    private JsonNode makeAPICall(URI uri) throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readTree(response.getBody());
    }

    public List<Projection> getAllProjectionsAvailable(Long movieId) {
        Movie foundMovie = movieRepository.findById(movieId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The movie was not found!"));

        return foundMovie.getProjectionList().stream()
                .filter(projection -> projection.getStartTime().isAfter(LocalDateTime.now()))
                .filter(this::hasProjectionAvailableTickets)
                .collect(Collectors.toList());
    }

    public boolean hasProjectionAvailableTickets(Projection projection) {
        return projection.getTicketList().stream()
                .anyMatch(Ticket::getAvailable);
    }

    public Movie updateMovie(UpdateMovieDTO updateMovieDTO, Long movieId) {
        Movie foundMovie = movieRepository.findById(movieId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The movie was not found!"));
        foundMovie.setPrice(updateMovieDTO.getPrice());

        return movieRepository.save(foundMovie);
    }

    public void deleteMovie(Long movieId) {
        Movie foundMovie = movieRepository.findById(movieId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The movie was not found!"));
        foundMovie.getProjectionList().clear();
        movieRepository.delete(foundMovie);
    }
}