package com.example.common.config.query.filter;

import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LongFilter extends RangeFilter<Long> {

    private static final long serialVersionUID = 1L;

    public LongFilter(LongFilter filter) {
        super(filter);
    }

    @Override
    public LongFilter copy() {
        return new LongFilter(this);
    }
}
