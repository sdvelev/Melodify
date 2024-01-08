package bg.sofia.uni.fmi.melodify.mapper;

import bg.sofia.uni.fmi.melodify.dto.GenreDto;
import bg.sofia.uni.fmi.melodify.model.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    GenreMapper INSTANCE = Mappers.getMapper(GenreMapper.class);

    GenreDto toDto(Genre genreEntity);

    Genre toEntity(GenreDto genreDto);

    List<GenreDto> toDtoCollection(Collection<Genre> genreEntities);

    List<Genre> toEntityCollection(Collection<GenreDto> genreDtos);
}