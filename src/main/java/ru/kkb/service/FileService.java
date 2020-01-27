package ru.kkb.service;

import org.springframework.web.multipart.commons.CommonsMultipartFile;
import ru.kkb.dao.entities.File;

import java.util.List;

public interface FileService {

    List<File> getFiles(int requestNumber);
    void saveFile(int requestNumber, CommonsMultipartFile file);
}
