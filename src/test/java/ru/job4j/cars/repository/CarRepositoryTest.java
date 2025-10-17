package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.model.User;
import ru.job4j.cars.testutil.TestDatabaseConfig;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CarRepositoryTest {

    private SessionFactory sessionFactory;
    private CrudRepository crudRepository;
    private CarRepository carRepository;

    @BeforeEach
    void setUp() throws Exception {
        this.sessionFactory = TestDatabaseConfig.setupTestEnvironment("db/liquibase_test.properties");
        this.crudRepository = new CrudRepository(sessionFactory);
        this.carRepository = new CarRepository(crudRepository);
    }

    @AfterEach
    void tearDown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }

    /**
     * Тестирует метод create.
     * Проверяет, что при добавлении нового автомобиля он корректно сохраняется в хранилище
     * и может быть найден по его ID.
     * Ожидаемый результат: автомобиль, найденный по ID после добавления, имеет то же имя,
     * что и исходный добавленный автомобиль, и у него появляется ID.
     */
    @Test
    void whenCreateNewCarThenRepositoryHasSameCar() {
        Engine engine = new Engine();
        engine.setName("Test Engine");
        crudRepository.run(session -> session.persist(engine));

        Car car = new Car();
        car.setName("Test Car Name");
        car.setEngine(engine);

        Car savedCar = carRepository.create(car);

        assertThat(savedCar.getId()).isGreaterThan(0);
        assertThat(savedCar.getName()).isEqualTo(car.getName());

        var foundCar = carRepository.findById(Math.toIntExact(savedCar.getId()));
        assertThat(foundCar).isPresent();
        assertThat(foundCar.get().getName()).isEqualTo(car.getName());
    }

    /**
     * Тестирует метод update.
     * Проверяет, что при обновлении данных автомобиля изменения корректно сохраняются в хранилище.
     * Ожидаемый результат: после обновления автомобиль имеет новые данные.
     */
    @Test
    void whenUpdateCarThenRepositoryHasUpdatedCar() {
        Engine engine = new Engine();
        engine.setName("Test Engine");
        crudRepository.run(session -> session.persist(engine));

        Car car = new Car();
        car.setName("Original Name");
        car.setEngine(engine);
        Car savedCar = carRepository.create(car);

        savedCar.setName("Updated Name");
        carRepository.update(savedCar);

        Optional<Car> updatedCar = carRepository.findById(Math.toIntExact(savedCar.getId()));
        assertThat(updatedCar).isPresent();
        assertThat(updatedCar.get().getName()).isEqualTo("Updated Name");
    }

    /**
     * Тестирует метод delete.
     * Проверяет, что при удалении автомобиля он больше не находится в хранилище.
     * Ожидаемый результат: после удаления автомобиль отсутствует в хранилище.
     */
    @Test
    void whenDeleteCarThenRepositoryDoesNotHaveCar() {
        Engine engine = new Engine();
        engine.setName("Test Engine");
        crudRepository.run(session -> session.persist(engine));

        Car car = new Car();
        car.setName("Car to Delete");
        car.setEngine(engine);
        Car savedCar = carRepository.create(car);

        carRepository.delete(Math.toIntExact(savedCar.getId()));

        Optional<Car> deletedCar = carRepository.findById(Math.toIntExact(savedCar.getId()));
        assertThat(deletedCar).isNotPresent();
    }

    /**
     * Тестирует метод findAllOrderById.
     * Проверяет, что метод возвращает все автомобили, отсортированные по ID в возрастающем порядке.
     * Ожидаемый результат: список содержит все добавленные автомобили в правильном порядке.
     */
    @Test
    void whenFindAllOrderByIdThenReturnsAllCarsSortedById() {
        Engine engine1 = new Engine();
        engine1.setName("Engine 1");
        Engine engine2 = new Engine();
        engine2.setName("Engine 2");
        crudRepository.run(session -> {
            session.persist(engine1);
            session.persist(engine2);
        });

        Car car1 = new Car();
        car1.setName("First Car");
        car1.setEngine(engine1);
        Car car2 = new Car();
        car2.setName("Second Car");
        car2.setEngine(engine2);

        Car savedCar1 = carRepository.create(car1);
        Car savedCar2 = carRepository.create(car2);

        List<Car> allCars = carRepository.findAllOrderById();

        assertThat(allCars).hasSize(2);
        assertThat(allCars.get(0).getId()).isLessThan(allCars.get(1).getId());
        assertThat(allCars).extracting(Car::getName)
                .containsExactly("First Car", "Second Car");
    }

    /**
     * Тестирует метод findById.
     * Проверяет, что метод возвращает Optional.empty() когда автомобиль с указанным ID не существует.
     * Ожидаемый результат: для несуществующего ID возвращается пустой Optional.
     */
    @Test
    void whenFindByNonExistentIdThenReturnsEmptyOptional() {
        Optional<Car> foundCar = carRepository.findById(-1);

        assertThat(foundCar).isNotPresent();
    }

    /**
     * Тестирует создание автомобиля с двигателем.
     * Проверяет, что связь с двигателем корректно сохраняется и загружается.
     * Ожидаемый результат: автомобиль сохраняется с связанным двигателем.
     */
    @Test
    void whenCreateCarWithEngineThenRepositoryHasCarWithEngine() {
        Engine engine = new Engine();
        engine.setName("V8 Engine");
        crudRepository.run(session -> session.persist(engine));

        Car car = new Car();
        car.setName("Car with Engine");
        car.setEngine(engine);

        Car savedCar = carRepository.create(car);

        Optional<Car> foundCar = carRepository.findById(Math.toIntExact(savedCar.getId()));
        assertThat(foundCar).isPresent();
        assertThat(foundCar.get().getEngine()).isNotNull();
        assertThat(foundCar.get().getEngine().getName()).isEqualTo("V8 Engine");
    }

    /**
     * Тестирует обновление автомобиля с изменением двигателя.
     * Проверяет, что при обновлении можно изменить связанный двигатель.
     * Ожидаемый результат: после обновления автомобиль имеет новый двигатель.
     */

    @Test
    void whenUpdateCarWithNewEngineThenRepositoryHasUpdatedEngine() {
        Engine engine1 = new Engine();
        engine1.setName("Old Engine");
        Engine engine2 = new Engine();
        engine2.setName("New Engine");

        crudRepository.run(session -> session.persist(engine1));
        crudRepository.run(session -> session.persist(engine2));

        Car car = new Car();
        car.setName("Car with Engine");
        car.setEngine(engine1);
        Car savedCar = carRepository.create(car);

        savedCar.setEngine(engine2);
        carRepository.update(savedCar);

        Optional<Car> updatedCar = carRepository.findById(Math.toIntExact(savedCar.getId()));
        assertThat(updatedCar).isPresent();
        assertThat(updatedCar.get().getEngine().getName()).isEqualTo("New Engine");
    }

    /**
     * Тестирует создание автомобиля с владельцами.
     * Проверяет, что связь ManyToMany с владельцами корректно сохраняется и загружается.
     * Ожидаемый результат: автомобиль сохраняется с связанными владельцами.
     */
    @Test
    void whenCreateCarWithOwnersThenRepositoryHasCarWithOwners() {
        Engine engine = new Engine();
        engine.setName("Test Engine");
        crudRepository.run(session -> session.persist(engine));

        User user1 = new User();
        user1.setLogin("user1");
        user1.setPassword("password");
        crudRepository.run(session -> session.persist(user1));

        User user2 = new User();
        user2.setLogin("user2");
        user2.setPassword("password");
        crudRepository.run(session -> session.persist(user2));

        Owner owner1 = new Owner();
        owner1.setName("John Doe");
        owner1.setUser(user1);

        Owner owner2 = new Owner();
        owner2.setName("Jane Smith");
        owner2.setUser(user2);

        Car car = new Car();
        car.setName("Car with Owners");
        car.setEngine(engine);
        car.setOwners(Set.of(owner1, owner2));

        Car savedCar = carRepository.create(car);

        Optional<Car> foundCar = carRepository.findById(Math.toIntExact(savedCar.getId()));
        assertThat(foundCar).isPresent();
        assertThat(foundCar.get().getOwners()).hasSize(2);
        assertThat(foundCar.get().getOwners()).extracting(Owner::getName)
                .containsExactlyInAnyOrder("John Doe", "Jane Smith");
    }

    /**
     * Тестирует добавление владельцев к существующему автомобилю.
     * Проверяет, что связь ManyToMany корректно обновляется.
     * Ожидаемый результат: после обновления автомобиль имеет новых владельцев.
     */
    @Test
    void whenUpdateCarWithNewOwnersThenRepositoryHasUpdatedOwners() {
        Engine engine = new Engine();
        engine.setName("Test Engine");
        crudRepository.run(session -> session.persist(engine));

        User user1 = new User();
        user1.setLogin("user1");
        user1.setPassword("password");
        crudRepository.run(session -> session.persist(user1));

        User user2 = new User();
        user2.setLogin("user2");
        user2.setPassword("password");
        crudRepository.run(session -> session.persist(user2));

        Car car = new Car();
        car.setName("Car for Owners Update");
        car.setEngine(engine);
        Car savedCar = carRepository.create(car);

        Owner owner1 = new Owner();
        owner1.setName("New Owner 1");
        owner1.setUser(user1);

        Owner owner2 = new Owner();
        owner2.setName("New Owner 2");
        owner2.setUser(user2);

        savedCar.setOwners(Set.of(owner1, owner2));
        carRepository.update(savedCar);

        Optional<Car> updatedCar = carRepository.findById(Math.toIntExact(savedCar.getId()));
        assertThat(updatedCar).isPresent();
        assertThat(updatedCar.get().getOwners()).hasSize(2);
        assertThat(updatedCar.get().getOwners()).extracting(Owner::getName)
                .containsExactlyInAnyOrder("New Owner 1", "New Owner 2");
    }
}