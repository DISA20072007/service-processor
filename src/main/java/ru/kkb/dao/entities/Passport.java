package ru.kkb.dao.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "PASSPORT")
@Getter
@Setter
public class Passport {

    @Id
    @Column(name = "PASSPORT_NUMBER")
    private String passportNumber;

    @Column(name = "SURNAME")
    private String surname;

    @Column(name = "NAME")
    private String name;

    @Column(name = "PATRONYMIC")
    private String patronymic;

    @Column(name = "BIRTH_DATE")
    private Date birthDate;

    @Column(name = "BIRTH_PLACE")
    private String birthPlace;

    @Column(name = "ISSUE_DATE")
    private Date issueDate;

    @Column(name = "ISSUE_PLACE")
    private String issuePlace;
}
