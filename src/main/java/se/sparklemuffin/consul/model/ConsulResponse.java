package se.sparklemuffin.consul.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigInteger;

@Getter
@ToString
@EqualsAndHashCode
public class ConsulResponse<T> {

    private final T response;
    private final long lastContact;
    private final boolean knownLeader;
    private final BigInteger index;

    public ConsulResponse(T response, long lastContact, boolean knownLeader, BigInteger index) {
        this.response = response;
        this.lastContact = lastContact;
        this.knownLeader = knownLeader;
        this.index = index;
    }
}