package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;
import ru.job4j.cars.testutil.TestDatabaseConfig;
import ru.job4j.cars.testutil.TestRepositoryUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для репозитория объявлений (PostRepository)
 */
class PostRepositoryTest {

    private SessionFactory sessionFactory;
    private CrudRepository crudRepository;
    private PostRepository postRepository;
    private TestRepositoryUtils testUtils;

    /**
     * Инициализация репозиториев перед каждым тестом.
     */
    @BeforeEach
    void setUp() throws Exception {
        this.sessionFactory = TestDatabaseConfig.setupTestEnvironment("db/liquibase_test.properties");
        this.crudRepository = new CrudRepository(sessionFactory);
        this.postRepository = new PostRepository(crudRepository);
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
     * Тестирует создание объявления.
     */
    @Test
    void whenCreatePostThenPostHasId() {
        User user = testUtils.createTestUser("testuser");
        Car car = testUtils.createTestCar("VIN001");
        Post post = testUtils.createTestPost(user, car, "active", new BigDecimal("1000000.00"));

        Post savedPost = postRepository.create(post);

        assertThat(savedPost.getId()).isGreaterThan(0);
        Optional<Post> foundPost = postRepository.findById(Math.toIntExact(savedPost.getId()));
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getStatus()).isEqualTo("active");
    }

    /**
     * Тестирует обновление объявления.
     */
    @Test
    void whenUpdatePostThenChangesSaved() {
        User user = testUtils.createTestUser("testuser");
        Car car = testUtils.createTestCar("VIN002");
        Post post = testUtils.createTestPost(user, car, "active", new BigDecimal("1000000.00"));
        Post savedPost = postRepository.create(post);

        savedPost.setStatus("sold");
        savedPost.setPrice(new BigDecimal("950000.00"));
        postRepository.update(savedPost);

        Optional<Post> updatedPost = postRepository.findById(Math.toIntExact(savedPost.getId()));
        assertThat(updatedPost).isPresent();
        assertThat(updatedPost.get().getStatus()).isEqualTo("sold");
        assertThat(updatedPost.get().getPrice()).isEqualTo(new BigDecimal("950000.00"));
    }

    /**
     * Тестирует удаление объявления.
     */
    @Test
    void whenDeletePostThenPostNotFound() {
        User user = testUtils.createTestUser("testuser");
        Car car = testUtils.createTestCar("VIN003");
        Post post = testUtils.createTestPost(user, car, "active", new BigDecimal("1000000.00"));
        Post savedPost = postRepository.create(post);

        postRepository.delete(Math.toIntExact(savedPost.getId()));

        Optional<Post> deletedPost = postRepository.findById(Math.toIntExact(savedPost.getId()));
        assertThat(deletedPost).isNotPresent();
    }

    /**
     * Тестирует получение всех объявлений с сортировкой.
     */
    @Test
    void whenFindAllOrderByIdThenReturnsSortedPosts() {
        User user = testUtils.createTestUser("testuser");
        Car car1 = testUtils.createTestCar("VIN004");
        Car car2 = testUtils.createTestCar("VIN005");
        Post post1 = testUtils.createTestPost(user, car1, "active", new BigDecimal("1000000.00"));
        Post post2 = testUtils.createTestPost(user, car2, "active", new BigDecimal("1500000.00"));

        postRepository.create(post1);
        postRepository.create(post2);

        List<Post> allPosts = postRepository.findAllOrderById();

        assertThat(allPosts).hasSizeGreaterThanOrEqualTo(2);
        assertThat(allPosts.get(0).getId()).isLessThan(allPosts.get(1).getId());
    }

    /**
     * Тестирует поиск активных объявлений.
     */
    @Test
    void whenFindActivePostsThenReturnsOnlyActive() {
        User user = testUtils.createTestUser("testuser");
        Car car1 = testUtils.createTestCar("VIN006");
        Car car2 = testUtils.createTestCar("VIN007");
        Car car3 = testUtils.createTestCar("VIN008");

        Post activePost1 = testUtils.createTestPost(user, car1, "active", new BigDecimal("1000000.00"));
        Post activePost2 = testUtils.createTestPost(user, car2, "active", new BigDecimal("1200000.00"));
        Post soldPost = testUtils.createTestPost(user, car3, "sold", new BigDecimal("800000.00"));

        postRepository.create(activePost1);
        postRepository.create(activePost2);
        postRepository.create(soldPost);

        List<Post> activePosts = postRepository.findActivePostsOrderByCreatedAtDesc();

        assertThat(activePosts).hasSize(2);
        assertThat(activePosts).allMatch(post -> "active".equals(post.getStatus()));
    }

    /**
     * Тестирует поиск объявления по ID с полной информацией.
     */
    @Test
    void whenFindByIdThenReturnsPostWithAllRelations() {
        User user = testUtils.createTestUser("testuser");
        Car car = testUtils.createTestCar("FULLVIN001");
        Post post = testUtils.createTestPost(user, car, "active", new BigDecimal("1000000.00"));
        Post savedPost = postRepository.create(post);

        Optional<Post> foundPost = postRepository.findById(Math.toIntExact(savedPost.getId()));

        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getUser()).isNotNull();
        assertThat(foundPost.get().getCar()).isNotNull();
        assertThat(foundPost.get().getCar().getBrand()).isNotNull();
        assertThat(foundPost.get().getCar().getModel()).isNotNull();
        assertThat(foundPost.get().getPostPhotos()).isNotNull();
    }

    /**
     * Тестирует поиск объявлений по ID пользователя.
     */
    @Test
    void whenFindByUserIdThenReturnsUserPosts() {
        User user1 = testUtils.createTestUser("user1");
        User user2 = testUtils.createTestUser("user2");
        Car car1 = testUtils.createTestCar("VIN009");
        Car car2 = testUtils.createTestCar("VIN010");
        Car car3 = testUtils.createTestCar("VIN011");

        Post user1Post1 = testUtils.createTestPost(user1, car1, "active", new BigDecimal("1000000.00"));
        Post user1Post2 = testUtils.createTestPost(user1, car2, "active", new BigDecimal("1200000.00"));
        Post user2Post = testUtils.createTestPost(user2, car3, "active", new BigDecimal("1500000.00"));

        postRepository.create(user1Post1);
        postRepository.create(user1Post2);
        postRepository.create(user2Post);

        List<Post> user1Posts = postRepository.findByUserId(Math.toIntExact(user1.getId()));

        assertThat(user1Posts).hasSize(2);
        assertThat(user1Posts).allMatch(post -> post.getUser().getId().equals(user1.getId()));
    }
}