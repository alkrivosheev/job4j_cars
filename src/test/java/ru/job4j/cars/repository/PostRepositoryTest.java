package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;
import ru.job4j.cars.testutil.TestDatabaseConfig;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PostRepositoryTest {

    private SessionFactory sessionFactory;
    private CrudRepository crudRepository;
    private PostRepository postRepository;

    @BeforeEach
    void setUp() throws Exception {
        this.sessionFactory = TestDatabaseConfig.setupTestEnvironment("db/liquibase_test.properties");
        this.crudRepository = new CrudRepository(sessionFactory);
        this.postRepository = new PostRepository(crudRepository);
    }

    @AfterEach
    void tearDown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }

    /**
     * Тестирует метод findPostsForLastDay.
     * Проверяет, что возвращаются только объявления, созданные за последний день.
     * Ожидаемый результат: список содержит только объявления, созданные за последние 24 часа.
     */
    @Test
    void whenFindPostsForLastDayThenReturnsOnlyRecentPosts() {
        Engine engine = new Engine();
        engine.setName("Test Engine");
        crudRepository.run(session -> session.persist(engine));

        Car car = new Car();
        car.setName("Test Car");
        car.setEngine(engine);
        crudRepository.run(session -> session.persist(car));

        User user = new User();
        user.setLogin("testuser");
        user.setPassword("password");
        crudRepository.run(session -> session.persist(user));

        Post recentPost = new Post();
        recentPost.setDescription("Recent Post");
        recentPost.setCreated(LocalDateTime.now().minusHours(12));
        recentPost.setCar(car);
        recentPost.setUser(user);

        Post oldPost = new Post();
        oldPost.setDescription("Old Post");
        oldPost.setCreated(LocalDateTime.now().minusDays(2));
        oldPost.setCar(car);
        oldPost.setUser(user);

        crudRepository.run(session -> {
            session.persist(recentPost);
            session.persist(oldPost);
        });

        List<Post> recentPosts = postRepository.findPostsForLastDay();

        assertThat(recentPosts).hasSize(1);
        assertThat(recentPosts.get(0).getDescription()).isEqualTo("Recent Post");
    }

    /**
     * Тестирует метод findPostsWithPhoto.
     * Проверяет, что возвращаются только объявления с фотографиями.
     * Ожидаемый результат: список содержит только объявления, у которых есть фотографии.
     */
    @Test
    void whenFindPostsWithPhotoThenReturnsOnlyPostsWithPhotos() {
        Engine engine = new Engine();
        engine.setName("Test Engine");
        crudRepository.run(session -> session.persist(engine));

        Car car = new Car();
        car.setName("Test Car");
        car.setEngine(engine);
        crudRepository.run(session -> session.persist(car));

        User user = new User();
        user.setLogin("testuser");
        user.setPassword("password");
        crudRepository.run(session -> session.persist(user));

        Post postWithPhoto = new Post();
        postWithPhoto.setDescription("Post with Photo");
        postWithPhoto.setCreated(LocalDateTime.now());
        postWithPhoto.setCar(car);
        postWithPhoto.setUser(user);
        postWithPhoto.setPhotos(List.of("photo1.jpg", "photo2.jpg"));

        Post postWithoutPhoto = new Post();
        postWithoutPhoto.setDescription("Post without Photo");
        postWithoutPhoto.setCreated(LocalDateTime.now());
        postWithoutPhoto.setCar(car);
        postWithoutPhoto.setUser(user);

        crudRepository.run(session -> {
            session.persist(postWithPhoto);
            session.persist(postWithoutPhoto);
        });

        List<Post> postsWithPhotos = postRepository.findPostsWithPhoto();

        assertThat(postsWithPhotos).hasSize(1);
        assertThat(postsWithPhotos.get(0).getDescription()).isEqualTo("Post with Photo");
        assertThat(postsWithPhotos.get(0).getPhotos()).isNotEmpty();
    }

    /**
     * Тестирует метод findPostsByBrand.
     * Проверяет, что возвращаются только объявления с указанной маркой автомобиля.
     * Ожидаемый результат: список содержит только объявления, где марка автомобиля соответствует запросу.
     */
    @Test
    void whenFindPostsByBrandThenReturnsOnlyPostsWithMatchingBrand() {
        Engine engine = new Engine();
        engine.setName("Test Engine");
        crudRepository.run(session -> session.persist(engine));
        Car toyotaCar = new Car();
        toyotaCar.setName("Toyota Camry");
        toyotaCar.setEngine(engine);
        Car hondaCar = new Car();
        hondaCar.setName("Honda Civic");
        hondaCar.setEngine(engine);
        crudRepository.run(session -> {
            session.persist(toyotaCar);
            session.persist(hondaCar);
        });
        User user = new User();
        user.setLogin("testuser");
        user.setPassword("password");
        crudRepository.run(session -> session.persist(user));
        Post toyotaPost = new Post();
        toyotaPost.setDescription("Toyota Post");
        toyotaPost.setCreated(LocalDateTime.now());
        toyotaPost.setCar(toyotaCar);
        toyotaPost.setUser(user);
        Post hondaPost = new Post();
        hondaPost.setDescription("Honda Post");
        hondaPost.setCreated(LocalDateTime.now());
        hondaPost.setCar(hondaCar);
        hondaPost.setUser(user);
        crudRepository.run(session -> {
            session.persist(toyotaPost);
            session.persist(hondaPost);
        });
        List<Post> toyotaPosts = postRepository.findPostsByBrand("Toyota");
        assertThat(toyotaPosts).hasSize(1);
        assertThat(toyotaPosts.get(0).getDescription()).isEqualTo("Toyota Post");
        assertThat(toyotaPosts.get(0).getCar().getName()).contains("Toyota");
    }

    /**
     * Тестирует метод findPostsByBrand с частичным совпадением.
     * Проверяет, что поиск работает по частичному совпадению марки автомобиля.
     * Ожидаемый результат: список содержит объявления, где марка автомобиля содержит указанную подстроку.
     */
    @Test
    void whenFindPostsByPartialBrandThenReturnsPostsWithMatchingBrand() {
        Engine engine = new Engine();
        engine.setName("Test Engine");
        crudRepository.run(session -> session.persist(engine));

        Car bmwCar = new Car();
        bmwCar.setName("BMW X5");
        bmwCar.setEngine(engine);
        Car bmwCar2 = new Car();
        bmwCar2.setName("BMW M3");
        bmwCar2.setEngine(engine);
        crudRepository.run(session -> {
            session.persist(bmwCar);
            session.persist(bmwCar2);
        });

        User user = new User();
        user.setLogin("testuser");
        user.setPassword("password");
        crudRepository.run(session -> session.persist(user));
        Post bmwPost1 = new Post();
        bmwPost1.setDescription("BMW X5 Post");
        bmwPost1.setCreated(LocalDateTime.now());
        bmwPost1.setCar(bmwCar);
        bmwPost1.setUser(user);
        Post bmwPost2 = new Post();
        bmwPost2.setDescription("BMW M3 Post");
        bmwPost2.setCreated(LocalDateTime.now());
        bmwPost2.setCar(bmwCar2);
        bmwPost2.setUser(user);

        crudRepository.run(session -> {
            session.persist(bmwPost1);
            session.persist(bmwPost2);
        });
        List<Post> bmwPosts = postRepository.findPostsByBrand("BMW");
        assertThat(bmwPosts).hasSize(2);
        assertThat(bmwPosts).extracting(post -> post.getCar().getName())
                .allMatch(name -> name.contains("BMW"));
    }

    /**
     * Тестирует метод findPostsForLastDay когда нет объявлений за последний день.
     * Проверяет, что возвращается пустой список, если нет подходящих объявлений.
     * Ожидаемый результат: пустой список объявлений.
     */
    @Test
    void whenFindPostsForLastDayAndNoRecentPostsThenReturnsEmptyList() {
        Engine engine = new Engine();
        engine.setName("Test Engine");
        crudRepository.run(session -> session.persist(engine));

        Car car = new Car();
        car.setName("Test Car");
        car.setEngine(engine);
        crudRepository.run(session -> session.persist(car));

        User user = new User();
        user.setLogin("testuser");
        user.setPassword("password");
        crudRepository.run(session -> session.persist(user));

        Post oldPost = new Post();
        oldPost.setDescription("Old Post");
        oldPost.setCreated(LocalDateTime.now().minusDays(2));
        oldPost.setCar(car);
        oldPost.setUser(user);

        crudRepository.run(session -> session.persist(oldPost));

        List<Post> recentPosts = postRepository.findPostsForLastDay();

        assertThat(recentPosts).isEmpty();
    }

    /**
     * Тестирует метод findPostsWithPhoto когда нет объявлений с фотографиями.
     * Проверяет, что возвращается пустой список, если нет объявлений с фото.
     * Ожидаемый результат: пустой список объявлений.
     */
    @Test
    void whenFindPostsWithPhotoAndNoPostsWithPhotosThenReturnsEmptyList() {
        Engine engine = new Engine();
        engine.setName("Test Engine");
        crudRepository.run(session -> session.persist(engine));

        Car car = new Car();
        car.setName("Test Car");
        car.setEngine(engine);
        crudRepository.run(session -> session.persist(car));

        User user = new User();
        user.setLogin("testuser");
        user.setPassword("password");
        crudRepository.run(session -> session.persist(user));

        Post postWithoutPhoto = new Post();
        postWithoutPhoto.setDescription("Post without Photo");
        postWithoutPhoto.setCreated(LocalDateTime.now());
        postWithoutPhoto.setCar(car);
        postWithoutPhoto.setUser(user);

        crudRepository.run(session -> session.persist(postWithoutPhoto));

        List<Post> postsWithPhotos = postRepository.findPostsWithPhoto();

        assertThat(postsWithPhotos).isEmpty();
    }
}