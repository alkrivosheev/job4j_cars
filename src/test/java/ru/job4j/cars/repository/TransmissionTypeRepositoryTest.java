package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.TransmissionType;
import ru.job4j.cars.testutil.TestDatabaseConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для репозитория типов трансмиссии автомобилей (TransmissionTypeRepository)
 */
class TransmissionTypeRepositoryTest {

    private SessionFactory sessionFactory;
    private CrudRepository crudRepository;
    private TransmissionTypeRepository transmissionTypeRepository;

    /**
     * Инициализация репозиториев перед каждым тестом.
     */
    @BeforeEach
    void setUp() throws Exception {
        this.sessionFactory = TestDatabaseConfig.setupTestEnvironment("db/liquibase_test.properties");
        this.crudRepository = new CrudRepository(sessionFactory);
        this.transmissionTypeRepository = new TransmissionTypeRepository(crudRepository);
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
     * Создает тестовый тип трансмиссии автомобиля.
     */
    private TransmissionType createTestTransmissionType(String name) {
        TransmissionType transmissionType = new TransmissionType();
        transmissionType.setName(name + "_" + System.currentTimeMillis());
        return transmissionType;
    }

    /**
     * Тестирует создание нового типа трансмиссии автомобиля в репозитории.
     * Проверяет, что после сохранения тип трансмиссии имеет присвоенный идентификатор,
     * имя сохраняется корректно и тип трансмиссии может быть найден по идентификатору.
     */
    @Test
    void whenCreateNewTransmissionTypeThenRepositoryHasSameTransmissionType() {
        TransmissionType transmissionType = createTestTransmissionType("Автоматическая");
        TransmissionType savedTransmissionType = transmissionTypeRepository.create(transmissionType);

        assertThat(savedTransmissionType.getId()).isGreaterThan(0);
        assertThat(savedTransmissionType.getName()).isEqualTo(transmissionType.getName());

        Optional<TransmissionType> foundTransmissionType = transmissionTypeRepository.findById(Math.toIntExact(savedTransmissionType.getId()));
        assertThat(foundTransmissionType).isPresent();
        assertThat(foundTransmissionType.get().getName()).isEqualTo(transmissionType.getName());
    }

    /**
     * Тестирует обновление данных типа трансмиссии автомобиля в репозитории.
     * Проверяет, что изменение имени корректно сохраняется
     * и может быть получено при последующем поиске.
     */
    @Test
    void whenUpdateTransmissionTypeThenRepositoryHasUpdatedTransmissionType() {
        TransmissionType transmissionType = createTestTransmissionType("Механическая");
        TransmissionType savedTransmissionType = transmissionTypeRepository.create(transmissionType);

        savedTransmissionType.setName("Роботизированная");
        transmissionTypeRepository.update(savedTransmissionType);

        Optional<TransmissionType> updatedTransmissionType = transmissionTypeRepository.findById(Math.toIntExact(savedTransmissionType.getId()));
        assertThat(updatedTransmissionType).isPresent();
        assertThat(updatedTransmissionType.get().getName()).isEqualTo("Роботизированная");
    }

    /**
     * Тестирует удаление типа трансмиссии автомобиля из репозитория.
     * Проверяет, что после удаления тип трансмиссии больше не находится в хранилище
     * при поиске по идентификатору.
     */
    @Test
    void whenDeleteTransmissionTypeThenRepositoryDoesNotHaveTransmissionType() {
        TransmissionType transmissionType = createTestTransmissionType("Вариатор");
        TransmissionType savedTransmissionType = transmissionTypeRepository.create(transmissionType);

        transmissionTypeRepository.delete(Math.toIntExact(savedTransmissionType.getId()));

        Optional<TransmissionType> deletedTransmissionType = transmissionTypeRepository.findById(Math.toIntExact(savedTransmissionType.getId()));
        assertThat(deletedTransmissionType).isNotPresent();
    }

    /**
     * Тестирует получение всех типов трансмиссии автомобилей с сортировкой по идентификатору.
     * Проверяет, что возвращаются все сохраненные типы трансмиссии в правильном порядке,
     * отсортированные по возрастанию идентификатора.
     */
    @Test
    void whenFindAllOrderByIdThenReturnsAllTransmissionTypesSortedById() {
        TransmissionType transmissionType1 = createTestTransmissionType("АКПП");
        TransmissionType transmissionType2 = createTestTransmissionType("МКПП");
        TransmissionType savedTransmissionType1 = transmissionTypeRepository.create(transmissionType1);
        TransmissionType savedTransmissionType2 = transmissionTypeRepository.create(transmissionType2);

        List<TransmissionType> allTransmissionTypes = transmissionTypeRepository.findAllOrderById();

        assertThat(allTransmissionTypes).hasSizeGreaterThanOrEqualTo(2);
        assertThat(allTransmissionTypes.get(0).getId()).isLessThan(allTransmissionTypes.get(1).getId());
    }

    /**
     * Тестирует поиск типа трансмиссии автомобиля по несуществующему идентификатору.
     * Проверяет, что для несуществующего идентификатора возвращается пустой Optional,
     * что соответствует ожидаемому поведению при отсутствии данных.
     */
    @Test
    void whenFindByNonExistentIdThenReturnsEmptyOptional() {
        Optional<TransmissionType> foundTransmissionType = transmissionTypeRepository.findById(-1);
        assertThat(foundTransmissionType).isNotPresent();
    }

    /**
     * Тестирует поиск типа трансмиссии автомобиля по нулевому идентификатору.
     * Проверяет граничное условие - возвращается пустой Optional для идентификатора 0,
     * так как в системе идентификаторы обычно начинаются с 1.
     */
    @Test
    void whenFindByIdWithZeroIdThenReturnsEmpty() {
        Optional<TransmissionType> result = transmissionTypeRepository.findById(0);
        assertThat(result).isNotPresent();
    }

    /**
     * Тестирует удаление несуществующего типа трансмиссии автомобиля.
     * Проверяет, что операция удаления для несуществующего идентификатора
     * завершается без генерации исключений, обеспечивая отказоустойчивость.
     */
    @Test
    void whenDeleteNonExistentTransmissionTypeThenNoException() {
        boolean deletionResult = true;
        try {
            transmissionTypeRepository.delete(-1);
        } catch (Exception e) {
            deletionResult = false;
        }
        assertThat(deletionResult).isTrue();
    }
}