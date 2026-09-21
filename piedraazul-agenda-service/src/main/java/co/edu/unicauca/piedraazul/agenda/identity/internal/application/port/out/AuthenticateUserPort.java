package co.edu.unicauca.piedraazul.agenda.identity.internal.application.port.out;
import java.util.Map;

public interface AuthenticateUserPort {

    Map<String, Object> getToken(String username, String password);
}
