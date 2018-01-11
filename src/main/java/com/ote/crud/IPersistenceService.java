package com.ote.crud;

import com.ote.common.BeanUtils;
import com.ote.crud.exception.CreateException;
import com.ote.crud.exception.MergeException;
import com.ote.crud.exception.ResetException;
import com.ote.crud.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface IPersistenceService<TP extends IPayload> {

    <TE extends IEntity> IEntityRepository<TE> getEntityRepository();

    //<TE extends IEntity> IMapperService<TP, TE> getMapperService();

    String getEntityName();

    int getDefaultPageSize();

    default Optional<TP> findOne(long id) {
        return Optional.ofNullable(getEntityRepository().findOne(id)).
                map(IEntity::convert);
    }

    default List<TP> findMany(Filters filters) {
        List<IEntity> list;
        if (filters == null || filters.isEmpty()) {
            list = getEntityRepository().findAll();
        } else {
            Specification<IEntity> filterSpecification = getFilter(filters);
            list = getEntityRepository().findAll(filterSpecification);
        }
        return list.stream().map(IEntity::<TP>convert).collect(Collectors.toList());
    }

    default SplitList<TP> findMany(Filters filters, SortingParameters sortingParam, SplitListParameter splitListParam) {
        sortingParam = getOrDefault(sortingParam);
        splitListParam = getOrDefault(splitListParam);
        Pageable pageable = getPageable(sortingParam, splitListParam);
        Page<IEntity> page;
        if (filters == null || filters.isEmpty()) {
            page = getEntityRepository().findAll(pageable);
        } else {
            Specification<IEntity> filterSpecification = getFilter(filters);
            page = getEntityRepository().findAll(filterSpecification, pageable);
        }
        return convert(page, sortingParam, splitListParam);
    }

    default TP create(TP payload) throws CreateException {
        try {
            payload.setId(0);
            IEntity entity = payload.convert();
            return getEntityRepository().save(entity).convert();
        } catch (Exception e) {
            throw new CreateException(getEntityName(), e);
        }
    }

    default Optional<TP> reset(long id, TP payload) throws ResetException {
        try {
            if (getEntityRepository().exists(id)) {
                IEntity entity = payload.convert();
                entity.setId(id);
                return Optional.of(getEntityRepository().save(entity)).map(IEntity::convert);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new ResetException(getEntityName(), id, e);
        }
    }

    default Optional<TP> merge(long id, TP payload) throws MergeException {
        try {
            Optional<IEntity> currentEntity = Optional.ofNullable(getEntityRepository().findOne(id));
            if (currentEntity.isPresent()) {
                IEntity entity = payload.convert();
                return Optional.of(merge(entity, currentEntity.get())).map(IEntity::convert);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new MergeException(getEntityName(), id, e);
        }
    }

    default void deleteOne(long id) {
        if (getEntityRepository().exists(id)) {
            getEntityRepository().delete(id);
        }
    }

    default void deleteMany(Filters filters) {
        if (filters == null || filters.isEmpty()) {
            getEntityRepository().deleteAll();
        } else {
            findMany(filters).parallelStream().forEach(p -> deleteOne(p.getId()));
        }
    }

    default IEntity merge(IEntity fromEntity, IEntity toEntity) throws Exception {
        new BeanUtils("id").copyProperties(toEntity, fromEntity);
        return getEntityRepository().save(toEntity);
    }

    default SortingParameters getOrDefault(SortingParameters sortingParameters) {
        return Optional.ofNullable(sortingParameters).
                filter(p -> !p.isEmpty()).
                orElse(new SortingParameters(new SortingParameter("id", SortingParameter.Direction.ASC)));
    }

    default SplitListParameter getOrDefault(SplitListParameter splitListParameter) {
        return Optional.ofNullable(splitListParameter).orElse(new SplitListParameter(getDefaultPageSize(), 0));
    }

    default Sort.Order getSortOrder(SortingParameter sortingParam) {
        assert sortingParam != null;
        Sort.Direction sortDirection = sortingParam.getDirection() == SortingParameter.Direction.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        return new Sort.Order(sortDirection, sortingParam.getProperty());
    }

    default Sort getSort(SortingParameters sortingParams) {
        List<Sort.Order> sortingList = sortingParams.getContent().stream().
                map(this::getSortOrder).
                collect(Collectors.toList());
        Sort.Order sortById = new Sort.Order(Sort.Direction.ASC, "id");
        if (!sortingList.contains(sortById)) {
            sortingList.add(sortById);
        }
        return new Sort(sortingList);
    }

    default Pageable getPageable(SortingParameters sortingParams, SplitListParameter splitListParam) {
        assert sortingParams != null && !sortingParams.isEmpty();
        assert splitListParam != null;
        return new PageRequest(splitListParam.getPageIndex(), splitListParam.getPageSize(), getSort((sortingParams)));
    }

    default SplitList<TP> convert(Page<IEntity> page, SortingParameters sortingParams, SplitListParameter splitListParam) {
        assert sortingParams != null && !sortingParams.isEmpty();
        assert splitListParam != null;
        SplitList<TP> splitList = new SplitList<>();
        splitList.setSort(sortingParams.getContent());
        splitList.setSplitListParam(splitListParam);
        splitList.setTotalElements(page.getTotalElements());
        splitList.setTotalPages(page.getTotalPages());
        splitList.setContent(page.map(IEntity::<TP>convert).getContent());
        return splitList;
    }

    default Specification<IEntity> getFilter(Filters filters) {
        assert filters != null && !filters.isEmpty();
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(filters.getContent().stream().map(filter -> getPredicate(filter, root, criteriaQuery, criteriaBuilder)).toArray(Predicate[]::new));
    }

    default Predicate getPredicate(Filter filter, Root<IEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
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
