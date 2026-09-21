package co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.out;
public interface EncodePasswordPort {

    String encode(String plainPassword);

    boolean matches(String plainPassword, String encodedPassword);
}
