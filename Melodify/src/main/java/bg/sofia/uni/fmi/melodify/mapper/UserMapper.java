package bg.sofia.uni.fmi.melodify.mapper;

import bg.sofia.uni.fmi.melodify.dto.UserDto;
import bg.sofia.uni.fmi.melodify.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", uses = {PlaylistMapper.class, QueueMapper.class})
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "playlists", target = "playlistDtos")
    @Mapping(source = "queue", target = "queueDto")
    UserDto toDto(User userEntity);

    @Mapping(source = "playlistDtos", target = "playlists")
    @Mapping(source = "queueDto", target = "queue")
    User toEntity(UserDto userDto);

    List<UserDto> toDtoCollection(Collection<User> userEntities);

    List<User> toEntityCollection(Collection<UserDto> userDtos);
}