package ru.kkb.controllers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestParams {

    private RequestFilter filter;

    private RequestSort sort;
}
