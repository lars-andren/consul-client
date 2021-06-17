package se.sparklemuffin.consul.transport;

public interface Callback<T> {

    void onResponse(T result);

    void onFailure(Throwable t);
}