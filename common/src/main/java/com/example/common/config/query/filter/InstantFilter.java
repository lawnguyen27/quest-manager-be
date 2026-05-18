package com.example.common.config.query.filter;

import java.time.Instant;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InstantFilter extends RangeFilter<Instant> {

    private static final long serialVersionUID = 1L;

    public InstantFilter(InstantFilter filter) {
        super(filter);
    }

    @Override
    public InstantFilter copy() {
        return new InstantFilter(this);
    }
}
