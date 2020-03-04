import org.junit.Before;
import org.junit.Ignore;
import se.cloudcharge.consul.ConsulConnector;

@Ignore
public abstract class BaseTest {

    protected ConsulConnector consulConnector;

    private static final String CONSUL_URL = "http://localhost:8500";

    @Before
    public void setup(){

        consulConnector = ConsulConnector.builder()
            .withUrl(CONSUL_URL)
            .build();
    }
}
