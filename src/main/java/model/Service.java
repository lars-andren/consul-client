package model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@ToString
@Builder
public class Service {

    @SerializedName("ID")
    private String id;

    @SerializedName("Name")
    private String name;

    @SerializedName("Service")
    private String service;

    @SerializedName("Tags")
    private List<String> tags;

    @SerializedName("Address")
    private String address;

    @SerializedName("Meta")
    private Map<String, String> meta;

    @SerializedName("Port")
    private Integer port;

    @SerializedName("EnableTagOverride")
    private Boolean enableTagOverride;

    @SerializedName("CreateIndex")
    private Long createIndex;

    @SerializedName("ModifyIndex")
    private Long modifyIndex;

    @SerializedName("Check")
    private Check check;
}
