package com.CourseElasticSearch.Controller;

import com.CourseElasticSearch.Modal.CourseDocument;
import com.CourseElasticSearch.Service.CourseSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class CourseSearchController {

    @Autowired
    private CourseSearchService courseSearchService;

    @GetMapping
    public Map<String, Object> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
            @RequestParam(defaultValue = "upcoming") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        SearchHits<CourseDocument> searchHits = courseSearchService.searchCourses(
                q, minAge, maxAge, category, type, minPrice, maxPrice, startDate, sort, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("total", searchHits.getTotalHits());
        response.put("courses", searchHits.getSearchHits().stream()
                .map(hit -> {
                    CourseDocument course = hit.getContent();
                    Map<String, Object> courseMap = new HashMap<>();
                    courseMap.put("id", course.getId());
                    courseMap.put("title", course.getTitle());
                    courseMap.put("category", course.getCategory());
                    courseMap.put("price", course.getPrice());
                    courseMap.put("nextSessionDate", course.getNextSessionDate());
                    return courseMap;
                })
                .toList());

        return response;
    }

    @GetMapping("/suggest")
    public List<String> suggest(@RequestParam String q) {
        return courseSearchService.suggestCourses(q);
    }
}
