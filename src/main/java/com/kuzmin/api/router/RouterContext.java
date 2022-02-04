package com.kuzmin.api.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterContext {
    @Bean
    RouterFunction<ServerResponse> routes(CourseHandler courseHandler) {
        return route(GET("/courses").and(accept(APPLICATION_JSON)), courseHandler::findAllCourses)
                .andRoute(GET("/courses/{id}").and(accept(APPLICATION_JSON)), courseHandler::findCourseById)
                .andRoute(POST("/courses").and(accept(APPLICATION_JSON)), courseHandler::createCourse)
                .andRoute(PUT("/courses").and(accept(APPLICATION_JSON)), courseHandler::updateCourse)
                .andRoute(DELETE("/courses/{id}").and(accept(APPLICATION_JSON)), courseHandler::deleteCourse)
                .andRoute(DELETE("/courses").and(accept(APPLICATION_JSON)), courseHandler::deleteAllCourses);
    }
}
