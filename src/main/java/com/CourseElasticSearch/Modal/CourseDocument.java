package com.CourseElasticSearch.Modal;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.ZonedDateTime;

@Data
@Document(indexName = "courses")
@Setting(settingPath = "/elasticsearch-settings.json")
public class CourseDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(type = FieldType.Keyword)
    private String gradeRange;

    @Field(type = FieldType.Integer)
    private Integer minAge;

    @Field(type = FieldType.Integer)
    private Integer maxAge;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Date, pattern = "uuuu-MM-dd'T'HH:mm:ssX")
    private ZonedDateTime nextSessionDate;

    @Field(type = FieldType.Object)
    private Suggest suggest;

    @Data
    public static class Suggest {
        private String[] input;
    }
}
