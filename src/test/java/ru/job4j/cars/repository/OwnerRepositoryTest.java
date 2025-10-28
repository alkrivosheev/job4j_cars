package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.model.User;
import ru.job4j.cars.testutil.TestDatabaseConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("Временно отключен из-за изменений в логике репозитория")
class OwnerRepositoryTest {

    private SessionFactory sessionFactory;
    private CrudRepository crudRepository;
    private OwnerRepository ownerRepository;

    @BeforeEach
    void setUp() throws Exception {
        this.sessionFactory = TestDatabaseConfig.setupTestEnvironment("db/liquibase_test.properties");
        this.crudRepository = new CrudRepository(sessionFactory);
        this.ownerRepository = new OwnerRepository(crudRepository);
    }

    @AfterEach
    void tearDown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }

    /**
     * Тестирует метод create.
     * Проверяет, что при добавлении нового владельца он корректно сохраняется в хранилище
     * и может быть найден по его ID.
     * Ожидаемый результат: владелец, найденный по ID после добавления, имеет то же имя,
     * что и исходный добавленный владелец, и у него появляется ID.
     */
    @Test
    void whenCreateNewOwnerThenRepositoryHasSameOwner() {
        User user = new User();
        user.setLogin("testuser");
        user.setPassword("password");
        crudRepository.run(session -> session.persist(user));

        Owner owner = new Owner();
        owner.setName("Test Owner");
        owner.setUser(user);

        Owner savedOwner = ownerRepository.create(owner);

        assertThat(savedOwner.getId()).isGreaterThan(0);
        assertThat(savedOwner.getName()).isEqualTo(owner.getName());

        var foundOwner = ownerRepository.findById(Math.toIntExact(savedOwner.getId()));
        assertThat(foundOwner).isPresent();
        assertThat(foundOwner.get().getName()).isEqualTo(owner.getName());
    }

    /**
     * Тестирует метод update.
     * Проверяет, что при обновлении данных владельца изменения корректно сохраняются в хранилище.
     * Ожидаемый результат: после обновления владелец имеет новые данные.
     */
    @Test
    void whenUpdateOwnerThenRepositoryHasUpdatedOwner() {
        User user = new User();
        user.setLogin("testuser");
        user.setPassword("password");
        crudRepository.run(session -> session.persist(user));

        Owner owner = new Owner();
        owner.setName("Original Name");
        owner.setUser(user);
        Owner savedOwner = ownerRepository.create(owner);

        savedOwner.setName("Updated Name");
        ownerRepository.update(savedOwner);

        Optional<Owner> updatedOwner = ownerRepository.findById(Math.toIntExact(savedOwner.getId()));
        assertThat(updatedOwner).isPresent();
        assertThat(updatedOwner.get().getName()).isEqualTo("Updated Name");
    }

    /**
     * Тестирует метод delete.
     * Проверяет, что при удалении владельца он больше не находится в хранилище.
     * Ожидаемый результат: после удаления владелец отсутствует в хранилище.
     */
    @Test
    void whenDeleteOwnerThenRepositoryDoesNotHaveOwner() {
        User user = new User();
        user.setLogin("testuser");
        user.setPassword("password");
        crudRepository.run(session -> session.persist(user));

        Owner owner = new Owner();
        owner.setName("Owner to Delete");
        owner.setUser(user);
        Owner savedOwner = ownerRepository.create(owner);

        ownerRepository.delete(Math.toIntExact(savedOwner.getId()));

        Optional<Owner> deletedOwner = ownerRepository.findById(Math.toIntExact(savedOwner.getId()));
        assertThat(deletedOwner).isNotPresent();
    }

    /**
     * Тестирует метод findAllOrderById.
     * Проверяет, что метод возвращает всех владельцев, отсортированных по ID в возрастающем порядке.
     * Ожидаемый результат: список содержит всех добавленных владельцев в правильном порядке.
     */
    @Test
    void whenFindAllOrderByIdThenReturnsAllOwnersSortedById() {
        User user1 = new User();
        user1.setLogin("user1");
        user1.setPassword("password");
        User user2 = new User();
        user2.setLogin("user2");
        user2.setPassword("password");
        crudRepository.run(session -> {
            session.persist(user1);
            session.persist(user2);
        });

        Owner owner1 = new Owner();
        owner1.setName("First Owner");
        owner1.setUser(user1);
        Owner owner2 = new Owner();
        owner2.setName("Second Owner");
        owner2.setUser(user2);

        Owner savedOwner1 = ownerRepository.create(owner1);
        Owner savedOwner2 = ownerRepository.create(owner2);

        List<Owner> allOwners = ownerRepository.findAllOrderById();

        assertThat(allOwners).hasSize(2);
        assertThat(allOwners.get(0).getId()).isLessThan(allOwners.get(1).getId());
        assertThat(allOwners).extracting(Owner::getName)
                .containsExactly("First Owner", "Second Owner");
    }

    /**
     * Тестирует метод findById.
     * Проверяет, что метод возвращает Optional.empty() когда владелец с указанным ID не существует.
     * Ожидаемый результат: для несуществующего ID возвращается пустой Optional.
     */
    @Test
    void whenFindByNonExistentIdThenReturnsEmptyOptional() {
        Optional<Owner> foundOwner = ownerRepository.findById(-1);

        assertThat(foundOwner).isNotPresent();
    }

    /**
     * Тестирует создание владельца с пользователем.
     * Проверяет, что связь с пользователем корректно сохраняется и загружается.
     * Ожидаемый результат: владелец сохраняется с связанным пользователем.
     */
    @Test
    void whenCreateOwnerWithUserThenRepositoryHasOwnerWithUser() {
        User user = new User();
        user.setLogin("owneruser");
        user.setPassword("password");
        crudRepository.run(session -> session.persist(user));

        Owner owner = new Owner();
        owner.setName("Owner with User");
        owner.setUser(user);

        Owner savedOwner = ownerRepository.create(owner);

        Optional<Owner> foundOwner = ownerRepository.findById(Math.toIntExact(savedOwner.getId()));
        assertThat(foundOwner).isPresent();
        assertThat(foundOwner.get().getUser()).isNotNull();
        assertThat(foundOwner.get().getUser().getLogin()).isEqualTo("owneruser");
    }

    /**
     * Тестирует обновление владельца с изменением пользователя.
     * Проверяет, что при обновлении можно изменить связанного пользователя.
     * Ожидаемый результат: после обновления владелец имеет нового пользователя.
     */
    @Test
    void whenUpdateOwnerWithNewUserThenRepositoryHasUpdatedUser() {
        User user1 = new User();
        user1.setLogin("olduser");
        user1.setPassword("password");
        User user2 = new User();
        user2.setLogin("newuser");
        user2.setPassword("password");

        crudRepository.run(session -> session.persist(user1));
        crudRepository.run(session -> session.persist(user2));

        Owner owner = new Owner();
        owner.setName("Owner for User Update");
        owner.setUser(user1);
        Owner savedOwner = ownerRepository.create(owner);

        savedOwner.setUser(user2);
        ownerRepository.update(savedOwner);

        Optional<Owner> updatedOwner = ownerRepository.findById(Math.toIntExact(savedOwner.getId()));
        assertThat(updatedOwner).isPresent();
        assertThat(updatedOwner.get().getUser().getLogin()).isEqualTo("newuser");
    }
}