package com.CourseElasticSearch.config;

import com.CourseElasticSearch.Modal.CourseDocument;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class CourseInitializer {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private ObjectMapper objectMapper;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() throws IOException {
        if (!elasticsearchOperations.indexOps(CourseDocument.class).exists()) {
            elasticsearchOperations.indexOps(CourseDocument.class).create();

            ClassPathResource resource = new ClassPathResource("sample-courses.json");
            List<CourseDocument> courses = objectMapper.readValue(
                    resource.getInputStream(),
                    new TypeReference<List<CourseDocument>>(){}
            );

            courses.forEach(course ->
                    elasticsearchOperations.save(course)
            );

            elasticsearchOperations.indexOps(CourseDocument.class).refresh();
        System.out.println("📦 Initializing course data...");
        System.out.println("✅ Courses loaded: " + courses.size());
        }
    }
}
