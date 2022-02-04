package com.kuzmin.api.controller;

import com.kuzmin.api.model.Course;
import com.kuzmin.api.repository.CourseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/courses/")
public class CourseController {
    private final CourseRepository courseRepository;

    @Autowired
    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public Flux<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<Course>> getCourseById(@PathVariable("id") String courseId) {
        return courseRepository.findById(courseId)

                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("category/{name}")
    public Flux<Course> getCourseByCategory(@PathVariable("name") String category) {
        return courseRepository.findAllByCategory(category)

                .doOnError(e -> log.error("Failed to create course", e.getMessage()));
    }

    @PostMapping
    public Mono<Course> createCourse(@RequestBody Course course) {
        return courseRepository.save(course)
                .doOnSuccess(updatedCourse -> log.info("Successfully created course", updatedCourse))
                .doOnError(e -> log.error("Failed to create course", e.getMessage()));
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<Course>> updateCourse(@PathVariable("id") String courseId, @RequestBody Course course) {
        return this.courseRepository.findById(courseId).flatMap(existingCourse -> {
                    existingCourse.setName(course.getName());
                    existingCourse.setRating(course.getRating());
                    existingCourse.setCategory(course.getCategory());
                    existingCourse.setDescription(course.getDescription());
                    return this.courseRepository.save(existingCourse);
                }).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build())
                .doOnError(e -> log.error("Failed to update course", e.getMessage()));
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Course>> deleteCourseById(@PathVariable("id") String courseId) {
        return this.courseRepository.findById(courseId).flatMap(
                        course -> this.courseRepository.deleteById(course.getId()).then(Mono.just(ResponseEntity.ok(course))))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public Mono<Void> deleteCourses() {
        return courseRepository.deleteAll();
    }
}
