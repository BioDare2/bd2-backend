package ed.biodare2.backend.web.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextRepository;

public class SessionSavingBasicAuthenticationFilter extends BasicAuthenticationFilter {

    private final SecurityContextRepository securityContextRepository;

    public SessionSavingBasicAuthenticationFilter(
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository) {
        super(authenticationManager);
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    protected void onSuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.core.Authentication authResult) throws IOException {

        super.onSuccessfulAuthentication(request, response, authResult);

        SecurityContext context = SecurityContextHolder.getContext();
        this.securityContextRepository.saveContext(context, request, response);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        super.doFilterInternal(request, response, chain);
    }
}
