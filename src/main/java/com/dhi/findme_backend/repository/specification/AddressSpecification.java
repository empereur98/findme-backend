package com.dhi.findme_backend.repository.specification;

import com.dhi.findme_backend.entity.Address;
import com.dhi.findme_backend.entity.User;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class AddressSpecification {

    public static Specification<Address> filterBy(String search, String country, String city, String type, String status, User user) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (user != null) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), user.getId()));
            }

            if (search != null && !search.isEmpty()) {
                String likePattern = "%" + search.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("district")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("street")), likePattern)
                ));
            }

            if (country != null && !country.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("country"), country));
            }

            if (city != null && !city.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("city"), city));
            }

            if (type != null && !type.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }

            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}