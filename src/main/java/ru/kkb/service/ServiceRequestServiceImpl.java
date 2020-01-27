package ru.kkb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;
import ru.kkb.controllers.RequestParams;
import ru.kkb.dao.PassportRepository;
import ru.kkb.dao.ServiceRequestRepository;
import ru.kkb.dao.ServiceRequestsDao;
import ru.kkb.dao.entities.ServiceRequest;
import ru.kkb.dto.ServiceRequestDTO;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceRequestServiceImpl implements ServiceRequestService {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private PassportRepository passportRepository;

    @Autowired
    private ServiceRequestsDao serviceRequestsDao;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public int openRequest(ServiceRequestDTO request) {
        ServiceRequest serviceRequest = modelMapper.map(request, ServiceRequest.class);
        int requestNumber = saveRequest(serviceRequest);

        ServiceRequestDTO requestDTO = modelMapper.map(serviceRequest, ServiceRequestDTO.class);
        requestDTO.setRequestNumber(requestNumber);

        IndexQuery indexQuery = new IndexQueryBuilder().withObject(requestDTO).build();
        elasticsearchTemplate.index(indexQuery);

        return requestNumber;
    }

    @Override
    public List<ServiceRequestDTO> getRequests(RequestParams params) {
        Map<String, Object> filterMap = objectMapper.convertValue(params.getFilter(), Map.class);
        Map<String, Object> sortMap = objectMapper.convertValue(params.getSort(), Map.class);

        List<ServiceRequestDTO> requests = new ArrayList<>();
        for (ServiceRequest request : serviceRequestsDao.getRequests(filterMap, sortMap)) {
            requests.add(modelMapper.map(request, ServiceRequestDTO.class));
        }
        return requests;
    }

    @Override
    public void closeRequest(int requestNumber) {
        serviceRequestRepository.closeRequest(requestNumber);
        serviceRequestRepository.flush();

        Optional<ServiceRequest> optional = serviceRequestRepository.findById(requestNumber);
        if (optional.isPresent()) {
            ServiceRequest serviceRequest = serviceRequestRepository.getServiceRequest(requestNumber);

            ServiceRequestDTO requestDTO = modelMapper.map(serviceRequest, ServiceRequestDTO.class);

            UpdateQuery updateQuery = new UpdateQueryBuilder().withId(Objects.toString(requestNumber)).withUpdateRequest(
                    new UpdateRequest().doc(new ObjectMapper().convertValue(requestDTO, Map.class))).withClass(ServiceRequestDTO.class).build();

            elasticsearchTemplate.update(updateQuery);
        }
    }

    @Override
    public List<ServiceRequestDTO> getRequestsFast(RequestParams requestParams) {
        QueryBuilder filterBuilder = getFilterBuilder(BeanMap.create(requestParams.getFilter()));
        SortBuilder sortBuilder = null;
        if (requestParams.getSort() != null) {
            sortBuilder = getSortBuilder(BeanMap.create(requestParams.getSort()));
        }

        SearchQuery searchQuery = new NativeSearchQuery(new MatchAllQueryBuilder(), filterBuilder, sortBuilder != null ? List.of(sortBuilder) : List.of());
        return elasticsearchTemplate.queryForList(searchQuery, ServiceRequestDTO.class);
    }

    @Override
    public List<ServiceRequestDTO> getRequestList(RequestParams requestParams) {
        Map<String, Object> sortMap = objectMapper.convertValue(requestParams.getSort(), Map.class);

        List<Sort.Order> orderList = new ArrayList<>();
        sortMap.forEach( (field, sortingOrder) -> {
            if (sortingOrder != null) {
                orderList.add(new Sort.Order(Direction.fromString(String.valueOf(sortingOrder)), field));
            }
        });

        ServiceRequest serviceRequest = modelMapper.map(requestParams.getFilter(), ServiceRequest.class);

        return serviceRequestRepository.findAll(Example.of(serviceRequest), Sort.by(orderList)).stream()
                .map((request) -> modelMapper.map(request, ServiceRequestDTO.class)).collect(Collectors.toList());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private int saveRequest(ServiceRequest request) {
        if (request.getPassport() != null) {
            String passportNumber = request.getPassport().getPassportNumber();

            if (passportRepository.existsById(passportNumber)) {
                request.setPassport(passportRepository.findById(passportNumber).get());
            }
        }

        request = serviceRequestRepository.saveAndFlush(request);

        return request.getRequestNumber();
    }

    private QueryBuilder getFilterBuilder(Map<String, Object> filterMap) {
        QueryBuilder filterBuilder = null;
        for (Map.Entry<String, Object> entry : filterMap.entrySet()) {
            if (entry.getValue() != null) {
                filterBuilder = QueryBuilders.matchQuery(entry.getKey(), entry.getValue());
            }
        }
        return filterBuilder;
    }

    private SortBuilder getSortBuilder(Map<String, Object> sortMap) {
        SortBuilder sortBuilder = null;
        for (Map.Entry<String, Object> entry : sortMap.entrySet()) {
            String value = Objects.toString(entry.getValue(), null);

            if (value != null) {
                sortBuilder = SortBuilders.fieldSort(entry.getKey()).order(SortOrder.fromString(value));
            }
        }

        return sortBuilder;
    }
}
