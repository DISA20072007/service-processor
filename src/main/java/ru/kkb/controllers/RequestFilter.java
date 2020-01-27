package ru.kkb.controllers;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import static ru.kkb.configuration.ApplicationConfiguration.DATE_FORMAT;

@Getter
@Setter
public class RequestFilter {

    @JsonProperty("createDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private LocalDate createDate;

    @JsonProperty("passport.surname")
    private String surname;

    @JsonProperty("passport.name")
    private String name;

    @JsonProperty("passport.patronymic")
    private String patronymic;

    @JsonProperty("serviceName")
    private String serviceName;

    @JsonProperty("active")
    private Boolean active;
}
