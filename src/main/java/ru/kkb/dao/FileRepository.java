package ru.kkb.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kkb.dao.entities.File;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {

    @Query("FROM File WHERE requestNumber = :requestNumber")
    List<File> getFiles(@Param("requestNumber") int requestNumber);
}
