package ru.kkb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import ru.kkb.configuration.ApplicationConfiguration;
import ru.kkb.controllers.RequestFilter;
import ru.kkb.controllers.RequestParams;
import ru.kkb.dao.ServiceRequestRepository;
import ru.kkb.dao.ServiceRequestsDao;
import ru.kkb.dao.entities.ServiceRequest;
import ru.kkb.dto.PassportDTO;
import ru.kkb.dto.ServiceRequestDTO;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ServiceRequestsServiceTest {

    @TestConfiguration
    static class CustomTestConfiguration {

        @Bean
        public ServiceRequestService serviceRequestService() {
            return new ServiceRequestServiceImpl();
        }

        @Bean
        public ModelMapper modelMapper() {
            ModelMapper modelMapper = new ModelMapper();
          //  modelMapper.typeMap(LocalDate.class, java.sql.Date.class).setProvider(request -> new Date(System.currentTimeMillis()));
         //   modelMapper.typeMap(java.sql.Date.class, LocalDate.class).setProvider(request -> LocalDate.now());
         /*   modelMapper.createTypeMap(ServiceRequestDTO.class, ServiceRequest.class)
                    .addMappings(mapping -> mapping.skip(ServiceRequest::setCreateDate))
                    .addMappings(mapping -> mapping.skip(ServiceRequest::setPassport));
            modelMapper.createTypeMap(ServiceRequest.class, ServiceRequestDTO.class)
                    .addMappings(mapping -> mapping.skip(ServiceRequestDTO::setCreateDate))
                    .addMappings(mapping -> mapping.skip(ServiceRequestDTO::setPassport)); */
            return modelMapper;
        }
    }

    @Autowired
    private ServiceRequestService serviceRequestService;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @MockBean
    private ServiceRequestsDao serviceRequestsDao;

    @MockBean
    private ObjectMapper objectMapper;

    @MockBean
    private ElasticsearchTemplate elasticsearchTemplate;

/*    @Before
    public void setUp() {
        ServiceRequest request = new ServiceRequest();
        request.setRequestNumber(1);


    } */

    @Test
    public void testOpenRequest() {
        PassportDTO passportDTO = new PassportDTO();
        passportDTO.setPassportNumber("123456789");

        ServiceRequestDTO serviceRequest = new ServiceRequestDTO();
        serviceRequest.setServiceName("Тестовая услуга");
        serviceRequest.setCreateDate(null);
        serviceRequest.setPassport(passportDTO);
        serviceRequest.setActive(false);

        int requestNumber = serviceRequestService.openRequest(serviceRequest);
        assertTrue(requestNumber > 0);
    }

    @Test
    public void testCloseRequest() {
        testOpenRequest();

        serviceRequestService.closeRequest(1);

        boolean isActive = true;
        Optional<ServiceRequest> request = serviceRequestRepository.findById(1);
        if (request.isPresent()) {
            isActive = request.get().getActive();
        }

        assertFalse(isActive);
    }
}
