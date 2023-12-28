package bg.sofia.uni.fmi.melodify.mapper;

import bg.sofia.uni.fmi.melodify.dto.QueueDto;
import bg.sofia.uni.fmi.melodify.model.Queue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, SongMapper.class})
public interface QueueMapper {
    QueueMapper INSTANCE = Mappers.getMapper(QueueMapper.class);

    @Mapping(source = "owner", target = "ownerDto")
    @Mapping(source = "songs", target = "songDtos")
    QueueDto toDto(Queue queueEntity);

    @Mapping(source = "ownerDto", target = "owner")
    @Mapping(source = "songDtos", target = "songs")
    Queue toEntity(QueueDto queueDto);

    List<QueueDto> toDtoCollection(Collection<Queue> queueEntities);

    List<Queue> toEntityCollection(Collection<QueueDto> queueDtos);
}
