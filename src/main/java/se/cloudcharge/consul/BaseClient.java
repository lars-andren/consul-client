package se.cloudcharge.consul;

import se.cloudcharge.consul.cache.CacheConfig;
import se.cloudcharge.consul.monitoring.ClientEventCallback;
import se.cloudcharge.consul.monitoring.ClientEventHandler;
import se.cloudcharge.consul.transport.Http;

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

