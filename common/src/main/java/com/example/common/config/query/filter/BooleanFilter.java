package com.example.common.config.query.filter;

import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BooleanFilter extends Filter<Boolean> {

    private static final long serialVersionUID = 1L;

    public BooleanFilter(BooleanFilter filter) {
        super(filter);
    }

    @Override
    public BooleanFilter copy() {
        return new BooleanFilter(this);
    }
}
