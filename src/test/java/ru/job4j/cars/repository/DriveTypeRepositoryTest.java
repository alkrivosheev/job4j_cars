package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.DriveType;
import ru.job4j.cars.testutil.TestDatabaseConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для репозитория типов привода автомобилей (DriveTypeRepository)
 */
class DriveTypeRepositoryTest {

    private SessionFactory sessionFactory;
    private CrudRepository crudRepository;
    private DriveTypeRepository driveTypeRepository;

    /**
     * Инициализация репозиториев перед каждым тестом.
     */
    @BeforeEach
    void setUp() throws Exception {
        this.sessionFactory = TestDatabaseConfig.setupTestEnvironment("db/liquibase_test.properties");
        this.crudRepository = new CrudRepository(sessionFactory);
        this.driveTypeRepository = new DriveTypeRepository(crudRepository);
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
     * Создает тестовый тип привода автомобиля.
     */
    private DriveType createTestDriveType(String name) {
        DriveType driveType = new DriveType();
        driveType.setName(name + "_" + System.currentTimeMillis());
        return driveType;
    }

    /**
     * Тестирует создание нового типа привода автомобиля в репозитории.
     * Проверяет, что после сохранения тип привода имеет присвоенный идентификатор,
     * имя сохраняется корректно и тип привода может быть найден по идентификатору.
     */
    @Test
    void whenCreateNewDriveTypeThenRepositoryHasSameDriveType() {
        DriveType driveType = createTestDriveType("Передний");
        DriveType savedDriveType = driveTypeRepository.create(driveType);

        assertThat(savedDriveType.getId()).isGreaterThan(0);
        assertThat(savedDriveType.getName()).isEqualTo(driveType.getName());

        Optional<DriveType> foundDriveType = driveTypeRepository.findById(Math.toIntExact(savedDriveType.getId()));
        assertThat(foundDriveType).isPresent();
        assertThat(foundDriveType.get().getName()).isEqualTo(driveType.getName());
    }

    /**
     * Тестирует обновление данных типа привода автомобиля в репозитории.
     * Проверяет, что изменение имени корректно сохраняется
     * и может быть получено при последующем поиске.
     */
    @Test
    void whenUpdateDriveTypeThenRepositoryHasUpdatedDriveType() {
        DriveType driveType = createTestDriveType("Задний");
        DriveType savedDriveType = driveTypeRepository.create(driveType);

        savedDriveType.setName("Полный");
        driveTypeRepository.update(savedDriveType);

        Optional<DriveType> updatedDriveType = driveTypeRepository.findById(Math.toIntExact(savedDriveType.getId()));
        assertThat(updatedDriveType).isPresent();
        assertThat(updatedDriveType.get().getName()).isEqualTo("Полный");
    }

    /**
     * Тестирует удаление типа привода автомобиля из репозитория.
     * Проверяет, что после удаления тип привода больше не находится в хранилище
     * при поиске по идентификатору.
     */
    @Test
    void whenDeleteDriveTypeThenRepositoryDoesNotHaveDriveType() {
        DriveType driveType = createTestDriveType("Подключаемый полный");
        DriveType savedDriveType = driveTypeRepository.create(driveType);

        driveTypeRepository.delete(Math.toIntExact(savedDriveType.getId()));

        Optional<DriveType> deletedDriveType = driveTypeRepository.findById(Math.toIntExact(savedDriveType.getId()));
        assertThat(deletedDriveType).isNotPresent();
    }

    /**
     * Тестирует получение всех типов привода автомобилей с сортировкой по идентификатору.
     * Проверяет, что возвращаются все сохраненные типы привода в правильном порядке,
     * отсортированные по возрастанию идентификатора.
     */
    @Test
    void whenFindAllOrderByIdThenReturnsAllDriveTypesSortedById() {
        DriveType driveType1 = createTestDriveType("Передний привод");
        DriveType driveType2 = createTestDriveType("Задний привод");
        DriveType savedDriveType1 = driveTypeRepository.create(driveType1);
        DriveType savedDriveType2 = driveTypeRepository.create(driveType2);

        List<DriveType> allDriveTypes = driveTypeRepository.findAllOrderById();

        assertThat(allDriveTypes).hasSizeGreaterThanOrEqualTo(2);
        assertThat(allDriveTypes.get(0).getId()).isLessThan(allDriveTypes.get(1).getId());
    }

    /**
     * Тестирует поиск типа привода автомобиля по несуществующему идентификатору.
     * Проверяет, что для несуществующего идентификатора возвращается пустой Optional,
     * что соответствует ожидаемому поведению при отсутствии данных.
     */
    @Test
    void whenFindByNonExistentIdThenReturnsEmptyOptional() {
        Optional<DriveType> foundDriveType = driveTypeRepository.findById(-1);
        assertThat(foundDriveType).isNotPresent();
    }

    /**
     * Тестирует поиск типа привода автомобиля по нулевому идентификатору.
     * Проверяет граничное условие - возвращается пустой Optional для идентификатора 0,
     * так как в системе идентификаторы обычно начинаются с 1.
     */
    @Test
    void whenFindByIdWithZeroIdThenReturnsEmpty() {
        Optional<DriveType> result = driveTypeRepository.findById(0);
        assertThat(result).isNotPresent();
    }

    /**
     * Тестирует удаление несуществующего типа привода автомобиля.
     * Проверяет, что операция удаления для несуществующего идентификатора
     * завершается без генерации исключений, обеспечивая отказоустойчивость.
     */
    @Test
    void whenDeleteNonExistentDriveTypeThenNoException() {
        boolean deletionResult = true;
        try {
            driveTypeRepository.delete(-1);
        } catch (Exception e) {
            deletionResult = false;
        }
        assertThat(deletionResult).isTrue();
    }
}