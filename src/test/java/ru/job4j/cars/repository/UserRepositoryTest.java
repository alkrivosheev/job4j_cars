package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.User;
import ru.job4j.cars.testutil.TestDatabaseConfig;
import ru.job4j.cars.testutil.TestRepositoryUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для репозитория пользователей (UserRepository)
 */
class UserRepositoryTest {

    private SessionFactory sessionFactory;
    private CrudRepository crudRepository;
    private UserRepository userRepository;
    private TestRepositoryUtils testUtils;

    /**
     * Инициализация репозиториев перед каждым тестом.
     */
    @BeforeEach
    void setUp() throws Exception {
        this.sessionFactory = TestDatabaseConfig.setupTestEnvironment("db/liquibase_test.properties");
        this.crudRepository = new CrudRepository(sessionFactory);
        this.userRepository = new UserRepository(crudRepository);
        this.testUtils = new TestRepositoryUtils(sessionFactory, crudRepository);
    }

    /**
     * Закрытие SessionFactory после каждого теста.
     */
    @AfterEach
    void tearDown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }

    /**
     * Тестирует создание пользователя.
     */
    @Test
    void whenCreateUserThenUserHasId() {
        User user = testUtils.createTestUser("Test User", "testuser", "password123");

        assertThat(user.getId()).isGreaterThan(0);
        Optional<User> foundUser = userRepository.findById(Math.toIntExact(user.getId()));
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getLogin()).isEqualTo(user.getLogin());
    }

    /**
     * Тестирует обновление пользователя.
     */
    @Test
    void whenUpdateUserThenChangesSaved() {
        User user = testUtils.createTestUser("Old Name", "updateuser", "oldpassword");

        user.setName("New Name");
        user.setPassword("newpassword");
        userRepository.update(user);

        Optional<User> updatedUser = userRepository.findById(Math.toIntExact(user.getId()));
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getName()).isEqualTo("New Name");
        assertThat(updatedUser.get().getPassword()).isEqualTo("newpassword");
    }

    /**
     * Тестирует удаление пользователя.
     */
    @Test
    void whenDeleteUserThenUserNotFound() {
        User user = testUtils.createTestUser("Delete User", "deleteuser", "password");

        userRepository.delete(Math.toIntExact(user.getId()));

        Optional<User> deletedUser = userRepository.findById(Math.toIntExact(user.getId()));
        assertThat(deletedUser).isNotPresent();
    }

    /**
     * Тестирует получение всех пользователей с сортировкой.
     */
    @Test
    void whenFindAllOrderByIdThenReturnsSortedUsers() {
        User user1 = testUtils.createTestUser("User One", "user1", "pass1");
        User user2 = testUtils.createTestUser("User Two", "user2", "pass2");

        List<User> allUsers = userRepository.findAllOrderById();

        assertThat(allUsers).hasSizeGreaterThanOrEqualTo(2);
        assertThat(allUsers.get(0).getId()).isLessThan(allUsers.get(1).getId());
    }

    /**
     * Тестирует поиск пользователя по ID.
     */
    @Test
    void whenFindByIdThenReturnsUser() {
        User user = testUtils.createTestUser("Specific User", "specificuser", "password");

        Optional<User> foundUser = userRepository.findById(Math.toIntExact(user.getId()));

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo(user.getName());
        assertThat(foundUser.get().getLogin()).isEqualTo(user.getLogin());
    }

    /**
     * Тестирует поиск пользователя по логину.
     */
    @Test
    void whenFindByLoginThenReturnsUser() {
        User user = testUtils.createTestUser("Login User", "loginuser", "password");

        Optional<User> foundUser = userRepository.findByLogin(user.getLogin());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo(user.getName());
        assertThat(foundUser.get().getLogin()).isEqualTo(user.getLogin());
    }
}