package ru.kkb.dao.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

import static org.hibernate.annotations.CascadeType.*;

@Entity
@Table(name = "SERVICE_REQUESTS")
@Getter
@Setter
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUEST_NUMBER")
    private Integer requestNumber;

    @Column(name = "CREATE_DATE", updatable = false)
    private Date createDate;

    @Column(name = "SERVICE_NAME")
    private String serviceName;

    @Column(name = "ACTIVE")
    private Boolean active;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "PASSPORT_NUMBER")
    @Cascade(SAVE_UPDATE)
    private Passport passport;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "request")
    private List<File> files;

    @PrePersist
    public void toCreate() {
        setActive(true);
        setCreateDate(new Date(System.currentTimeMillis()));
    }
}
