package com.ote.crud;

import com.ote.crud.model.Filter;
import com.ote.crud.model.Filters;
import com.ote.crud.model.IEntity;
import com.ote.crud.model.IPayload;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface IMapperService<TP extends IPayload, TE extends IEntity> {

    TE convert(TP payload);

    TP convert(TE payload);

    Predicate getPredicate(Filter filter, Root<TE> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder);

    default Specification<TE> getFilter(Filters filters) {
        assert filters != null && !filters.isEmpty();

        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(filters.getContent().stream().map(filter -> getPredicate(filter, root, criteriaQuery, criteriaBuilder)).toArray(Predicate[]::new));
    }
}
