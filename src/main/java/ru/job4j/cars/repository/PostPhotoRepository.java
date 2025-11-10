package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.PostPhoto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class PostPhotoRepository {

    private final CrudRepository crudRepository;

    /**
     * Сохранить в базе.
     *
     * @param photo фотография объявления.
     * @return фотография объявления с id.
     */
    public PostPhoto create(PostPhoto photo) {
        crudRepository.run(session -> session.persist(photo));
        return photo;
    }

    /**
     * Обновить в базе фотографию объявления.
     *
     * @param photo фотография объявления.
     */
    public void update(PostPhoto photo) {
        crudRepository.run(session -> session.merge(photo));
    }

    /**
     * Удалить фотографию объявления по id.
     *
     * @param photoId ID
     */
    public void delete(int photoId) {
        crudRepository.run(
                "DELETE FROM PostPhoto WHERE id = :fId",
                Map.of("fId", photoId)
        );
    }

    /**
     * Список всех фотографий объявления, отсортированных по id.
     *
     * @return список фотографий.
     */
    public List<PostPhoto> findAllOrderById() {
        return crudRepository.query("FROM PostPhoto ORDER BY id ASC", PostPhoto.class);
    }

    /**
     * Найти фотографию объявления по ID
     *
     * @param photoId ID фотографии
     * @return фотография объявления.
     */
    public Optional<PostPhoto> findById(int photoId) {
        return crudRepository.optional(
                "FROM PostPhoto WHERE id = :fId",
                PostPhoto.class,
                Map.of("fId", photoId)
        );
    }

    /**
     * Найти фотографии объявления по ID объявления.
     *
     * @param postId ID объявления.
     * @return список фотографий объявления.
     */
    public List<PostPhoto> findByPostId(int postId) {
        return crudRepository.query(
                "FROM PostPhoto WHERE post.id = :postId ORDER BY id ASC",
                PostPhoto.class,
                Map.of("postId", postId)
        );
    }
}