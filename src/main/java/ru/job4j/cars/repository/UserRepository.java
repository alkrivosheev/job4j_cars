package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class UserRepository {

    private final CrudRepository crudRepository;

    /**
     * Сохранить в базе.
     *
     * @param user пользователь.
     * @return пользователь с id, либо null.
     */
    public Optional<User> create(User user) {
        try {
            User savedUser = crudRepository.tx(session -> {
                session.persist(user);
                return user;
            });
            return Optional.of(savedUser);
        } catch (Exception e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                return Optional.empty();
            }
            return Optional.empty();
        }
    }

    /**
     * Обновить в базе пользователя.
     *
     * @param user пользователь.
     */
    public void update(User user) {
        crudRepository.run(session -> session.merge(user));
    }

    /**
     * Удалить пользователя по id.
     *
     * @param userId ID
     */
    public void delete(int userId) {
        crudRepository.run(
                "DELETE FROM User WHERE id = :fId",
                Map.of("fId", userId)
        );
    }

    /**
     * Список пользователей, отсортированных по id.
     *
     * @return список пользователей.
     */
    public List<User> findAllOrderById() {
        return crudRepository.query("FROM User ORDER BY id ASC", User.class);
    }

    /**
     * Найти пользователя по ID
     *
     * @param userId ID пользователя
     * @return пользователь.
     */
    public Optional<User> findById(int userId) {
        return crudRepository.optional(
                "SELECT u FROM User u WHERE u.id = :fId",
                User.class,
                Map.of("fId", userId)
        );
    }

    /**
     * Найти пользователя по логину.
     *
     * @param login логин пользователя
     * @return пользователь.
     */
    public Optional<User> findByLogin(String login) {
        return crudRepository.optional(
                "SELECT u FROM User u WHERE u.login = :login",
                User.class,
                Map.of("login", login)
        );
    }

    /**
     * Найти пользователя по логину и паролю.
     *
     * @param login логин пользователя
     * @param password пароль пользователя
     * @return пользователь.
     */
    public Optional<User> findByLoginAndPassword(String login, String password) {
        return crudRepository.optional(
                "SELECT u FROM User u WHERE u.login = :login AND u.password = :password",
                User.class,
                Map.of(
                        "login", login,
                        "password", password
                )
        );
    }
}