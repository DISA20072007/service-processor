package ru.kkb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import ru.kkb.dao.FileRepository;
import ru.kkb.dao.ServiceRequestRepository;
import ru.kkb.dao.ServiceRequestsDao;
import ru.kkb.dao.entities.File;
import ru.kkb.dao.entities.ServiceRequest;

import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileRepository fileRepository;

    @Override
    public List<File> getFiles(int requestNumber) {
        return fileRepository.getFiles(requestNumber);
    }

    @Override
    public void saveFile(int requestNumber, CommonsMultipartFile file) {
        fileRepository.saveAndFlush(new File(requestNumber, file.getOriginalFilename(), file.getBytes()));
    }
}
