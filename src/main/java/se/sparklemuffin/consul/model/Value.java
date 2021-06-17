package se.sparklemuffin.consul.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class Value {

    @SerializedName("CreateIndex")
    public long createIndex;

    @SerializedName("ModifyIndex")
    public long modifyIndex;

    @SerializedName("LockIndex")
    public long lockIndex;

    @SerializedName("Key")
    public String key;

    @SerializedName("Flags")
    public long flags;

    @SerializedName("Value")
    public String value;

    @SerializedName("Session")
    public String session;
}
