package bg.sofia.uni.fmi.melodify.mapper;

import bg.sofia.uni.fmi.melodify.dto.PlaylistDto;
import bg.sofia.uni.fmi.melodify.model.Playlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", uses = {SongMapper.class})
public interface PlaylistMapper {
    PlaylistMapper INSTANCE = Mappers.getMapper(PlaylistMapper.class);

    @Mapping(source = "songs", target = "songDtos")
    PlaylistDto toDto(Playlist playlistEntity);

    @Mapping(source = "songDtos", target = "songs")
    Playlist toEntity(PlaylistDto playlistDto);

    List<PlaylistDto> toDtoCollection(Collection<Playlist> playlistEntities);

    List<Playlist> toEntityCollection(Collection<PlaylistDto> playlistDtos);
}