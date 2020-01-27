package ru.kkb.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.Date;

import static ru.kkb.configuration.ApplicationConfiguration.DATE_FORMAT;

@Getter
@Setter
public class PassportDTO {

    @Field(type = FieldType.Text)
    private String passportNumber;

    @Field(type = FieldType.Text)
    private String surname;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String patronymic;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT, timezone = "GMT+03:00")
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = DATE_FORMAT)
    private LocalDate birthDate;

    @Field(type = FieldType.Text)
    private String birthPlace;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT, timezone = "GMT+03:00")
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = DATE_FORMAT)
    private LocalDate issueDate;

    @Field(type = FieldType.Text)
    private String issuePlace;
}
