package com.ote.crud;

import com.ote.crud.model.Filter;
import com.ote.crud.model.Filters;
import com.ote.crud.model.IEntity;
import com.ote.crud.model.IPayload;
import org.springframework.data.jpa.domain.Specification;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface IMapperService<TP extends IPayload, TE extends IEntity> {

    TE convert(TP payload);

    TP convert(TE payload);

    default Specification<TE> getFilter(Filters filters) {
        assert filters != null && !filters.isEmpty();

        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(filters.getContent().stream().map(filter -> getPredicate(filter, root, criteriaQuery, criteriaBuilder)).toArray(Predicate[]::new));
    }

    default Predicate getPredicate(Filter filter, Root<TE> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

        switch (filter.getOperator()) {
            case EQUALS:
                return criteriaBuilder.equal(root.get(filter.getProperty()), filter.getValue());
            case DIFFERENT:
                return criteriaBuilder.notEqual(root.get(filter.getProperty()), filter.getValue());
            case GREATER_THAN:
                return criteriaBuilder.greaterThan(root.get(filter.getProperty()), filter.getValue());
            case GREATER_THAN_OR_EQUALS:
                return criteriaBuilder.greaterThanOrEqualTo(root.get(filter.getProperty()), filter.getValue());
            case LESS_THAN:
                return criteriaBuilder.lessThan(root.get(filter.getProperty()), filter.getValue());
            case LESS_THAN_OR_EQUALS:
                return criteriaBuilder.lessThanOrEqualTo(root.get(filter.getProperty()), filter.getValue());
            case LIKE:
                return criteriaBuilder.like(root.get(filter.getProperty()), "%" + filter.getValue() + "%");
            default:
                throw new NotImplementedException();
        }
    }
}
