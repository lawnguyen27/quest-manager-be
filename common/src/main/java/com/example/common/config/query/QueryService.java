package com.example.common.config.query;

import java.util.function.Function;
import org.springframework.data.jpa.domain.Specification;
import com.example.common.config.query.filter.BooleanFilter;
import com.example.common.config.query.filter.Filter;
import com.example.common.config.query.filter.RangeFilter;
import com.example.common.config.query.filter.StringFilter;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.persistence.metamodel.SingularAttribute;

public abstract class QueryService<ENTITY> {

    protected <X> Specification<ENTITY> buildSpecification(Filter<X> filter, SingularAttribute<? super ENTITY, X> field) {
        return buildSpecificationByFunction(filter, (jakarta.persistence.criteria.Root<ENTITY> root) -> root.get(field));
    }

    protected <X> Specification<ENTITY> buildSpecificationByFunction(Filter<X> filter, Function<jakarta.persistence.criteria.Root<ENTITY>, jakarta.persistence.criteria.Expression<X>> metamodelFunction) {
        if (filter.getEquals() != null) {
            return (root, query, builder) -> builder.equal(metamodelFunction.apply(root), filter.getEquals());
        }
        if (filter.getNotEquals() != null) {
            return (root, query, builder) -> builder.notEqual(metamodelFunction.apply(root), filter.getNotEquals());
        }
        if (filter.getIn() != null && !filter.getIn().isEmpty()) {
            return (root, query, builder) -> {
                In<X> in = builder.in(metamodelFunction.apply(root));
                for (X value : filter.getIn()) {
                    in.value(value);
                }
                return in;
            };
        }
        if (filter.getSpecified() != null) {
            return (root, query, builder) -> filter.getSpecified() ? builder.isNotNull(metamodelFunction.apply(root)) : builder.isNull(metamodelFunction.apply(root));
        }
        return null;
    }

    protected Specification<ENTITY> buildStringSpecification(StringFilter filter, SingularAttribute<? super ENTITY, String> field) {
        return buildStringSpecificationByFunction(filter, (jakarta.persistence.criteria.Root<ENTITY> root) -> root.get(field));
    }

    protected Specification<ENTITY> buildStringSpecificationByFunction(StringFilter filter, Function<jakarta.persistence.criteria.Root<ENTITY>, jakarta.persistence.criteria.Expression<String>> metamodelFunction) {
        Specification<ENTITY> spec = buildSpecificationByFunction(filter, metamodelFunction);
        if (spec != null) return spec;

        if (filter.getContains() != null) {
            return (root, query, builder) -> builder.like(builder.lower(metamodelFunction.apply(root)), "%" + filter.getContains().toLowerCase() + "%");
        }
        return null;
    }

    protected <X extends Comparable<? super X>> Specification<ENTITY> buildRangeSpecification(RangeFilter<X> filter, SingularAttribute<? super ENTITY, X> field) {
        return buildRangeSpecificationByFunction(filter, (jakarta.persistence.criteria.Root<ENTITY> root) -> root.get(field));
    }

    protected <X extends Comparable<? super X>> Specification<ENTITY> buildRangeSpecificationByFunction(RangeFilter<X> filter, Function<jakarta.persistence.criteria.Root<ENTITY>, jakarta.persistence.criteria.Expression<X>> metamodelFunction) {
        Specification<ENTITY> spec = buildSpecificationByFunction(filter, metamodelFunction);
        if (spec != null) return spec;

        if (filter.getGreaterThan() != null) {
            return (root, query, builder) -> builder.greaterThan(metamodelFunction.apply(root), filter.getGreaterThan());
        }
        if (filter.getGreaterThanOrEqual() != null) {
            return (root, query, builder) -> builder.greaterThanOrEqualTo(metamodelFunction.apply(root), filter.getGreaterThanOrEqual());
        }
        if (filter.getLessThan() != null) {
            return (root, query, builder) -> builder.lessThan(metamodelFunction.apply(root), filter.getLessThan());
        }
        if (filter.getLessThanOrEqual() != null) {
            return (root, query, builder) -> builder.lessThanOrEqualTo(metamodelFunction.apply(root), filter.getLessThanOrEqual());
        }
        return null;
    }

    protected Specification<ENTITY> buildSpecification(BooleanFilter filter, SingularAttribute<? super ENTITY, Boolean> field) {
        return buildSpecificationByFunction(filter, (jakarta.persistence.criteria.Root<ENTITY> root) -> root.get(field));
    }
}
