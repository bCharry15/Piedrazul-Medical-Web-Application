package co.edu.unicauca.piedraazul.agenda.identity.internal.adapter.out.security;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.out.EncodePasswordPort;

@Component
public class BCryptPasswordAdapter implements EncodePasswordPort {

    private final BCryptPasswordEncoder passwordEncoder;

    public BCryptPasswordAdapter(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    @Override
    public boolean matches(String plainPassword, String encodedPassword) {
        return passwordEncoder.matches(plainPassword, encodedPassword);
    }
}
