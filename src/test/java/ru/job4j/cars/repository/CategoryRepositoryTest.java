package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Category;
import ru.job4j.cars.testutil.TestDatabaseConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для репозитория категорий автомобилей (CategoryRepository)
 */
class CategoryRepositoryTest {

    private SessionFactory sessionFactory;
    private CrudRepository crudRepository;
    private CategoryRepository categoryRepository;

    /**
     * Инициализация репозиториев перед каждым тестом.
     */
    @BeforeEach
    void setUp() throws Exception {
        this.sessionFactory = TestDatabaseConfig.setupTestEnvironment("db/liquibase_test.properties");
        this.crudRepository = new CrudRepository(sessionFactory);
        this.categoryRepository = new CategoryRepository(crudRepository);
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
     * Создает тестовую категорию автомобиля.
     */
    private Category createTestCategory(String name) {
        Category category = new Category();
        category.setName(name + "_" + System.currentTimeMillis());
        return category;
    }

    /**
     * Тестирует создание новой категории автомобиля в репозитории.
     * Проверяет, что после сохранения категория имеет присвоенный идентификатор,
     * имя сохраняется корректно и категория может быть найдена по идентификатору.
     */
    @Test
    void whenCreateNewCategoryThenRepositoryHasSameCategory() {
        Category category = createTestCategory("Легковой");
        Category savedCategory = categoryRepository.create(category);

        assertThat(savedCategory.getId()).isGreaterThan(0);
        assertThat(savedCategory.getName()).isEqualTo(category.getName());

        Optional<Category> foundCategory = categoryRepository.findById(Math.toIntExact(savedCategory.getId()));
        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getName()).isEqualTo(category.getName());
    }

    /**
     * Тестирует обновление данных категории автомобиля в репозитории.
     * Проверяет, что изменение имени корректно сохраняется
     * и может быть получено при последующем поиске.
     */
    @Test
    void whenUpdateCategoryThenRepositoryHasUpdatedCategory() {
        Category category = createTestCategory("Грузовой");
        Category savedCategory = categoryRepository.create(category);

        savedCategory.setName("Внедорожник");
        categoryRepository.update(savedCategory);

        Optional<Category> updatedCategory = categoryRepository.findById(Math.toIntExact(savedCategory.getId()));
        assertThat(updatedCategory).isPresent();
        assertThat(updatedCategory.get().getName()).isEqualTo("Внедорожник");
    }

    /**
     * Тестирует удаление категории автомобиля из репозитория.
     * Проверяет, что после удаления категория больше не находится в хранилище
     * при поиске по идентификатору.
     */
    @Test
    void whenDeleteCategoryThenRepositoryDoesNotHaveCategory() {
        Category category = createTestCategory("Минивэн");
        Category savedCategory = categoryRepository.create(category);

        categoryRepository.delete(Math.toIntExact(savedCategory.getId()));

        Optional<Category> deletedCategory = categoryRepository.findById(Math.toIntExact(savedCategory.getId()));
        assertThat(deletedCategory).isNotPresent();
    }

    /**
     * Тестирует получение всех категорий автомобилей с сортировкой по идентификатору.
     * Проверяет, что возвращаются все сохраненные категории в правильном порядке,
     * отсортированные по возрастанию идентификатора.
     */
    @Test
    void whenFindAllOrderByIdThenReturnsAllCategoriesSortedById() {
        Category category1 = createTestCategory("Седан");
        Category category2 = createTestCategory("Купе");
        Category savedCategory1 = categoryRepository.create(category1);
        Category savedCategory2 = categoryRepository.create(category2);

        List<Category> allCategories = categoryRepository.findAllOrderById();

        assertThat(allCategories).hasSizeGreaterThanOrEqualTo(2);
        assertThat(allCategories.get(0).getId()).isLessThan(allCategories.get(1).getId());
    }

    /**
     * Тестирует поиск категории автомобиля по несуществующему идентификатору.
     * Проверяет, что для несуществующего идентификатора возвращается пустой Optional,
     * что соответствует ожидаемому поведению при отсутствии данных.
     */
    @Test
    void whenFindByNonExistentIdThenReturnsEmptyOptional() {
        Optional<Category> foundCategory = categoryRepository.findById(-1);
        assertThat(foundCategory).isNotPresent();
    }

    /**
     * Тестирует поиск категории автомобиля по нулевому идентификатору.
     * Проверяет граничное условие - возвращается пустой Optional для идентификатора 0,
     * так как в системе идентификаторы обычно начинаются с 1.
     */
    @Test
    void whenFindByIdWithZeroIdThenReturnsEmpty() {
        Optional<Category> result = categoryRepository.findById(0);
        assertThat(result).isNotPresent();
    }

    /**
     * Тестирует удаление несуществующей категории автомобиля.
     * Проверяет, что операция удаления для несуществующего идентификатора
     * завершается без генерации исключений, обеспечивая отказоустойчивость.
     */
    @Test
    void whenDeleteNonExistentCategoryThenNoException() {
        boolean deletionResult = true;
        try {
            categoryRepository.delete(-1);
        } catch (Exception e) {
            deletionResult = false;
        }
        assertThat(deletionResult).isTrue();
    }
}