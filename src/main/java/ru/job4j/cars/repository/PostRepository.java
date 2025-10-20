package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class PostRepository {
    private final CrudRepository crudRepository;

    /**
     * Показать объявления за последний день.
     * @return список объявлений.
     */
    public List<Post> findPostsForLastDay() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        String query = """
        SELECT DISTINCT p FROM Post p 
        LEFT JOIN FETCH p.photos 
        LEFT JOIN FETCH p.car c 
        LEFT JOIN FETCH c.engine 
        LEFT JOIN FETCH p.user 
        WHERE p.created > :oneDayAgo 
        ORDER BY p.created DESC
        """;
        return crudRepository.query(query, Post.class, Map.of("oneDayAgo", oneDayAgo));
    }

    /**
     * Показать объявления с фото.
     * @return список объявлений с фото.
     */
    public List<Post> findPostsWithPhoto() {
        String query = """
        SELECT DISTINCT p FROM Post p 
        LEFT JOIN FETCH p.photos 
        LEFT JOIN FETCH p.car c 
        LEFT JOIN FETCH c.engine 
        LEFT JOIN FETCH p.user 
        WHERE SIZE(p.photos) > 0 
        ORDER BY p.created DESC
        """;
        return crudRepository.query(query, Post.class);
    }

    /**
     * Показать объявления определенной марки.
     * @param brand марка автомобиля.
     * @return список объявлений.
     */
    public List<Post> findPostsByBrand(String brand) {
        String query = """
        SELECT DISTINCT p FROM Post p 
        LEFT JOIN FETCH p.photos 
        LEFT JOIN FETCH p.car c 
        LEFT JOIN FETCH c.engine 
        LEFT JOIN FETCH p.user 
        WHERE c.name LIKE :brand 
        ORDER BY p.created DESC
        """;
        return crudRepository.query(query, Post.class, Map.of("brand", "%" + brand + "%"));
    }
}