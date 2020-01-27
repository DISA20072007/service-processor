package ru.kkb.dao;

import ru.kkb.dao.entities.ServiceRequest;

import java.util.List;
import java.util.Map;

public interface ServiceRequestsDao {

    List<ServiceRequest> getRequests(Map<String, Object> filter, Map<String, Object> sort);
}
