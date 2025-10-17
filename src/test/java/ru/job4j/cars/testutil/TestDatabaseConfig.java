package ru.job4j.cars.testutil;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Утилитный класс для настройки тестовой среды базы данных.
 * Предоставляет методы для инициализации базы данных и создания SessionFactory.
 */
public class TestDatabaseConfig {

    private TestDatabaseConfig() {
    }

    /**
     * Загружает свойства для тестовой базы данных из указанного файла.
     * @param propertiesFileName имя файла свойств
     * @return объект Properties с загруженными настройками
     * @throws IOException если файл не найден или произошла ошибка чтения
     */
    public static Properties loadTestProperties(String propertiesFileName) throws IOException {
        Properties props = new Properties();
        try (InputStream input = TestDatabaseConfig.class.getClassLoader().getResourceAsStream(propertiesFileName)) {
            if (input == null) {
                throw new IOException("Файл конфигурации " + propertiesFileName + " не найден в classpath тестов.");
            }
            props.load(input);
        }
        return props;
    }

    /**
     * Инициализирует базу данных с помощью Liquibase.
     * @param props свойства подключения к базе данных
     * @throws Exception если произошла ошибка при инициализации
     */
    public static void initializeDatabase(Properties props) throws Exception {
        String url = props.getProperty("url");
        String driver = props.getProperty("driver");
        String username = props.getProperty("username");
        String password = props.getProperty("password");

        if (driver != null && !driver.isEmpty()) {
            Class.forName(driver);
        }

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("db/dbchangelog.xml",
                    new ClassLoaderResourceAccessor(), database);
            liquibase.dropAll();
            liquibase.update(new Contexts());
        }
    }

    /**
     * Создает и настраивает SessionFactory для тестов.
     * @param props свойства для настройки Hibernate
     * @return настроенный SessionFactory
     */
    public static SessionFactory createSessionFactory(Properties props) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml"); // Будет искать в test/resources

        configuration.setProperty("hibernate.connection.url", props.getProperty("url"));
        configuration.setProperty("hibernate.connection.username", props.getProperty("username"));
        configuration.setProperty("hibernate.connection.password", props.getProperty("password"));
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

        String driver = props.getProperty("driver");
        if (driver != null && !driver.isEmpty()) {
            configuration.setProperty("hibernate.connection.driver_class", driver);
        } else {
            configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        }

        StandardServiceRegistry registry = configuration.getStandardServiceRegistryBuilder().build();

        try {
            return configuration.buildSessionFactory(registry);
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
            throw e;
        }
    }

    /**
     * Полностью настраивает тестовую среду и возвращает SessionFactory.
     * @param propertiesFileName имя файла свойств
     * @return настроенный SessionFactory
     * @throws Exception если произошла ошибка при настройке
     */
    public static SessionFactory setupTestEnvironment(String propertiesFileName) throws Exception {
        Properties testProps = loadTestProperties(propertiesFileName);
        initializeDatabase(testProps);
        return createSessionFactory(testProps);
    }
}