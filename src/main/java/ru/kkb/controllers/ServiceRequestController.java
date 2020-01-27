package ru.kkb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import ru.kkb.dto.ServiceRequestDTO;
import ru.kkb.service.FileService;
import ru.kkb.service.ServiceRequestService;

import java.util.List;

@RestController
@RequestMapping("/service/requests")
public class ServiceRequestController {

    @Autowired
    private ServiceRequestService serviceRequestService;

    @Autowired
    private FileService fileService;

    @PutMapping("/open")
    @ResponseBody
    public int openRequest(@RequestBody ServiceRequestDTO request) {
        return serviceRequestService.openRequest(request);
    }

    @PostMapping("/{requestNumber}/uploadFile")
    public void uploadFile(@PathVariable int requestNumber, @RequestParam CommonsMultipartFile file) {
        fileService.saveFile(requestNumber, file);
    }

    @PostMapping("/get")
    @ResponseBody
    public List<ServiceRequestDTO> getRequests(@RequestParam(defaultValue = "false") boolean isFast, @RequestBody RequestParams params) {
        return isFast ? serviceRequestService.getRequestsFast(params) : serviceRequestService.getRequestList(params);
    }

    @PutMapping("/close/{requestNumber}")
    public void closeRequest(@PathVariable int requestNumber) {
        serviceRequestService.closeRequest(requestNumber);
    }
}
