package ru.kkb.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import static ru.kkb.configuration.ApplicationConfiguration.DATE_FORMAT;

@Getter
@Setter
@Document(indexName = "establishment", type = "service_request")
public class ServiceRequestDTO {

    public ServiceRequestDTO() {
        super();

        this.createDate = LocalDate.now();
        this.active = true;
    }

    @Id
    private Integer requestNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = DATE_FORMAT)
    private LocalDate createDate;

    @Field(type = FieldType.Text)
    private String serviceName;

    @Field(type = FieldType.Boolean)
    private boolean active;

    @Field(type = FieldType.Object)
    private PassportDTO passport;
}
