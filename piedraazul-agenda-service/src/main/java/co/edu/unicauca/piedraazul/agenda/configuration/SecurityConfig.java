package co.edu.unicauca.piedraazul.agenda.configuration;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.DispatcherType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                .csrf(csrf ->
                        csrf.disable()
                )

                .authorizeHttpRequests(auth -> auth

                        /*
                         * Permitir despachos internos de errores.
                         *
                         * Evita que un 400/409/500 real sea
                         * reemplazado por un 401 de Spring Security.
                         */
                        .dispatcherTypeMatchers(
                                DispatcherType.ERROR
                        )
                        .permitAll()

                        /*
                         * Preflight CORS.
                         */
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        )
                        .permitAll()

                        // Public endpoints
.requestMatchers(
        "/api/agenda/health",
        "/api/auth/login",
        "/api/auth/forgot-password",
        "/api/auth/reset-password",
        "/api/public/patients/register",
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/**"
)
.permitAll()

// Administration
.requestMatchers(
        HttpMethod.POST,
        "/api/auth/register",
        "/api/auth/admin/schedulers"
)
.hasRole("ADMIN")

.requestMatchers(
        HttpMethod.GET,
        "/api/auth/users/role/**"
)
.hasRole("ADMIN")

                        /*
                         * Doctors.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/doctors/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "SCHEDULER",
                                "PATIENT",
                                "DOCTOR"
                        )

                        .requestMatchers(
                                "/api/doctors/**"
                        )
                        .hasRole("ADMIN")

                        /*
                         * Availability configuration.
                         */
                        .requestMatchers(
                                "/api/availability-configurations/**",
                                "/api/configurations-availability/**"
                        )
                        .hasRole("ADMIN")

                        /*
                         * Appointment export.
                         */
                        .requestMatchers(
                                "/api/appointments/export",
                                "/api/appointments/exportar"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "SCHEDULER",
                                "DOCTOR"
                        )

                        /*
                         * Appointments.
                         */
                        .requestMatchers(
                                "/api/appointments/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "SCHEDULER",
                                "PATIENT",
                                "DOCTOR"
                        )

                        /*
                         * Availability queries.
                         */
                        .requestMatchers(
                                "/api/availability/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "SCHEDULER",
                                "PATIENT",
                                "DOCTOR"
                        )

                        .anyRequest()
                        .authenticated()
                )

                .oauth2ResourceServer(
                        oauth2 ->
                                oauth2.jwt(
                                        jwt ->
                                                jwt.jwtAuthenticationConverter(
                                                        jwtAuthenticationConverter()
                                                )
                                )
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource
    corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:4200"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept",
                        "Origin",
                        "X-Requested-With"
                )
        );

        configuration.setExposedHeaders(
                List.of(
                        "Content-Disposition"
                )
        );

        configuration.setAllowCredentials(
                true
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    private Converter<
            Jwt,
            ? extends AbstractAuthenticationToken
            >
    jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                this::extractRolesFromKeycloak
        );

        return converter;
    }

    private Collection<GrantedAuthority>
    extractRolesFromKeycloak(
            Jwt jwt
    ) {

        Map<String, Object> realmAccess =
                jwt.getClaim(
                        "realm_access"
                );

        if (
                realmAccess == null
                        ||
                !realmAccess.containsKey(
                        "roles"
                )
        ) {

            return List.of();
        }

        @SuppressWarnings("unchecked")
        Collection<String> roles =
                (Collection<String>)
                        realmAccess.get(
                                "roles"
                        );

        return roles
                .stream()
                .map(
                        role ->
                                new SimpleGrantedAuthority(
                                        "ROLE_" + role
                                )
                )
                .collect(
                        Collectors.toList()
                );
    }
}