package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.User;
import ru.job4j.cars.testutil.TestDatabaseConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest {

    private SessionFactory sessionFactory;
    private CrudRepository crudRepository;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception {
        this.sessionFactory = TestDatabaseConfig.setupTestEnvironment("db/liquibase_test.properties");
        this.crudRepository = new CrudRepository(sessionFactory);
        this.userRepository = new UserRepository(crudRepository);
    }

    @AfterEach
    void tearDown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }

    /**
     * Тестирует метод create.
     * Проверяет, что при добавлении нового пользователя он корректно сохраняется в хранилище
     * и может быть найден по его ID.
     * Ожидаемый результат: пользователь, найденный по ID после добавления, имеет тот же логин,
     * что и исходный добавленный пользователь, и у него появляется ID.
     */
    @Test
    void whenCreateNewUserThenRepositoryHasSameUser() {
        User user = new User();
        user.setLogin("testuser");
        user.setPassword("password");

        User savedUser = userRepository.create(user);

        assertThat(savedUser.getId()).isGreaterThan(0);
        assertThat(savedUser.getLogin()).isEqualTo(user.getLogin());

        var foundUser = userRepository.findById(Math.toIntExact(savedUser.getId()));
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getLogin()).isEqualTo(user.getLogin());
    }

    /**
     * Тестирует метод update.
     * Проверяет, что при обновлении данных пользователя изменения корректно сохраняются в хранилище.
     * Ожидаемый результат: после обновления пользователь имеет новые данные.
     */
    @Test
    void whenUpdateUserThenRepositoryHasUpdatedUser() {
        User user = new User();
        user.setLogin("original_login");
        user.setPassword("password");
        User savedUser = userRepository.create(user);

        savedUser.setLogin("updated_login");
        userRepository.update(savedUser);

        Optional<User> updatedUser = userRepository.findById(Math.toIntExact(savedUser.getId()));
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getLogin()).isEqualTo("updated_login");
    }

    /**
     * Тестирует метод delete.
     * Проверяет, что при удалении пользователя он больше не находится в хранилище.
     * Ожидаемый результат: после удаления пользователь отсутствует в хранилище.
     */
    @Test
    void whenDeleteUserThenRepositoryDoesNotHaveUser() {
        User user = new User();
        user.setLogin("user_to_delete");
        user.setPassword("password");
        User savedUser = userRepository.create(user);

        userRepository.delete(Math.toIntExact(savedUser.getId()));

        Optional<User> deletedUser = userRepository.findById(Math.toIntExact(savedUser.getId()));
        assertThat(deletedUser).isNotPresent();
    }

    /**
     * Тестирует метод findAllOrderById.
     * Проверяет, что метод возвращает всех пользователей, отсортированных по ID в возрастающем порядке.
     * Ожидаемый результат: список содержит всех добавленных пользователей в правильном порядке.
     */
    @Test
    void whenFindAllOrderByIdThenReturnsAllUsersSortedById() {
        List<User> initialUsers = userRepository.findAllOrderById();
        int initialCount = initialUsers.size();

        User user1 = new User();
        user1.setLogin("first_user");
        user1.setPassword("password");
        User user2 = new User();
        user2.setLogin("second_user");
        user2.setPassword("password");

        User savedUser1 = userRepository.create(user1);
        User savedUser2 = userRepository.create(user2);

        List<User> allUsers = userRepository.findAllOrderById();

        assertThat(allUsers).hasSize(initialCount + 2);

        List<User> newUsers = allUsers.subList(initialCount, allUsers.size());
        assertThat(newUsers.get(0).getId()).isLessThan(newUsers.get(1).getId());
        assertThat(newUsers).extracting(User::getLogin)
                .containsExactly("first_user", "second_user");

        for (int i = 0; i < allUsers.size() - 1; i++) {
            assertThat(allUsers.get(i).getId()).isLessThan(allUsers.get(i + 1).getId());
        }
    }

    /**
     * Тестирует метод findById.
     * Проверяет, что метод возвращает Optional.empty() когда пользователь с указанным ID не существует.
     * Ожидаемый результат: для несуществующего ID возвращается пустой Optional.
     */
    @Test
    void whenFindByNonExistentIdThenReturnsEmptyOptional() {
        Optional<User> foundUser = userRepository.findById(-1);

        assertThat(foundUser).isNotPresent();
    }

    /**
     * Тестирует метод findByLikeLogin.
     * Проверяет, что метод возвращает пользователей, у которых логин содержит указанную подстроку.
     * Ожидаемый результат: список содержит пользователей с логинами, содержащими указанную подстроку.
     */
    @Test
    void whenFindByLikeLoginThenReturnsUsersWithMatchingLogin() {
        User user1 = new User();
        user1.setLogin("john_doe");
        user1.setPassword("password");
        User user2 = new User();
        user2.setLogin("jane_smith");
        user2.setPassword("password");
        User user3 = new User();
        user3.setLogin("bob_johnson");
        user3.setPassword("password");

        userRepository.create(user1);
        userRepository.create(user2);
        userRepository.create(user3);

        List<User> usersWithJohn = userRepository.findByLikeLogin("john");

        assertThat(usersWithJohn).hasSize(2);
        assertThat(usersWithJohn).extracting(User::getLogin)
                .containsExactlyInAnyOrder("john_doe", "bob_johnson");
    }

    /**
     * Тестирует метод findByLikeLogin когда нет совпадений.
     * Проверяет, что возвращается пустой список, если нет пользователей с указанной подстрокой в логине.
     * Ожидаемый результат: пустой список пользователей.
     */
    @Test
    void whenFindByLikeLoginAndNoMatchesThenReturnsEmptyList() {
        User user = new User();
        user.setLogin("testuser");
        user.setPassword("password");
        userRepository.create(user);

        List<User> usersWithUnknown = userRepository.findByLikeLogin("unknown");

        assertThat(usersWithUnknown).isEmpty();
    }

    /**
     * Тестирует метод findByLogin.
     * Проверяет, что метод возвращает пользователя по точному совпадению логина.
     * Ожидаемый результат: возвращается пользователь с указанным логином.
     */
    @Test
    void whenFindByLoginThenReturnsUserWithExactLogin() {
        User user = new User();
        user.setLogin("exact_login");
        user.setPassword("password");
        userRepository.create(user);

        Optional<User> foundUser = userRepository.findByLogin("exact_login");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getLogin()).isEqualTo("exact_login");
    }

    /**
     * Тестирует метод findByLogin когда пользователь не существует.
     * Проверяет, что метод возвращает Optional.empty() когда пользователь с указанным логином не существует.
     * Ожидаемый результат: для несуществующего логина возвращается пустой Optional.
     */
    @Test
    void whenFindByNonExistentLoginThenReturnsEmptyOptional() {
        Optional<User> foundUser = userRepository.findByLogin("non_existent");

        assertThat(foundUser).isNotPresent();
    }

    /**
     * Тестирует метод findByLogin с учетом регистра.
     * Проверяет, что поиск по логину чувствителен к регистру.
     * Ожидаемый результат: пользователь с логином в другом регистре не находится.
     */
    @Test
    void whenFindByLoginWithDifferentCaseThenReturnsEmptyOptional() {
        User user = new User();
        user.setLogin("TestUser");
        user.setPassword("password");
        userRepository.create(user);

        Optional<User> foundUser = userRepository.findByLogin("testuser");

        assertThat(foundUser).isNotPresent();
    }

    /**
     * Тестирует обновление пароля пользователя.
     * Проверяет, что при обновлении можно изменить пароль пользователя.
     * Ожидаемый результат: после обновления пользователь имеет новый пароль.
     */
    @Test
    void whenUpdateUserPasswordThenRepositoryHasUpdatedPassword() {
        User user = new User();
        user.setLogin("user_for_password_update");
        user.setPassword("old_password");
        User savedUser = userRepository.create(user);

        savedUser.setPassword("new_password");
        userRepository.update(savedUser);

        Optional<User> updatedUser = userRepository.findById(Math.toIntExact(savedUser.getId()));
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getPassword()).isEqualTo("new_password");
    }
}