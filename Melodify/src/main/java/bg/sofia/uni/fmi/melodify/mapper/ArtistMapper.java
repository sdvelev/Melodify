package bg.sofia.uni.fmi.melodify.mapper;

import bg.sofia.uni.fmi.melodify.dto.ArtistDto;
import bg.sofia.uni.fmi.melodify.model.Artist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ArtistMapper {
    ArtistMapper INSTANCE = Mappers.getMapper(ArtistMapper.class);

    @Mapping(source = "songs", target = "songDtos")
    @Mapping(source = "albums", target = "albumDtos")
    ArtistDto toDto(Artist artistEntity);

    @Mapping(source = "songDtos", target = "songs")
    @Mapping(source = "albumDtos", target = "albums")
    Artist toEntity(ArtistDto albumDto);

    List<ArtistDto> toDtoCollection(Collection<Artist> albumEntities);

    List<Artist> toEntityCollection(Collection<ArtistDto> artistDtos);
}