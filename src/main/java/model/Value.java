package model;

import com.google.gson.annotations.SerializedName;

import java.util.Optional;

public abstract class Value {

    @SerializedName("CreateIndex")
    public abstract long getCreateIndex();

    @SerializedName("ModifyIndex")
    public abstract long getModifyIndex();

    @SerializedName("LockIndex")
    public abstract long getLockIndex();

    @SerializedName("Key")
    public abstract String getKey();

    @SerializedName("Flags")
    public abstract long getFlags();

    @SerializedName("Value")
    public abstract Optional<String> getValue();

    @SerializedName("Session")
    public abstract Optional<String> getSession();
}
