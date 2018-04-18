package com.printezisn.moviestore.movieservice.movie.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.printezisn.moviestore.movieservice.global.mappers.InstantMapper;
import com.printezisn.moviestore.movieservice.global.mappers.UUIDMapper;
import com.printezisn.moviestore.movieservice.movie.dto.MovieDto;
import com.printezisn.moviestore.movieservice.movie.entities.Movie;
import com.printezisn.moviestore.movieservice.movie.entities.SearchedMovie;

/**
 * The mapper class for the Movie entity
 */
@Mapper(componentModel = "spring", uses = { InstantMapper.class, UUIDMapper.class })
public interface MovieMapper {

	/**
	 * Converts a Movie object to MovieDto
	 * 
	 * @param movie The Movie object to convert
	 * @return The converted MovieDto object
	 */
	MovieDto movieToMovieDto(final Movie movie);
	
	/**
	 * Converts a MovieDto object to Movie
	 * 
	 * @param movieDto The MovieDto object to convert
	 * @return The converted Movie object
	 */
	@Mapping(target = "revision", ignore = true)
	Movie movieDtoToMovie(final MovieDto movieDto);
	
	/**
	 * Converts a Movie object to SearchedMovie
	 * 
	 * @param movie The Movie object to convert
	 * @return The converted SearchedMovie object
	 */
	SearchedMovie movieToSearchedMovie(final Movie movie);
	
	/**
	 * Converts a SearchedMovie object to MovieDto
	 * 
	 * @param searchedMovie The SearchedMovie object to convert
	 * @return The converted MovieDto object
	 */
	MovieDto searchedMovieToMovieDto(final SearchedMovie searchedMovie);
}
