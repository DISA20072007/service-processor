package ru.kkb.dao.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Blob;
import java.util.List;

@Entity
@Table(name = "REQUEST_FILES")
@Getter
@Setter
@NoArgsConstructor
public class File {

    public File(int requestNumber, String fileName, byte[] fileData) {
        this.requestNumber = requestNumber;
        this.fileName = fileName;
        this.fileData = fileData;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FILE_ID")
    private int fileId;

    @Column(name = "REQUEST_NUMBER")
    private int requestNumber;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "FILE_DATA")
    private byte[] fileData;

    @ManyToOne
    @JoinColumn(name = "REQUEST_NUMBER", insertable = false, updatable = false)
    private ServiceRequest request;
}
