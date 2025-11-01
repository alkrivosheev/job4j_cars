package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.Post;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class PostRepository {

    private final CrudRepository crudRepository;

    /**
     * Сохранить в базе.
     *
     * @param post объявление.
     * @return объявление с id.
     */
    public Post create(Post post) {
        crudRepository.run(session -> session.persist(post));
        return post;
    }

    /**
     * Обновить в базе объявление.
     *
     * @param post объявление.
     */
    public void update(Post post) {
        crudRepository.run(session -> session.merge(post));
    }

    /**
     * Удалить объявление по id.
     *
     * @param postId ID
     */
    public void delete(int postId) {
        crudRepository.run(
                "DELETE FROM Post WHERE id = :fId",
                Map.of("fId", postId)
        );
    }

    /**
     * Список объявлений, отсортированных по дате создания (новые первые).
     *
     * @return список объявлений.
     */
    public List<Post> findAllOrderById() {
        return crudRepository.query("FROM Post ORDER BY id ASC", Post.class);
    }

    /**
     * Список активных объявлений, отсортированных по дате создания (новые первые).
     *
     * @return список активных объявлений.
     */
    public List<Post> findActivePostsOrderByCreatedAtDesc() {
        return crudRepository.query(
                "SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.car c LEFT JOIN FETCH c.model LEFT JOIN FETCH c.brand WHERE p.status = 'active' ORDER BY p.createdAt DESC",
                Post.class
        );
    }

    /**
     * Найти объявление по ID
     *
     * @param postId ID объявления
     * @return объявление.
     */
    public Optional<Post> findById(int postId) {
        return crudRepository.optional(
                """
                        SELECT DISTINCT p FROM Post p
                        LEFT JOIN FETCH p.car c
                        LEFT JOIN FETCH p.user
                        LEFT JOIN FETCH c.model
                        LEFT JOIN FETCH c.brand
                        LEFT JOIN FETCH c.brand
                        LEFT JOIN FETCH c.category
                        LEFT JOIN FETCH c.body
                        LEFT JOIN FETCH c.engine
                        LEFT JOIN FETCH c.transmissionType
                        LEFT JOIN FETCH c.driveType
                        LEFT JOIN FETCH c.carColor
                        LEFT JOIN FETCH c.fuelType
                        LEFT JOIN FETCH c.wheelSide
                        LEFT JOIN FETCH p.postPhotos
                        WHERE p.id = :fId
                        """,
                Post.class,
                Map.of("fId", postId)
        );
    }

    /**
     * Найти объявления по ID пользователя (владельца).
     *
     * @param userId ID пользователя.
     * @return список объявлений пользователя.
     */
    public List<Post> findByUserId(int userId) {
        return crudRepository.query(
                """
                        SELECT DISTINCT p FROM Post p
                        LEFT JOIN FETCH p.car c
                        LEFT JOIN FETCH c.model
                        LEFT JOIN FETCH c.brand
                        WHERE p.user.id = :userId
                        ORDER BY p.id ASC
                        """,
                Post.class,
                Map.of("userId", userId)
        );
    }
}