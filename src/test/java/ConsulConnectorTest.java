import model.Service;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ConsulConnectorTest {

    private static final String CONSUL_URL = "http://localhost:8500";
    private static final String SERVICE_ID = "serviceID";
    private static final String SERVICE_NAME = "The Mer-Man service";

    @Test
    public void createConsulConnector() {

        ConsulConnector consulConnector = ConsulConnector.builder()
                .withUrl(CONSUL_URL)
                .build();

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
}
