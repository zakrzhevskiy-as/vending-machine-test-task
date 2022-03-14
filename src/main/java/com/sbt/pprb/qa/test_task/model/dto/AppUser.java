package com.sbt.pprb.qa.test_task.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Data
@Entity
@Table(schema = "vending_machine", name = "users")
@SequenceGenerator(name = "default_gen", schema = "vending_machine", sequenceName = "users_id_seq", allocationSize = 1)
public class AppUser extends AuditEntity implements UserDetails {

    @Column(unique = true, nullable = false, updatable = false, length = 32)
    private String username;

    @JsonIgnore
    @ToString.Exclude
    @Column(nullable = false, updatable = false)
    private String password;
    private Boolean enabled;
    private String authority;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_" + authority);
        return Collections.singleton(grantedAuthority);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUser user = (AppUser) o;
        return username.equals(user.username) && enabled.equals(user.enabled) && authority.equals(user.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, enabled, authority);
    }
}
