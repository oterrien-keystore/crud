package com.ote.crud;

import com.ote.crud.exception.CreateException;
import com.ote.crud.exception.MergeException;
import com.ote.crud.exception.ResetException;
import com.ote.crud.model.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

@RequiredArgsConstructor
public abstract class AdtPersistenceService<TP extends IPayload, TE extends IEntity> implements IPersistenceService<TP> {

    protected final IEntityRepository<TE> entityRepository;

    protected final IMapperService<TP, TE> mapperService;

    protected final String entityName;

    @Value("${page.default.size}")
    @Getter
    private int defaultPageSize;

    @Override
    public Optional<TP> findOne(long id) {
        return Optional.ofNullable(entityRepository.findOne(id)).
                map(mapperService::convert);
    }

    @Override
    public List<TP> findMany(Filters filters) {
        List<TE> list;
        if (filters == null || filters.isEmpty()) {
            list = entityRepository.findAll();
        } else {
            Specification<TE> filterSpecification = getFilter(filters);
            list = entityRepository.findAll(filterSpecification);
        }
        return list.stream().map(mapperService::convert).collect(Collectors.toList());
    }

    @Override
    public SplitList<TP> findMany(Filters filters, SortingParameters sortingParam, SplitListParameter splitListParam) {
        sortingParam = getOrDefault(sortingParam);
        splitListParam = getOrDefault(splitListParam);
        Pageable pageable = getPageable(sortingParam, splitListParam);
        Page<TE> page;
        if (filters == null || filters.isEmpty()) {
            page = entityRepository.findAll(pageable);
        } else {
            Specification<TE> filterSpecification = getFilter(filters);
            page = entityRepository.findAll(filterSpecification, pageable);
        }
        return convert(page, sortingParam, splitListParam);
    }

    @Override
    public TP create(TP payload) throws CreateException {
        try {
            payload.setId(0);
            TE entity = mapperService.convert(payload);
            return mapperService.convert(entityRepository.save(entity));
        } catch (Exception e) {
            throw new CreateException(entityName, e);
        }
    }

    @Override
    public Optional<TP> reset(long id, TP payload) throws ResetException {
        try {
            if (entityRepository.exists(id)) {
                TE entity = mapperService.convert(payload);
                entity.setId(id);
                return Optional.of(entityRepository.save(entity)).map(mapperService::convert);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new ResetException(entityName, id, e);
        }
    }

    @Override
    public Optional<TP> merge(long id, TP payload) throws MergeException {
        try {
            Optional<TE> currentEntity = Optional.ofNullable(entityRepository.findOne(id));
            if (currentEntity.isPresent()) {
                TE entity = mapperService.convert(payload);
                return Optional.of(merge(entity, currentEntity.get())).map(mapperService::convert);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new MergeException(entityName, id, e);
        }
    }

    @Override
    public void deleteOne(long id) {
        if (entityRepository.exists(id)) {
            entityRepository.delete(id);
        }
    }

    @Override
    public void deleteMany(Filters filters) {
        if (filters == null || filters.isEmpty()) {
            entityRepository.deleteAll();
        } else {
            findMany(filters).parallelStream().forEach(p -> deleteOne(p.getId()));
        }
    }

    private TE merge(TE fromEntity, TE toEntity) throws Exception {
        new NullAwareBeanUtilsBean("id").copyProperties(toEntity, fromEntity);
        return entityRepository.save(toEntity);
    }

    private SortingParameters getOrDefault(SortingParameters sortingParameters) {
        return Optional.ofNullable(sortingParameters).
                filter(p -> !p.isEmpty()).
                orElse(new SortingParameters(new SortingParameter("id", SortingParameter.Direction.ASC)));
    }

    private SplitListParameter getOrDefault(SplitListParameter splitListParameter) {
        return Optional.ofNullable(splitListParameter).orElse(new SplitListParameter(defaultPageSize, 0));
    }

    protected Sort.Order getSortOrder(SortingParameter sortingParam) {
        assert sortingParam != null;

        Sort.Direction sortDirection = sortingParam.getDirection() == SortingParameter.Direction.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        return new Sort.Order(sortDirection, sortingParam.getProperty());
    }

    protected Sort getSort(SortingParameters sortingParams) {
        assert sortingParams != null && !sortingParams.isEmpty();

        List<Sort.Order> sortingList = sortingParams.getContent().stream().
                map(this::getSortOrder).
                collect(Collectors.toList());
        Sort.Order sortById = new Sort.Order(Sort.Direction.ASC, "id");
        if (!sortingList.contains(sortById)) {
            sortingList.add(sortById);
        }
        return new Sort(sortingList);
    }

    protected Pageable getPageable(SortingParameters sortingParams, SplitListParameter splitListParam) {
        assert sortingParams != null && !sortingParams.isEmpty();
        assert splitListParam != null;

        return new PageRequest(splitListParam.getPageIndex(), splitListParam.getPageSize(), getSort((sortingParams)));
    }

    protected SplitList<TP> convert(Page<TE> page, SortingParameters sortingParams, SplitListParameter splitListParam) {
        assert sortingParams != null && !sortingParams.isEmpty();
        assert splitListParam != null;

        SplitList<TP> splitList = new SplitList<>();
        splitList.setSort(sortingParams.getContent());
        splitList.setSplitListParam(splitListParam);
        splitList.setTotalElements(page.getTotalElements());
        splitList.setTotalPages(page.getTotalPages());
        splitList.setContent(page.map(mapperService::convert).getContent());
        return splitList;
    }

    protected Specification<TE> getFilter(Filters filters) {
        assert filters != null && !filters.isEmpty();

        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(filters.getContent().stream().map(filter -> getPredicate(filter, root, criteriaQuery, criteriaBuilder)).toArray(Predicate[]::new));
    }

    protected Predicate getPredicate(Filter filter, Root<TE> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

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
