package br.com.jboard.orchestrator.models;

import br.com.jboard.orchestrator.models.enums.RoleEnum;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class User implements UserDetails {
    private String id;
    private String username;
    private String password;
    private RoleEnum role;

    public User(String username, String password, RoleEnum role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        var freeRole = new SimpleGrantedAuthority("ROLE_FREE");
        var premiumRole = new SimpleGrantedAuthority("ROLE_PREMIUM");

        return this.role == RoleEnum.PREMIUM ? List.of(premiumRole, freeRole) : List.of(freeRole);
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
        return true;
    }
}
