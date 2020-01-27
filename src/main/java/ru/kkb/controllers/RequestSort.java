package ru.kkb.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestSort {

    @JsonProperty("createDate")
    private SortingMode createDate;

    @JsonProperty("passport.surname")
    private SortingMode surname;

    @JsonProperty("passport.name")
    private SortingMode name;

    @JsonProperty("passport.patronymic")
    private SortingMode patronymic;

    @JsonProperty("serviceName")
    private SortingMode serviceName;

    @JsonProperty("active")
    private SortingMode active;
}
