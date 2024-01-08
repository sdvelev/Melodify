package bg.sofia.uni.fmi.melodify.model;

import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ModelContentTest {

    @Test
    public void testJpaAnnotationAlbum() throws NoSuchFieldException {
        Class<?> entityClass = Album.class;
        String fieldName = "id";
        Class<? extends Annotation> annotationClass = Id.class;

        Field field = entityClass.getDeclaredField(fieldName);

        assertTrue(field.isAnnotationPresent(annotationClass),
            "The field '" + fieldName + "' should be annotated with @" + annotationClass.getSimpleName());
    }

    @Test
    public void testJpaAnnotationArtist() throws NoSuchFieldException {
        Class<?> entityClass = Artist.class;
        String fieldName = "id";
        Class<? extends Annotation> annotationClass = Id.class;

        Field field = entityClass.getDeclaredField(fieldName);

        assertTrue(field.isAnnotationPresent(annotationClass),
            "The field '" + fieldName + "' should be annotated with @" + annotationClass.getSimpleName());
    }

    @Test
    public void testJpaAnnotationGenre() throws NoSuchFieldException {
        Class<?> entityClass = Genre.class;
        String fieldName = "id";
        Class<? extends Annotation> annotationClass = Id.class;

        Field field = entityClass.getDeclaredField(fieldName);

        assertTrue(field.isAnnotationPresent(annotationClass),
            "The field '" + fieldName + "' should be annotated with @" + annotationClass.getSimpleName());
    }

    @Test
    public void testJpaAnnotationPlaylist() throws NoSuchFieldException {
        Class<?> entityClass = Playlist.class;
        String fieldName = "id";
        Class<? extends Annotation> annotationClass = Id.class;

        Field field = entityClass.getDeclaredField(fieldName);

        assertTrue(field.isAnnotationPresent(annotationClass),
            "The field '" + fieldName + "' should be annotated with @" + annotationClass.getSimpleName());
    }

    @Test
    public void testJpaAnnotationQueue() throws NoSuchFieldException {
        Class<?> entityClass = Queue.class;
        String fieldName = "id";
        Class<? extends Annotation> annotationClass = Id.class;

        Field field = entityClass.getDeclaredField(fieldName);

        assertTrue(field.isAnnotationPresent(annotationClass),
            "The field '" + fieldName + "' should be annotated with @" + annotationClass.getSimpleName());
    }

    @Test
    public void testJpaAnnotationSong() throws NoSuchFieldException {
        Class<?> entityClass = Song.class;
        String fieldName = "id";
        Class<? extends Annotation> annotationClass = Id.class;

        Field field = entityClass.getDeclaredField(fieldName);

        assertTrue(field.isAnnotationPresent(annotationClass),
            "The field '" + fieldName + "' should be annotated with @" + annotationClass.getSimpleName());
    }

    @Test
    public void testJpaAnnotationUser() throws NoSuchFieldException {
        Class<?> entityClass = User.class;
        String fieldName = "id";
        Class<? extends Annotation> annotationClass = Id.class;

        Field field = entityClass.getDeclaredField(fieldName);

        assertTrue(field.isAnnotationPresent(annotationClass),
            "The field '" + fieldName + "' should be annotated with @" + annotationClass.getSimpleName());
    }
}