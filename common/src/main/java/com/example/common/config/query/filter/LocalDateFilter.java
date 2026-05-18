package com.example.common.config.query.filter;

import java.time.LocalDate;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LocalDateFilter extends RangeFilter<LocalDate> {

    private static final long serialVersionUID = 1L;

    public LocalDateFilter(LocalDateFilter filter) {
        super(filter);
    }

    @Override
    public LocalDateFilter copy() {
        return new LocalDateFilter(this);
    }
}
