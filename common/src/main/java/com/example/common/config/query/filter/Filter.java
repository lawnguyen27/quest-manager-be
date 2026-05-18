package com.example.common.config.query.filter;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Filter<FIELD_TYPE> implements Serializable {

    private static final long serialVersionUID = 1L;

    private FIELD_TYPE equals;
    private FIELD_TYPE notEquals;
    private Boolean specified;
    private List<FIELD_TYPE> in;
    private List<FIELD_TYPE> notIn;

    public Filter(Filter<FIELD_TYPE> filter) {
        this.equals = filter.equals;
        this.notEquals = filter.notEquals;
        this.specified = filter.specified;
        this.in = filter.in;
        this.notIn = filter.notIn;
    }

    public Filter<FIELD_TYPE> copy() {
        return new Filter<>(this);
    }
}
