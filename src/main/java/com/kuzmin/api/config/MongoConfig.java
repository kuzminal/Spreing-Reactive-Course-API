package com.kuzmin.api.config;

import com.kuzmin.api.model.Course;
import com.kuzmin.api.repository.CourseRepository;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.kuzmin.api.repository")
public class MongoConfig extends AbstractReactiveMongoConfiguration {
    @Value("${spring.data.mongodb.database}")
    private String databaseName;
    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create();
    }

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }


    static String role(String auth) {
        return "ROLE_" + auth;
    }

    static final String USER = "USER";
    static final String INVENTORY = "INVENTORY";

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner userLoader(ReactiveMongoTemplate template) {
        return args -> {
            template.save(new com.kuzmin.api.model.User(
                    "alex", passwordEncoder.encode( "password"), List.of(role(USER)))).subscribe();

            template.save(new com.kuzmin.api.model.User(
                    "ustas", passwordEncoder.encode("password"), Arrays.asList(role(USER), role(INVENTORY)))).subscribe();
        };
    }

    @Bean
    public CommandLineRunner courseLoader(CourseRepository courseRepository) {
        return args -> {
            Course course1 = Course.builder().name("Mastering Spring Boot").category("Spring").rating(4)
                    .description("Mastering Spring Boot").build();
            Course course2 = Course.builder().name("Mastering Python").category("Python").rating(5)
                    .description("Mastering Python").build();
            Course course3 = Course.builder().name("Mastering Go").category("Go").rating(3).description("Mastering Go")
                    .build();

            Flux
                    .just(course1, course2, course3)
                    .flatMap(courseRepository::save)
                    .thenMany(courseRepository.findAll())
                    .subscribe();
        };
    }
}
