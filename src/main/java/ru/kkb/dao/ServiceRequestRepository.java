package ru.kkb.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kkb.dao.entities.ServiceRequest;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Integer> {

    @Query("FROM ServiceRequest AS request INNER JOIN Passport AS passport " +
            "ON passport.passportNumber = request.passport.passportNumber WHERE request.requestNumber = :requestNumber")
    ServiceRequest getServiceRequest(@Param("requestNumber") int requestNumber);

    @Modifying
    @Query("UPDATE ServiceRequest SET active = false WHERE requestNumber = :requestNumber")
    void closeRequest(@Param("requestNumber") int requestNumber);
}
