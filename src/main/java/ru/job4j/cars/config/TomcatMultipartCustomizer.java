package ru.job4j.cars.config;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация веб-сервера для увеличения максимального количества частей multipart-запроса
 */
@Configuration
public class TomcatMultipartCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    private static final Logger log = LoggerFactory.getLogger(TomcatMultipartCustomizer.class);

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        log.info("Настройка Tomcat для увеличения лимитов multipart-запросов");

        factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                // Для Tomcat 10+ используем system property
                System.setProperty("org.apache.tomcat.util.http.Parameters.MAX_COUNT", "1000");
                System.setProperty("org.apache.tomcat.util.http.Parameters.MAX_HEADER_COUNT", "1000");

                log.info("Tomcat connector настроен: maxPostSize=50MB, maxSavePostSize=50MB");
            }
        });
    }
}
