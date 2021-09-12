package com.appspring.repository.specification;

import com.appspring.entity.Note;
import com.appspring.rest.dto.NoteFilterDto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.Objects;

public class NoteSpecificationBuilder {

    public static Specification<Note> getNoteConfigSpecification(NoteFilterDto filter) {

        var specification = (Specification<Note>)
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("deleted"), false);

        if (!StringUtils.isEmpty(filter.getName())) {
            specification = specification.and(inName(filter.getName()));
        }

        if (!StringUtils.isEmpty(filter.getDescription())) {
            specification = Objects.requireNonNull(specification).and(inDescription(filter.getDescription()));
        }

        return specification;
    }

    private static Specification<Note> inName(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Note.Fields.name), name);
    }

    private static Specification<Note> inDescription(String description) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get(Note.Fields.description)),
                "%" + description.toLowerCase() + "%"
        );
    }
}
