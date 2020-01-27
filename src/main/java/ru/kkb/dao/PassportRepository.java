package ru.kkb.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kkb.dao.entities.Passport;

public interface PassportRepository extends JpaRepository<Passport, String> {
}
