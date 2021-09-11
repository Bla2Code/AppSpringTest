package com.appspring.repository.specification;

import com.appspring.entity.User;
import com.appspring.rest.dto.UserFilterDto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class UserSpecificationBuilder {

    public static Specification<User> getEventConfigSpecification(UserFilterDto filter) {

        var specification = (Specification<User>)
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("deleted"), false);

        if (!StringUtils.isEmpty(filter.getLogin())) {
            specification = specification.and(inLogin(filter.getLogin()));
        }


        return specification;
    }

    private static Specification<User> inLogin(String login) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("login"), login);
    }
}
