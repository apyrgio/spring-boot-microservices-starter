package com.printezisn.moviestore.movieservice.movie.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.printezisn.moviestore.common.AppUtils;
import com.printezisn.moviestore.common.models.PagedResult;
import com.printezisn.moviestore.common.models.Result;
import com.printezisn.moviestore.common.models.movie.MovieResultModel;
import com.printezisn.moviestore.common.dto.movie.MovieDto;
import com.printezisn.moviestore.movieservice.movie.exceptions.MovieConditionalException;
import com.printezisn.moviestore.movieservice.movie.exceptions.MovieNotFoundException;
import com.printezisn.moviestore.movieservice.movie.services.MovieService;

import lombok.RequiredArgsConstructor;

/**
 * The movies controller
 */
@RestController
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final AppUtils appUtils;

    /**
     * Searches for movies
     * 
     * @param text
     *            The text to search for
     * @param pageNumber
     *            The page number
     * @param sortField
     *            The sorting field
     * @param isAscending
     *            Indicates if sorting is ascending or descending
     * @return The movies found
     */
    @GetMapping("/movie/search")
    public ResponseEntity<?> searchMovies(
        @RequestParam(value = "text") final Optional<String> text,
        @RequestParam(value = "page") final Optional<Integer> pageNumber,
        @RequestParam(value = "sort") final Optional<String> sortField,
        @RequestParam(value = "asc", defaultValue = "true") final boolean isAscending) {

        final PagedResult<MovieDto> result = movieService.searchMovies(text, pageNumber, sortField, isAscending);

        return ResponseEntity.ok(result);
    }

    /**
     * Returns a movie
     * 
     * @param id
     *            The id of the movie
     * @return The movie
     */
    @GetMapping("/movie/get/{id}")
    public ResponseEntity<?> getMovie(@PathVariable("id") final UUID id) {
        try {
            final MovieDto result = movieService.getMovie(id);

            return ResponseEntity.ok(result);
        }
        catch (final MovieNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Creates a new movie
     * 
     * @param movieDto
     *            The movie model
     * @param bindingResult
     *            The model binding result
     * @return The created movie
     */
    @PostMapping("/movie/new")
    public ResponseEntity<?> createMovie(@Valid @RequestBody final MovieDto movieDto,
        final BindingResult bindingResult) {

        final List<String> errors = appUtils.getModelErrors(bindingResult, "id");
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(
                MovieResultModel.builder().errors(errors).build());
        }

        final MovieDto createdMovieDto = movieService.createMovie(movieDto);
        final Result<MovieDto> result = MovieResultModel.builder().result(createdMovieDto).build();

        return ResponseEntity.ok(result);
    }

    /**
     * Updates an existing movie
     * 
     * @param movieDto
     *            The movie model
     * @param bindingResult
     *            The model binding result
     * @return The updated movie
     */
    @PostMapping("/movie/update")
    public ResponseEntity<?> updateMovie(@Valid @RequestBody final MovieDto movieDto,
        final BindingResult bindingResult) {

        final List<String> errors = appUtils.getModelErrors(bindingResult, "creator");
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(
                MovieResultModel.builder().errors(errors).build());
        }

        try {
            final MovieDto updatedMovieDto = movieService.updateMovie(movieDto);
            final Result<MovieDto> result = MovieResultModel.builder().result(updatedMovieDto).build();

            return ResponseEntity.ok(result);
        }
        catch (final MovieNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
        catch (final MovieConditionalException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Deletes a movie
     * 
     * @param id
     *            The id of the movie
     * @return The result of the operation
     */
    @GetMapping("/movie/delete/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable("id") final UUID id) {
        try {
            movieService.deleteMovie(id);

            return ResponseEntity.ok().build();
        }
        catch (final MovieConditionalException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Adds a like to a movie
     * 
     * @param movieId
     *            The id of the movie to like
     * @param account
     *            The account that likes the movie
     * @return The result of the operation
     * @throws MovieConditionalException
     *             Exception thrown in case of conditional update failure
     */
    @GetMapping("/movie/like/{movieId}/{account}")
    public ResponseEntity<?> likeMovie(
        @PathVariable("movieId") final UUID movieId,
        @PathVariable("account") final String account) {

        try {
            movieService.likeMovie(movieId, account);

            return ResponseEntity.ok().build();
        }
        catch (final MovieNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
        catch (final MovieConditionalException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Removes a like from a movie
     * 
     * @param movieId
     *            The id of the movie to unlike
     * @param account
     *            The account whose like is removed from the movie
     * @return The result of the operation
     * @throws MovieConditionalException
     *             Exception thrown in case of conditional update failure
     */
    @GetMapping("/movie/unlike/{movieId}/{account}")
    public ResponseEntity<?> unlikeMovie(
        @PathVariable("movieId") final UUID movieId,
        @PathVariable("account") final String account) {

        try {
            movieService.unlikeMovie(movieId, account);

            return ResponseEntity.ok().build();
        }
        catch (final MovieNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
        catch (final MovieConditionalException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Checks if an account has liked a movie
     * 
     * @param movieId
     *            The id of the movie
     * @param account
     *            The account to check
     * @return True if the account has liked the movie, otherwise false
     */
    @GetMapping("/movie/hasliked/{movieId}/{account}")
    public ResponseEntity<?> hasLiked(
        @PathVariable("movieId") final UUID movieId,
        @PathVariable("account") final String account) {

        final boolean result = movieService.hasLiked(movieId, account);

        return ResponseEntity.ok(result);
    }
}
