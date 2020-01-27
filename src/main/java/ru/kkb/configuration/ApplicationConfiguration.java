package ru.kkb.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.hibernate.SessionFactory;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.DefaultEntityMapper;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.TemporalField;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.TimeZone;

@Configuration
public class ApplicationConfiguration {

    public final static String DATE_FORMAT = "dd.MM.yyyy";

    @Value("${spring.jpa.properties.hibernate.dialect}")
    private String hibernateDialect;

    @Value("${elasticsearch.host}")
    private String elasticSearchHost;

    @Value("${elasticsearch.port}")
    private int elasticSearchPort;

    @Autowired
    private DataSource dataSource;

    @Bean(name = "entityManagerFactory")
    public SessionFactory getSessionFactory() throws Exception {
        Properties properties = new Properties();

        properties.setProperty("hibernate.dialect", hibernateDialect);

        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();

        factoryBean.setPackagesToScan("ru.kkb.dao.entities");
        factoryBean.setDataSource(dataSource);
        factoryBean.setHibernateProperties(properties);
        factoryBean.afterPropertiesSet();

        return factoryBean.getObject();
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);

        addEntityToDtoMappings(modelMapper);
        addDtoToEntityMappings(modelMapper);

        return modelMapper;
    }

    @Bean
    public ElasticsearchTemplate elasticsearchTemplate() {
        ElasticsearchTemplate template;

        try {
            TransportAddress transportAddress = new TransportAddress(InetAddress.getByName(elasticSearchHost), elasticSearchPort);
            Client client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(transportAddress);
            template = new ElasticsearchTemplate(client, new CustomEntityMapper());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return template;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().setDateFormat(new SimpleDateFormat(DATE_FORMAT)).registerModule(new JavaTimeModule());
    }

    private void addDtoToEntityMappings(ModelMapper modelMapper) {
        Converter<LocalDate, java.sql.Date> date2DateConverter = (context) -> {
            Optional<LocalDate> localDate = Optional.ofNullable(context.getSource());

            return localDate.map(Date::valueOf).orElse(null);
        };

        modelMapper.typeMap(LocalDate.class, java.sql.Date.class).setConverter(date2DateConverter);
    }

    private void addEntityToDtoMappings(ModelMapper modelMapper) {
        Converter<java.sql.Date, LocalDate> date2DateConverter = (context) -> {
            Optional<java.sql.Date> date = Optional.ofNullable(context.getSource());

            return date.map(Date::toLocalDate).orElse(null);
        };

        modelMapper.typeMap(java.sql.Date.class, LocalDate.class).setConverter(date2DateConverter).setProvider(request -> LocalDate.now());
    }
}
