import model.Check;
import model.Service;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@Ignore
public class AgentClientTest extends BaseTest {

    private static final String SERVICE_ID = "serviceID";
    private static final String SERVICE_NAME = "The Mer-Man service";
    private static final String CHECK_ID = "IZ SO MANY NAME";

    @Test
    public void agent_register_get() {

        AgentClient agentClient = consulConnector.getAgentClient();

        Service service = Service.builder()
                .id(SERVICE_ID)
                .port(1)
                .name(SERVICE_NAME)
                .build();

        agentClient.register(service);

        Map<String, Service> services = agentClient.getServices();

        assertEquals(SERVICE_NAME, services.get(SERVICE_ID).getService());
    }

    @Test
    public void add_check() {

        AgentClient agentClient = consulConnector.getAgentClient();
        HealthClient healthClient = consulConnector.getHealthClient();

        Check check = Check.builder()
                .ttl("10s")
                .id(CHECK_ID)
                .build();

        Service service = Service.builder()
                .id(SERVICE_ID)
                .port(1)
                .name(SERVICE_NAME)
                .check(check)
                .build();

        agentClient.register(service);

        List<Check> checks = healthClient.getServiceChecks(SERVICE_NAME).getResponse();

        assertEquals(CHECK_ID, checks.get(0).getId());
    }
}
