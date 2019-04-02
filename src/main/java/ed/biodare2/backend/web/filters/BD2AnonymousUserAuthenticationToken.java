/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.filters;

import ed.biodare2.backend.security.BioDare2User;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * It extends UsernamePasswordAuthenticationToken so that default filters will not treat as anonymous.
 * @author tzielins
 */
public class BD2AnonymousUserAuthenticationToken extends UsernamePasswordAuthenticationToken implements Serializable {

    final String remote;
    final BioDare2User user;
    
    BD2AnonymousUserAuthenticationToken(BioDare2User principal,WebAuthenticationDetails details,List<GrantedAuthority> authorities) {
        super(Objects.requireNonNull(principal),"",authorities);
        this.user = principal;
        this.remote = details.getRemoteAddress();
        this.setDetails(details);
    }
    
    public BioDare2User getUser() {
        return this.user;
    }
    
    public String getRemote() {
        return this.remote;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.user.getLogin());
        hash = 67 * hash + Objects.hashCode(this.remote);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BD2AnonymousUserAuthenticationToken other = (BD2AnonymousUserAuthenticationToken) obj;
        if (!Objects.equals(this.user.getLogin(), this.user.getLogin())) {
            return false;
        }
        if (!Objects.equals(this.remote, other.remote)) {
            return false;
        }
        return true;
    }
    
}
