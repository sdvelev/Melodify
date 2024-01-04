package bg.sofia.uni.fmi.melodify.mapper;

import bg.sofia.uni.fmi.melodify.dto.SongDto;
import bg.sofia.uni.fmi.melodify.model.Song;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ArtistMapper.class, GenreMapper.class})
public interface SongMapper {
    SongMapper INSTANCE = Mappers.getMapper(SongMapper.class);

   @Mapping(source = "genre", target = "genreDto")
//    @Mapping(source = "album", target = "albumDto")
   @Mapping(source = "artists", target = "artistDtos")
   @Mapping(target = "albumImage", source = "album.image")
   @Mapping(target = "albumName", source = "album.name")
   @Mapping(target = "albumId", source = "album.id")
   SongDto toDto(Song songEntity);

     @Mapping(source = "genreDto", target = "genre")
//    @Mapping(source = "albumDto", target = "album")
       @Mapping(source = "artistDtos", target = "artists")
    Song toEntity(SongDto songDto);

    List<SongDto> toDtoCollection(Collection<Song> songEntities);

    List<Song> toEntityCollection(Collection<SongDto> songDtos);
}