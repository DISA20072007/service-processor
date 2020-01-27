package ru.kkb.service;

import ru.kkb.controllers.RequestParams;
import ru.kkb.dto.ServiceRequestDTO;

import java.util.List;

public interface ServiceRequestService {

    int openRequest(ServiceRequestDTO request);
    List<ServiceRequestDTO> getRequests(RequestParams requestParams);
    void closeRequest(int requestNumber);
    List<ServiceRequestDTO> getRequestsFast(RequestParams requestParams);
    List<ServiceRequestDTO> getRequestList(RequestParams requestParams);
}
