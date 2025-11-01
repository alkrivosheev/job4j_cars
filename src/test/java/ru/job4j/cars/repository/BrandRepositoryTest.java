package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Brand;
import ru.job4j.cars.testutil.TestDatabaseConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для репозитория марок автомобилей (BrandRepository)
 */
class BrandRepositoryTest {

    private SessionFactory sessionFactory;
    private CrudRepository crudRepository;
    private BrandRepository brandRepository;

    /**
     * Инициализация репозиториев перед каждым тестом.
     */
    @BeforeEach
    void setUp() throws Exception {
        this.sessionFactory = TestDatabaseConfig.setupTestEnvironment("db/liquibase_test.properties");
        this.crudRepository = new CrudRepository(sessionFactory);
        this.brandRepository = new BrandRepository(crudRepository);
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
     * Создает тестовую марку автомобиля.
     */
    private Brand createTestBrand(String name) {
        Brand brand = new Brand();
        brand.setName(name + "_" + System.currentTimeMillis());
        return brand;
    }

    /**
     * Тестирует создание новой марки автомобиля в репозитории.
     * Проверяет, что после сохранения марка имеет присвоенный идентификатор,
     * имя сохраняется корректно и марка может быть найдена по идентификатору.
     */
    @Test
    void whenCreateNewBrandThenRepositoryHasSameBrand() {
        Brand brand = createTestBrand("Toyota");
        Brand savedBrand = brandRepository.create(brand);

        assertThat(savedBrand.getId()).isGreaterThan(0);
        assertThat(savedBrand.getName()).isEqualTo(brand.getName());

        Optional<Brand> foundBrand = brandRepository.findById(Math.toIntExact(savedBrand.getId()));
        assertThat(foundBrand).isPresent();
        assertThat(foundBrand.get().getName()).isEqualTo(brand.getName());
    }

    /**
     * Тестирует обновление данных марки автомобиля в репозитории.
     * Проверяет, что изменение имени корректно сохраняется
     * и может быть получено при последующем поиске.
     */
    @Test
    void whenUpdateBrandThenRepositoryHasUpdatedBrand() {
        Brand brand = createTestBrand("BMW");
        Brand savedBrand = brandRepository.create(brand);

        savedBrand.setName("Mercedes-Benz");
        brandRepository.update(savedBrand);

        Optional<Brand> updatedBrand = brandRepository.findById(Math.toIntExact(savedBrand.getId()));
        assertThat(updatedBrand).isPresent();
        assertThat(updatedBrand.get().getName()).isEqualTo("Mercedes-Benz");
    }

    /**
     * Тестирует удаление марки автомобиля из репозитория.
     * Проверяет, что после удаления марка больше не находится в хранилище
     * при поиске по идентификатору.
     */
    @Test
    void whenDeleteBrandThenRepositoryDoesNotHaveBrand() {
        Brand brand = createTestBrand("Audi");
        Brand savedBrand = brandRepository.create(brand);

        brandRepository.delete(Math.toIntExact(savedBrand.getId()));

        Optional<Brand> deletedBrand = brandRepository.findById(Math.toIntExact(savedBrand.getId()));
        assertThat(deletedBrand).isNotPresent();
    }

    /**
     * Тестирует получение всех марок автомобилей с сортировкой по идентификатору.
     * Проверяет, что возвращаются все сохраненные марки в правильном порядке,
     * отсортированные по возрастанию идентификатора.
     */
    @Test
    void whenFindAllOrderByIdThenReturnsAllBrandsSortedById() {
        Brand brand1 = createTestBrand("Volkswagen");
        Brand brand2 = createTestBrand("Ford");
        Brand savedBrand1 = brandRepository.create(brand1);
        Brand savedBrand2 = brandRepository.create(brand2);

        List<Brand> allBrands = brandRepository.findAllOrderById();

        assertThat(allBrands).hasSizeGreaterThanOrEqualTo(2);
        assertThat(allBrands.get(0).getId()).isLessThan(allBrands.get(1).getId());
    }

    /**
     * Тестирует поиск марки автомобиля по несуществующему идентификатору.
     * Проверяет, что для несуществующего идентификатора возвращается пустой Optional,
     * что соответствует ожидаемому поведению при отсутствии данных.
     */
    @Test
    void whenFindByNonExistentIdThenReturnsEmptyOptional() {
        Optional<Brand> foundBrand = brandRepository.findById(-1);
        assertThat(foundBrand).isNotPresent();
    }

    /**
     * Тестирует поиск марки автомобиля по нулевому идентификатору.
     * Проверяет граничное условие - возвращается пустой Optional для идентификатора 0,
     * так как в системе идентификаторы обычно начинаются с 1.
     */
    @Test
    void whenFindByIdWithZeroIdThenReturnsEmpty() {
        Optional<Brand> result = brandRepository.findById(0);
        assertThat(result).isNotPresent();
    }

    /**
     * Тестирует удаление несуществующей марки автомобиля.
     * Проверяет, что операция удаления для несуществующего идентификатора
     * завершается без генерации исключений, обеспечивая отказоустойчивость.
     */
    @Test
    void whenDeleteNonExistentBrandThenNoException() {
        boolean deletionResult = true;
        try {
            brandRepository.delete(-1);
        } catch (Exception e) {
            deletionResult = false;
        }
        assertThat(deletionResult).isTrue();
    }
}