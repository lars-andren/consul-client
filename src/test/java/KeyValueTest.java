import model.Value;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Ignore
public class KeyValueTest extends BaseTest {

    private static final String KEY = "I AM THE KEY MASTER";
    private static final String VALUE = "ARE YOU THE GATEKEEPER?";

    @Test
    public void put_get() {

        KeyValueClient keyValueClient = consulConnector.getKeyValueClient();

        assertTrue(keyValueClient.putValue(KEY, VALUE));

        Optional<Value> value = keyValueClient.getValue(KEY);

        assertEquals(VALUE, value.get().getValue());
    }
}
