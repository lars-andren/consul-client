package se.cloudcharge.consul.cache;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CacheDescriptor {

    private final String endpoint;
    private final String key;

    public CacheDescriptor(String endpoint) {
        this(endpoint, null);
    }

    public CacheDescriptor(String endpoint, String key) {
        this.endpoint = endpoint;
        this.key = key;
    }

}