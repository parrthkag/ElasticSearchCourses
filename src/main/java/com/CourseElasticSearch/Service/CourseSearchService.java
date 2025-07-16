package com.CourseElasticSearch.Service;

import com.CourseElasticSearch.Modal.CourseDocument;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class CourseSearchService {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    public SearchHits<CourseDocument> searchCourses(
            String query, Integer minAge, Integer maxAge, String category, String type,
            Double minPrice, Double maxPrice, ZonedDateTime startDate,
            String sort, int page, int size) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // Full-text search with fuzzy matching
        if (query != null && !query.isEmpty()) {
            boolQuery.must(QueryBuilders.multiMatchQuery(query, "title", "description")
                    .fuzziness(Fuzziness.AUTO));
        }

        // Filters
        if (minAge != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("minAge").gte(minAge));
        }
        if (maxAge != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("maxAge").lte(maxAge));
        }
        if (category != null && !category.isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("category", category));
        }
        if (type != null && !type.isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("type", type));
        }
        if (minPrice != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("price").gte(minPrice));
        }
        if (maxPrice != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("price").lte(maxPrice));
        }
        if (startDate != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("nextSessionDate").gte(startDate));
        }

        // Sorting
        Sort sortObj;
        switch (sort != null ? sort : "upcoming") {
            case "priceAsc":
                sortObj = Sort.by(Sort.Direction.ASC, "price");
                break;
            case "priceDesc":
                sortObj = Sort.by(Sort.Direction.DESC, "price");
                break;
            default:
                sortObj = Sort.by(Sort.Direction.ASC, "nextSessionDate");
        }

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(page, size, sortObj))
                .build();
        return elasticsearchOperations.search(searchQuery, CourseDocument.class);
    }

    public List<String> suggestCourses(String partialTitle) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchPhrasePrefixQuery("title", partialTitle))
                .withMaxResults(10)
                .build();

        return elasticsearchOperations.search(searchQuery, CourseDocument.class)
                .getSearchHits()
                .stream()
                .map(hit -> hit.getContent().getTitle())
                .toList();
    }
}
