package bg.sofia.uni.fmi.melodify.service;

import bg.sofia.uni.fmi.melodify.model.Album;
import bg.sofia.uni.fmi.melodify.model.Genre;
import bg.sofia.uni.fmi.melodify.model.Song;
import bg.sofia.uni.fmi.melodify.repository.SongRepository;
import bg.sofia.uni.fmi.melodify.validation.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SongServiceTest {

    private SongService songService;

    @Mock
    private SongRepository songRepository;

    private Song firstSong;
    private Song secondSong;

    @BeforeEach
    void setTestData() {
        firstSong = new Song(11L, "Lost in Japan", 200, 7, new Genre(1L, "pop"),
            new Album(), new ArrayList<>(), "/uri");
        secondSong = new Song(10L, "In My Blood", 211, 1, new Genre(1L, "pop"),
            new Album(), new ArrayList<>(), "/uri");

        songService = new SongService(songRepository);
    }

    @Test
    void testGetSongs() {
        when(songRepository.findAll(any(Specification.class))).thenReturn(List.of(firstSong, secondSong));

        assertIterableEquals(List.of(firstSong, secondSong), songService.getSongs(new HashMap<>()),
        "The actual list of get all songs method is not the same as expected");
    }

    @Test
    void testGetSongById() {
        when(songRepository.findById(firstSong.getId())).thenReturn(Optional.of(firstSong));

        assertEquals(Optional.of(firstSong), songService.getSongById(firstSong.getId()),
            "The actual song to be returned is not the same as the expected");
    }

    @Test
    void testCreateSong() {
        when(songRepository.save(firstSong)).thenReturn(firstSong);

        assertEquals(firstSong, songService.createSong(firstSong),
            "The actual song to be created is not the same as the expected");
    }

    @Test
    void testSetSongById() {
        when(songRepository.findById(firstSong.getId())).thenReturn(Optional.of(firstSong));

        Song song = new Song(null, "Lost in UK", null, 0L, null, null, null, null);
        Song updatedSong = firstSong;
        updatedSong.setName("Lost in UK");
        songService.setSongById(song, firstSong.getId());
        verify(songRepository, times(1)).save(updatedSong);
    }

    @Test
    void testDeleteSong() {
        when(songRepository.findById(firstSong.getId())).thenReturn(Optional.of(firstSong));
        songService.deleteSong(firstSong.getId());
        verify(songRepository, times(1)).delete(firstSong);
    }

    @Test
    void testDeleteSongNoSuchSong() {
        Long idToTest = 100L;
        when(songRepository.findById(idToTest)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> songService.deleteSong(idToTest),
            "ResourceNotFoundException is expected but not thrown");
    }
}