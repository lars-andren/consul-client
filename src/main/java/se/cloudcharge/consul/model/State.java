package se.cloudcharge.consul.model;

import lombok.Getter;

@Getter
public enum State {

    PASS("pass", "passing"),
    WARN("warn", "warning"),
    FAIL("fail", "critical"),
    ANY("any", "any"),
    UNKNOWN("unknown", "unknown");

    private final String path;
    private final String name;


    State(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public static State fromName(String name) {
        for(State state : values()) {
            if(state.getName().equals(name)) {
                return state;
            }
        }

        throw new IllegalArgumentException("Invalid State name");
    }
}
