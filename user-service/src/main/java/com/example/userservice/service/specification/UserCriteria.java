package com.example.userservice.service.specification;

import java.io.Serializable;

import com.example.common.config.query.filter.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.common.config.query.Criteria;
import com.example.userservice.entity.enums.Gender;

@Data
@NoArgsConstructor
public class UserCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private StringFilter email;
    private LocalDateFilter birthday;
    private StringFilter phone;
    private StringFilter fullname;
    private GenderFilter gender;
    private StringFilter city;
    private StringFilter address;
    private StringFilter roleName;
    private BooleanFilter isActive;

    public UserCriteria(UserCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.email = other.email == null ? null : other.email.copy();
        this.birthday = other.birthday == null ? null : other.birthday.copy();
        this.phone = other.phone == null ? null : other.phone.copy();
        this.fullname = other.fullname == null ? null : other.fullname.copy();
        this.gender = other.gender == null ? null : other.gender.copy();
        this.city = other.city == null ? null : other.city.copy();
        this.address = other.address == null ? null : other.address.copy();
        this.roleName = other.roleName == null ? null : other.roleName.copy();
        this.isActive = other.isActive == null ? null : other.isActive.copy();
    }

    @Override
    public Criteria copy() {
        return new UserCriteria(this);
    }

    public static class GenderFilter extends Filter<Gender> {
        private static final long serialVersionUID = 1L;

        public GenderFilter() {}

        public GenderFilter(GenderFilter other) {
            super(other);
        }

        @Override
        public GenderFilter copy() {
            return new GenderFilter(this);
        }
    }
}
