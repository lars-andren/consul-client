package se.sparklemuffin.consul;

import se.sparklemuffin.consul.cache.CacheConfig;
import se.sparklemuffin.consul.monitoring.ClientEventCallback;
import se.sparklemuffin.consul.monitoring.ClientEventHandler;
import se.sparklemuffin.consul.transport.Http;

public class BaseClient {

    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 8500;
    public static final String DEFAULT_PATH = "";

    private final ClientEventHandler eventHandler;
    private final CacheConfig cacheConfig;
    protected final Http http;

    protected BaseClient(String name, CacheConfig cacheConfig, ClientEventCallback eventCallback) {
        this.eventHandler = new ClientEventHandler(name, eventCallback);
        this.http = new Http(eventHandler);
        this.cacheConfig = cacheConfig;
    }
}

