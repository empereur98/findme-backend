package com.dhi.findme_backend.repository.specification;

import com.dhi.findme_backend.entity.User;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> filterBy(String search, String country, String plan) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isEmpty()) {
                String likePattern = "%" + search.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern)
                ));
            }

            if (plan != null && !plan.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("plan"), plan));
            }

            // Le filtre par pays nécessite une jointure avec les adresses
            if (country != null && !country.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("defaultLocation"), country));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
