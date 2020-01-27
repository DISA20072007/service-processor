package ru.kkb.dao;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kkb.controllers.SortingMode;
import ru.kkb.dao.entities.Passport;
import ru.kkb.dao.entities.ServiceRequest;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static ru.kkb.configuration.ApplicationConfiguration.DATE_FORMAT;

@Repository
@Transactional
public class ServiceRequestsDaoImpl implements ServiceRequestsDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<ServiceRequest> getRequests(Map<String, Object> filter, Map<String, Object> sort) {
        CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();

        CriteriaQuery<ServiceRequest> criteriaQuery = criteriaBuilder.createQuery(ServiceRequest.class);
        Root<ServiceRequest> requests = criteriaQuery.from(ServiceRequest.class);
        requests.alias("requests");

        Join<ServiceRequest, Passport> passportJoin = requests.join("passport");
        passportJoin.alias("passport");

        if (filter != null) {
            passportJoin.on(getFilterList(criteriaBuilder, List.of(requests, passportJoin), filter));
        }

        if (sort != null) {
            criteriaQuery.orderBy(getOrderList(criteriaBuilder, List.of(requests, passportJoin), sort));
        }

        List<ServiceRequest> serviceRequests;
        try (Session session = sessionFactory.openSession()) {
            TypedQuery<ServiceRequest> query = session.createQuery(criteriaQuery);

            serviceRequests = query.getResultList();
        }

        return serviceRequests;
    }

    private Predicate[] getFilterList(CriteriaBuilder criteriaBuilder, List<From> fromList, Map<String, Object> filter) {
        List<Predicate> filterList = new ArrayList<>();

        filter.forEach((key, value) -> {
            if (value != null) {
                Expression expression = getExpression(key, fromList);

                if (expression != null) {
                    if (StringUtils.containsIgnoreCase(key, "date")) {
                        try {
                            Date date = new SimpleDateFormat(DATE_FORMAT).parse(value.toString());
                            value = new java.sql.Date(date.getTime());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    filterList.add(criteriaBuilder.equal(expression, value));
                }
            }
        });

        return filterList.toArray(new Predicate[]{});
    }

    private List<Order> getOrderList(CriteriaBuilder criteriaBuilder, List<From> fromList, Map<String, Object> sort) {
        List<Order> orderList = new ArrayList<>();

        sort.forEach( (key, value) -> {
            Expression expression = getExpression(key, fromList);

            if (SortingMode.ASC.equals(value)) {
                orderList.add(criteriaBuilder.asc(expression));
            } else {
                orderList.add(criteriaBuilder.desc(expression));
            }
        });

        return orderList;
    }

    private Expression getExpression(String key, List<From> fromList) {
        String alias = key.split("\\.")[0];
        String field = key.split("\\.")[1];

        for (From from : fromList) {
            if (alias.equals(from.getAlias())) {
                return from.get(field);
            }
        }

        return null;
    }
}
