package se.sparklemuffin.consul.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@Builder
public class Check {

    public enum CheckStatus {
        @SerializedName("unknown")
        UNKNOWN,
        @SerializedName("passing")
        PASSING,
        @SerializedName("warning")
        WARNING,
        @SerializedName("critical")
        CRITICAL
    }

    @SerializedName("Node")
    private String node;

    @SerializedName("CheckID")
    private String id;

    @SerializedName("Name")
    private String name;

    @SerializedName("CheckStatus")
    private CheckStatus checkStatus;

    @SerializedName("Notes")
    private String notes;

    @SerializedName("Output")
    private String output;

    @SerializedName("ServiceID")
    private String serviceId;

    @SerializedName("ServiceName")
    private String serviceName;

    @SerializedName("ServiceTags")
    private List<String> serviceTags;

    @SerializedName("CreateIndex")
    private Long createIndex;

    @SerializedName("ModifyIndex")
    private Long modifyIndex;

    @SerializedName("Script")
    private String script;

    @SerializedName("DockerContainerID")
    private String dockerContainerID;

    @SerializedName("Shell")
    private String shell;

    @SerializedName("Interval")
    private String interval;

    @SerializedName("TTL")
    private String ttl;

    @SerializedName("HTTP")
    private String http;

    @SerializedName("Method")
    private String method;

    @SerializedName("Header")
    private Map<String, List<String>> header;

    @SerializedName("TCP")
    private String tcp;

    @SerializedName("Timeout")
    private String timeout;

    @SerializedName("DeregisterCriticalServiceAfter")
    private String deregisterCriticalServiceAfter;

    @SerializedName("TLSSkipVerify")
    private Boolean tlsSkipVerify;

    @SerializedName("Status")
    private String status;

    @SerializedName("GRPC")
    private String grpc;

    @SerializedName("GRPCUseTLS")
    private Boolean grpcUseTLS;

}